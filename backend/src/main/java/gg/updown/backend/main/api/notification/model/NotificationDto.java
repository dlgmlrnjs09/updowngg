package gg.updown.backend.main.api.notification.model;

import jakarta.annotation.security.DenyAll;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private long notificationSeq;
    private String notificationId;
    private String notificationType;
    private long targetSiteCode;
    private String content;
    private boolean readYn;
    private String championName;
    private LocalDateTime readDt;
    private LocalDateTime regDt;
    private String championIconUrl;
    private LocalDateTime gameCreateDt;
    private String gameModeName;
    private long reviewSeq;
}
