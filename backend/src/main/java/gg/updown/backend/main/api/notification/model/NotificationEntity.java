package gg.updown.backend.main.api.notification.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

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
    private String championName;
    private LocalDateTime readDt;
    private LocalDateTime regDt;
    private LocalDateTime gameCreateDt;
    private String gameModeName;
    private long reviewSeq;
}
