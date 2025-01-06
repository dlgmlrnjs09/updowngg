package gg.updown.backend.main.api.lol.summoner.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "LOL 소환사 랭크정보 DTO")
public class LolSummonerLeagueEntity {
    @Schema(description = "LOL 리그 고유 ID")
    private String leagueId;
    @Schema(description = "Riot 소환사 고유 ID")
    private String summonerId;
    @Schema(description = "큐 타입", allowableValues = {"RANKED_SOLO_5x5", "RANKED_FLEX_SR"})
    private String queueType;
    @Schema(description = "랭크 티어", allowableValues = {"IRON", "BRONZE", "SILVER", "GOLD", "PLATINUM", "EMERALD", "DIAMOND", "MASTER", "GRANDMASTER", "CHALLENGER"})
    private String tier;
    @Schema(description = "랭크 단계", allowableValues = {"I", "II", "III", "IV"})
    private String rank;
    @Schema(description = "랭크 점수")
    private Integer leaguePoints;
    @Schema(description = "승리 수")
    private Integer wins;
    @Schema(description = "패배 수")
    private Integer losses;
    @Schema(description = "연승 여부")
    private Boolean hotStreak;
    @Schema(description = "베테랑 여부(해당 티어에서 100게임 이상 플레이)")
    private Boolean veteran;
    @Schema(description = "신규 진입 여부 - 해당 티어 신규 진입")
    private Boolean freshBlood;
    @Schema(description = "휴면 계정 여부")
    private Boolean inactive;
    @Schema(description = "등록일")
    private LocalDateTime regDt;
    @Schema(description = "수정일")
    private LocalDateTime updDt;
}
