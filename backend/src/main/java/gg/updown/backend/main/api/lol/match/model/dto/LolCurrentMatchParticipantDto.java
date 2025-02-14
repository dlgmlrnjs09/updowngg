package gg.updown.backend.main.api.lol.match.model.dto;


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
public class LolCurrentMatchParticipantDto {
    // 팀 ID
    private LolCurrentMatchPlayerDto playerDto;
    // 소환사 정보 DTO
    private SummonerBasicInfoDto summonerInfoDto;
    // 소환사 자주받은태그 DTO
    private List<ReviewTagDto> frequentTagDtoList;
    // 소환사 리뷰통계 DTO
    private ReviewStatsDto reviewStatsDto;
}
