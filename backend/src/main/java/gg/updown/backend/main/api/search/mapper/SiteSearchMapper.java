package gg.updown.backend.main.api.search.mapper;

import gg.updown.backend.main.api.search.model.SiteSearchSummonerDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SiteSearchMapper {
    List<SiteSearchSummonerDto> searchSummoners(String keyword);
}
