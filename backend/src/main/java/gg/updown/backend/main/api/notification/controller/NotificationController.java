package gg.updown.backend.main.api.notification.controller;


import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import gg.updown.backend.main.api.auth.service.JwtTokenProvider;
import gg.updown.backend.main.api.notification.model.NotificationDto;
import gg.updown.backend.main.api.notification.service.NotificationService;
import gg.updown.backend.main.enums.TokenStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/subscribe")
    public ResponseEntity<SseEmitter> subscribe(@RequestParam String token) {
        // 토큰 유효성 검증
        TokenStatus tokenStatus = jwtTokenProvider.validateToken(token);
        if (tokenStatus != TokenStatus.VALID) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 토큰에서 Authentication 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailImpl userDetail)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // UserDetailImpl에서 puuid 가져오기
        String puuid = userDetail.getPuuid();

        // SSE Emitter 생성
        SseEmitter emitter = notificationService.subscribe(puuid);

        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .body(emitter);
    }

    @GetMapping("/list")
    public List<NotificationDto> getNotification(@AuthenticationPrincipal UserDetails user) {
        if (user != null) {
            return notificationService.getNotifications(((UserDetailImpl) user).getPuuid());
        }

        return Collections.emptyList();
    }

    @PostMapping("/read/{notificationId}")
    public void readNotification(@PathVariable String notificationId, @AuthenticationPrincipal UserDetails user) {
        if (user != null) {
            notificationService.readNotification(notificationId, ((UserDetailImpl) user).getPuuid());
        }
    }

    @PostMapping("/read/all")
    public void readAllNotification(@RequestBody List<String> notificationIds, @AuthenticationPrincipal UserDetails user) {
        if (user != null) {
            notificationService.readNotifications(notificationIds, ((UserDetailImpl) user).getPuuid());
        }
    }
}
