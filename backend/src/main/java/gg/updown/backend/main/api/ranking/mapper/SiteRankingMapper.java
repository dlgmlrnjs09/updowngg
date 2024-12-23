package gg.updown.backend.main.api.ranking.mapper;

import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface SiteRankingMapper {
    List<ReviewStatsDto> selectTopRankerRatings(@Param("offset") int offset, @Param("limit") int limit);
    Map<String, Object> selectTopRankerBasicInfo(String puuid);
    List<ReviewTagDto> selectTopRankerRecentTags(String puuid);
}
