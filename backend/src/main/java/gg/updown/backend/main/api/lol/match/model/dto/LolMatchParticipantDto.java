package gg.updown.backend.main.api.lol.match.model.dto;

import gg.updown.backend.main.api.review.model.dto.ReviewDto;
import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "LOL 참여자/리뷰정보 DTO")
public class LolMatchParticipantDto {
    @Schema(description = "LOL 경기 고유ID")
    private String matchId;
    @Schema(description = "라이엇 계정 고유ID")
    private String puuid;
    @Schema(description = "라이엇 닉네임")
    private String riotIdGameName;
    @Schema(description = "라이엇 배틀태그")
    private String riotIdTagline;
    @Schema(description = "포지션")
    private String position;
    @Schema(description = "100: 블루팀, 200: 레드팀")
    private int teamId;
    @Schema(description = "팀명(블루팀, 레드팀)")
    private String teamName;
    @Schema(description = "처치 수")
    private int kills;
    @Schema(description = "어시스트 수")
    private int assists;
    @Schema(description = "죽음 수")
    private int deaths;
    @Schema(description = "챔피언 레벨")
    private int champLevel;
    @Schema(description = "챔피언 고유ID")
    private int champId;
    @Schema(description = "챔피언 명(영문)")
    private String champName;
    @Schema(description = "챔피언 아이콘 URL")
    private String champProfileIconUrl;
    @Schema(description = "챔피언에게 가한 피해량")
    private int totalDamageToChampion;
    @Schema(description = "챔피언에게 받은 피해량")
    private int totalDamageTaken;
    @Schema(description = "승리 여부")
    private Boolean isWin;

    // 리뷰정보 객체
    private ReviewDto reviewDto;
    // 리뷰평점 객체
    private ReviewStatsDto reviewStatsDto;

    public void setChampProfileIconUrl(String iconUrl) {
        iconUrl = iconUrl.replaceAll("FiddleSticks", "Fiddlesticks");
        this.champProfileIconUrl = iconUrl;
    }
}
