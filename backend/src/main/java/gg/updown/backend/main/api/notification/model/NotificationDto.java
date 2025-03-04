package gg.updown.backend.main.api.notification.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private long notificationSeq;
    private String notificationId;
    private String notificationType;
    private long subSeq;
    private long targetSiteCode;
    private String targetPuuid;
    private String iconUrl;
    private String content;
    private boolean readYn;
    private LocalDateTime readDt;
    private LocalDateTime regDt;
    private LocalDateTime actionDt;
}
