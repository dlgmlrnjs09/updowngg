package gg.updown.backend.main.api.lol.match.model.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolMatchEntity {
    private String matchId;
    private int lolMatchSeq;
    private LocalDateTime gameCreateDt;
    private Long gameDuration;
    private Long gameId;
    private String gameMode;
    private String gameName;
    private int queueId;
    private LocalDateTime gameStartDt;
    private String gameType;
    private String gameVersion;
    private int mapId;
}
