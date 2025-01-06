package gg.updown.backend.main.api.ranking.model;

import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사이트 랭킹정보 DTO")
public class SiteRankingResDto {
    private SummonerBasicInfoDto summonerBasicInfoDto;
    private ReviewStatsDto reviewStatsDto;
    private List<ChampionStatsDto> championStatsDtoList;
    private List<ReviewTagDto> reviewTagDtoList;
}
