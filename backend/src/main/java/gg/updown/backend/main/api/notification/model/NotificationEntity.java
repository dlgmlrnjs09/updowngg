package gg.updown.backend.main.api.notification.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {
    private long notificationSeq;
    private String notificationId;
    private String notificationType;
    private long targetSiteCode;
    private String content;
    private boolean readYn;
    private LocalDateTime readDt;
    private LocalDateTime regDt;
}
