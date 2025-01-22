package gg.updown.backend.main.api.ranking.service;

import gg.updown.backend.common.util.CalculateUtil;
import gg.updown.backend.external.riot.RiotApiBasePath;
import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.main.api.ranking.mapper.SiteRankingMapper;
import gg.updown.backend.main.api.ranking.model.*;
import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import gg.updown.backend.main.api.stats.model.dto.SortTypeReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteRankingService {
    @Value("${riot-api.latest-version}")
    private String latestVersion;

    private final SiteRankingMapper siteRankingMapper;

    public List<SiteRankingResDto> selectTopRankers(SiteRankingReqDto reqDto) {
        List<SiteRankingResDto> resultDtoList = new ArrayList<>();

        // 1. 랭커 목록 Get
        List<ReviewStatsDto> rankerRatingList = siteRankingMapper.selectTopRankerRatings(reqDto);
        for (ReviewStatsDto ratingDto : rankerRatingList) {
            // 2. 평가점수, 비율 Set
            ratingDto.setScore(CalculateUtil.calculateWilsonScore((int) ratingDto.getUpCount(), (int) ratingDto.getDownCount()));
            ratingDto.setUpRatio(CalculateUtil.getRatio(ratingDto.getTotalReviewCnt(), ratingDto.getUpCount()));
            ratingDto.setDownRatio(CalculateUtil.getRatio(ratingDto.getTotalReviewCnt(), ratingDto.getDownCount()));
            // 2. 모스트3 챔피언정보 Get
            List<ChampionStatsDto> mostChampionList = siteRankingMapper.selectMostChampionUpdownCount(ratingDto.getPuuid());
            for (ChampionStatsDto champion : mostChampionList) {
                champion.setReviewCount(ratingDto.getTotalReviewCnt());
                champion.setIconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, champion.getNameUs()));
            }

            // 3. Top3 태그 Get
            SummonerTagDto topTagDto = siteRankingMapper.selectTopTagsBySummoner(ratingDto.getPuuid());
            if (topTagDto == null) {
                topTagDto = new SummonerTagDto();
            }
            // 4. 소환사 기본 정보 Get
            SummonerBasicInfoDto basicInfoDto = siteRankingMapper.selectTopRankerBasicInfo(ratingDto.getPuuid());
            basicInfoDto.setProfileIconUrl(RiotDdragonUrlBuilder.getSummonerIconUrl(latestVersion, basicInfoDto.getProfileIconId()));


            resultDtoList.add(SiteRankingResDto.builder()
                    .reviewStatsDto(ratingDto)
                            .championStatsDtoList(mostChampionList)
                            .reviewTagDtoList(topTagDto.getTagDtoList())
                            .summonerBasicInfoDto(basicInfoDto)
                    .build());
        }

        return resultDtoList;
    }
}
