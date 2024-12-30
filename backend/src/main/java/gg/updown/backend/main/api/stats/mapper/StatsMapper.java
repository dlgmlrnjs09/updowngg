package gg.updown.backend.main.api.stats.mapper;

import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import gg.updown.backend.main.api.stats.model.dto.ChampionStatsTagDto;
import gg.updown.backend.main.api.stats.model.dto.SortTypeReqDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatsMapper {
    List<ChampionStatsDto> getPlayCountByChampions(SortTypeReqDto dto);
    List<ChampionStatsDto> getReviewByChampions(SortTypeReqDto dto);
    List<ChampionStatsDto> getStatsByChampions(SortTypeReqDto dto);
    List<ChampionStatsTagDto> getTopTagsByChampion(SortTypeReqDto dto);
}
