package gg.updown.backend.main.api.lol.match.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolMatchInfoDto {
    private String matchId;
    private Long gameDuration;
    private String gameMode;
    private String gameModeName;
    private LocalDateTime gameStartDt;
    private String gameType;
    private int queueId;
}
