package gg.updown.backend.main.api.stats.mapper;

import gg.updown.backend.main.api.stats.model.dto.ChampionResDto;
import gg.updown.backend.main.api.stats.model.dto.SortTypeReqDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatsMapper {
    List<ChampionResDto> getPlayCountByChampions(SortTypeReqDto dto);
}
