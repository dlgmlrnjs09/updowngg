package gg.updown.backend.main.api.lol.match.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "LOL 경기정보 DTO")
public class LolMatchInfoDto {
    @Schema(description = "LOL 경기 고유ID")
    private String matchId;
    @Schema(description = "게임 진행시간(sec)")
    private Long gameDuration;
    @Schema(description = "게임 모드(ARAM 등)")
    private String gameMode;
    @Schema(description = "게임 모드 고유ID")
    private String gameModeName;
    @Schema(description = "게임 시작일시")
    private LocalDateTime gameStartDt;
    @Schema(description = "게임 유형")
    private String gameType;
    @Schema(description = "큐 ID (450: 칼바람, 430: 일반게임, 420: 개인랭크, 440: 자유랭크)")
    private int queueId;
}
