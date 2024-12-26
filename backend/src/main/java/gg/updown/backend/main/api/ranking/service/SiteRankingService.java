package gg.updown.backend.main.api.ranking.service;

import gg.updown.backend.external.riot.RiotApiBasePath;
import gg.updown.backend.main.api.ranking.mapper.SiteRankingMapper;
import gg.updown.backend.main.api.ranking.model.SiteRankingCardResDto;
import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteRankingService {
    private final SiteRankingMapper siteRankingMapper;

    public List<SiteRankingCardResDto> selectTopRankers(int offset, int limit) {
        List<SiteRankingCardResDto> resultDtoList = new ArrayList<>();

        List<ReviewStatsDto> rankerRatingList = siteRankingMapper.selectTopRankerRatings(offset, limit);
        for (ReviewStatsDto ratingDto : rankerRatingList) {
            Map<String, Object> basicInfoMap = siteRankingMapper.selectTopRankerBasicInfo(ratingDto.getPuuid());
            List<ReviewTagDto> recentTagList = siteRankingMapper.selectTopRankerRecentTags(ratingDto.getPuuid());
            resultDtoList.add(SiteRankingCardResDto.builder()
                            .puuid(basicInfoMap.get("puuid").toString())
                            .profileIconId(basicInfoMap.get("profile_icon_id").toString())
                            .gameName(basicInfoMap.get("game_name").toString())
                            .tagLine(basicInfoMap.get("tag_line").toString())
                            .upCount(ratingDto.getUpCount())
                            .downCount(ratingDto.getDownCount())
                            .totalReviewCount(ratingDto.getTotalReviewCnt())
                            .last30DayReviewCnt(ratingDto.getLast30DayReviewCnt())
                            .profileIconUrl(RiotApiBasePath.DDRAGON.getUrl() + "/cdn/14.4.1/img/profileicon/" + basicInfoMap.get("profile_icon_id") + ".png")
                            .recentTags(recentTagList)
                    .build());
        }

        return resultDtoList;
    }
}
