package gg.updown.backend.main.api.notification.service;

import gg.updown.backend.main.api.notification.mapper.NotificationMapper;
import gg.updown.backend.main.api.notification.model.NotificationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationMapper notificationMapper;

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

    public List<NotificationEntity> getNotifications(long siteCode) {
        return notificationMapper.getNotifications(siteCode);
    }
}
