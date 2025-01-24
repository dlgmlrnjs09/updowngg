package gg.updown.backend.main.api.notification.service;

import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationMapper notificationMapper;

    @Value("${riot-api.latest-version}")
    private String latestVersion;

    public SseEmitter subscribe(long memberSiteCode) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(memberSiteCode, emitter);

        emitter.onCompletion(() -> emitters.remove(memberSiteCode));
        emitter.onTimeout(() -> emitters.remove(memberSiteCode));

        return emitter;
    }

    public void notify(NotificationEntity notificationEntity) {
        SseEmitter emitter = emitters.get(notificationEntity.getTargetSiteCode());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .data(notificationEntity)
                        .name("review-notification"));

                notificationEntity.setNotificationType("SITE_REVIEW");
                notificationMapper.insertNotification(notificationEntity);
            } catch (IOException e) {
                emitters.remove(notificationEntity.getTargetSiteCode());
            }
        }
    }

    public void notify(NotificationDto notificationDto) {
        SseEmitter emitter = emitters.get(notificationDto.getTargetSiteCode());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .data(notificationDto)
                        .name("review-notification"));

                notificationDto.setNotificationType("SITE_REVIEW");
                NotificationEntity notificationEntity = new NotificationEntity();
                BeanUtils.copyProperties(notificationDto, notificationEntity);
                notificationMapper.insertNotification(notificationEntity);
            } catch (IOException e) {
                emitters.remove(notificationDto.getTargetSiteCode());
            }
        }
    }

    public List<NotificationDto> getNotifications(long siteCode) {
        List<NotificationEntity> list = notificationMapper.getNotifications(siteCode);

        return list.stream()
                .map(dto -> {
                    NotificationDto entity = new NotificationDto();
                    BeanUtils.copyProperties(dto, entity);
                    entity.setChampionIconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, dto.getChampionName()));
                    return entity;
                })
                .toList();
    }

    public void readNotification(String id, long siteCode) {
        notificationMapper.readNotification(id, siteCode);
    }

    public void readNotifications(List<String> ids, long siteCode) {
        notificationMapper.readNotifications(ids, siteCode);
    }
}
