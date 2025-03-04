package gg.updown.backend.main.api.notification.service;

import gg.updown.backend.main.api.notification.mapper.NotificationMapper;
import gg.updown.backend.main.api.notification.model.NotificationDto;
import gg.updown.backend.main.api.notification.model.NotificationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationMapper notificationMapper;

    @Value("${riot-api.latest-version}")
    private String latestVersion;

    public SseEmitter subscribe(String memberPuuid) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(memberPuuid, emitter);

        emitter.onCompletion(() -> emitters.remove(memberPuuid));
        emitter.onTimeout(() -> emitters.remove(memberPuuid));

        return emitter;
    }

    public void notify(NotificationEntity notificationEntity) {
        SseEmitter emitter = emitters.get(notificationEntity.getTargetPuuid());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .data(notificationEntity)
                        .name("review-notification"));

                notificationEntity.setNotificationType("SITE_REVIEW");
                notificationMapper.insertNotification(notificationEntity);
            } catch (IOException e) {
                emitters.remove(notificationEntity.getTargetPuuid());
            }
        }
    }

    public void notify(NotificationDto notificationDto) {
        notificationDto.setNotificationType(notificationDto.getNotificationType());
        notificationDto.setNotificationId(UUID.randomUUID().toString());
        NotificationEntity notificationEntity = new NotificationEntity();
        BeanUtils.copyProperties(notificationDto, notificationEntity);
        notificationMapper.insertNotification(notificationEntity);

        SseEmitter emitter = emitters.get(notificationDto.getTargetPuuid());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .data(notificationDto)
                        .name(notificationDto.getNotificationType()));
            } catch (IOException e) {
                emitters.remove(notificationDto.getTargetPuuid());
            }
        }
    }

    public List<NotificationDto> getNotifications(String puuid) {
        List<NotificationEntity> list = notificationMapper.getNotifications(puuid);

        return list.stream()
                .map(dto -> {
                    NotificationDto entity = new NotificationDto();
                    BeanUtils.copyProperties(dto, entity);
                    return entity;
                })
                .toList();
    }

    public void readNotification(String id, String puuid) {
        notificationMapper.readNotification(id, puuid);
    }

    public void readNotifications(List<String> ids, String puuid) {
        notificationMapper.readNotifications(ids, puuid);
    }
}
