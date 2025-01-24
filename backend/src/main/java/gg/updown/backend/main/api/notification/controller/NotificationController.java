package gg.updown.backend.main.api.notification.controller;


import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import gg.updown.backend.main.api.notification.model.NotificationDto;
import gg.updown.backend.main.api.notification.model.NotificationEntity;
import gg.updown.backend.main.api.notification.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails user) {
        if (user != null) {
            return notificationService.subscribe(((UserDetailImpl) user).getSiteCode());
        }

        return null;
    }

    @GetMapping("/list")
    public List<NotificationDto> getNotification(@AuthenticationPrincipal UserDetails user) {
        if (user != null) {
            return notificationService.getNotifications(((UserDetailImpl) user).getSiteCode());
        }

        return null;
    }

    @PostMapping("/read/{notificationId}")
    public void readNotification(@PathVariable String notificationId, @AuthenticationPrincipal UserDetails user) {
        if (user != null) {
            notificationService.readNotification(notificationId, ((UserDetailImpl) user).getSiteCode());
        }
    }

    @PostMapping("/read/all")
    public void readAllNotification(@RequestBody List<String> notificationIds, @AuthenticationPrincipal UserDetails user) {
        if (user != null) {
            notificationService.readNotifications(notificationIds, ((UserDetailImpl) user).getSiteCode());
        }
    }
}
