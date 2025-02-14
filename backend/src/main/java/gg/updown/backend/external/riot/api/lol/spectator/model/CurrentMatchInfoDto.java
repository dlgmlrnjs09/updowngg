package gg.updown.backend.external.riot.api.lol.spectator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "현재 게임의 상세 정보를 담는 데이터 전송 객체")
public class CurrentMatchInfoDto {
    @Schema(description = "게임의 고유 식별 번호", example = "1234567890")
    private long gameId;

    @Schema(description = "게임 유형", example = "랭크 솔로 5대5")
    private String gameType;

    @Schema(description = "게임 시작 시간 (밀리초 단위의 에포크 시간)", example = "1623456789000")
    private long gameStartTime;

    @Schema(description = "게임이 진행되는 맵의 고유 식별 번호", example = "11")
    private long mapId;

    @Schema(description = "게임 시작 후 경과된 시간 (초 단위)", example = "300")
    private long gameLength;

    @Schema(description = "게임이 진행되는 플랫폼 식별자", example = "KR")
    private String platformId;

    @Schema(description = "게임 모드", example = "클래식")
    private String gameMode;

    @Schema(description = "밴된 챔피언 목록")
    private List<BannedChampionDto> bannedChampions;

    @Schema(description = "게임 큐 타입 설정 식별 번호", example = "420")
    private long gameQueueConfigId;

    @Schema(description = "게임 관전자 정보")
    private ObserverDto observers;

    @Schema(description = "게임 참가자 목록")
    private List<CurrentMatchParticipantDto> participants;
}