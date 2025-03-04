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
    private long subSeq;
    private long targetSiteCode;
    private String targetPuuid;
    private String content;
    private String iconUrl;
    private boolean readYn;
    private LocalDateTime readDt;
    private LocalDateTime regDt;
    private LocalDateTime actionDt;
}
