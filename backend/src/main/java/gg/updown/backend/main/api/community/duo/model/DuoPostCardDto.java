package gg.updown.backend.main.api.community.duo.model;

import gg.updown.backend.main.api.community.common.model.CommunityPostDto;
import gg.updown.backend.main.api.lol.summoner.model.dto.LolSummonerMostChampionDto;
import gg.updown.backend.main.api.lol.summoner.model.entity.LolSummonerEntity;
import gg.updown.backend.main.api.ranking.model.SummonerBasicInfoDto;
import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuoPostCardDto extends CommunityPostDto {
    // 게시글 상세정보 DTO
    private DuoCommunityPostDto postDto;
    // 소환사 정보 DTO
    private DuoSummonerInfoDto duoSummonerInfoDto;
}
