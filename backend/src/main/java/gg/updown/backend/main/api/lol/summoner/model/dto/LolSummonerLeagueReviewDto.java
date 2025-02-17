package gg.updown.backend.main.api.lol.summoner.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "LOL 소환사 랭크별 평가정보 DTO")
public class LolSummonerLeagueReviewDto {
    @Schema(description = "추천 수")
    private int upCount;
    @Schema(description = "비추천 수")
    private int downCount;
}
