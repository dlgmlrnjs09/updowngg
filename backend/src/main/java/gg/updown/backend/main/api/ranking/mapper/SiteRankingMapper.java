package gg.updown.backend.main.api.ranking.mapper;

import gg.updown.backend.main.api.ranking.model.SiteRankingReqDto;
import gg.updown.backend.main.api.ranking.model.SummonerBasicInfoDto;
import gg.updown.backend.main.api.ranking.model.SummonerTagDto;
import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import gg.updown.backend.main.api.stats.model.dto.SortTypeReqDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface SiteRankingMapper {
    List<ReviewStatsDto> selectTopRankerRatings(SiteRankingReqDto siteRankingReqDto);
    SummonerBasicInfoDto selectTopRankerBasicInfo(String puuid);
    List<ReviewTagDto> selectTopRankerRecentTags(String puuid);

    List<ChampionStatsDto> selectMostChampionUpdownCount(@Param("puuid")String puuid);
    SummonerTagDto selectTopTagsBySummoner(@Param("puuid")String puuid);
}
