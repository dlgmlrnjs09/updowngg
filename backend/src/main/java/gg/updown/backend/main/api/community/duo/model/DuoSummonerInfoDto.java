package gg.updown.backend.main.api.community.duo.model;

import gg.updown.backend.main.api.lol.summoner.model.dto.LolSummonerMostChampionDto;
import gg.updown.backend.main.api.ranking.model.SummonerBasicInfoDto;
import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DuoSummonerInfoDto {
    // 소환사 정보 DTO
    private SummonerBasicInfoDto summonerBasicInfoDto;
    // 소환사 자주받은태그 DTO
    private List<ReviewTagDto> frequentTagDtoList;
    // 소환사 리뷰통계 DTO
    private ReviewStatsDto reviewStatsDto;
    // 모스트 챔피언 DTO
    private List<LolSummonerMostChampionDto> mostChampionDto;
}
