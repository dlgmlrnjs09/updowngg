package gg.updown.backend.main.api.ranking.model;

import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteRankingResDto {
    private SummonerBasicInfoDto summonerBasicInfoDto;
    private ReviewStatsDto reviewStatsDto;
    private List<ChampionStatsDto> championStatsDtoList;
    private List<ReviewTagDto> reviewTagDtoList;
}
