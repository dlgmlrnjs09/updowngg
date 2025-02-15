package gg.updown.backend.main.api.ranking.service;

import gg.updown.backend.common.util.CalculateUtil;
import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.main.api.ranking.mapper.SiteRankingMapper;
import gg.updown.backend.main.api.ranking.model.SiteRankingReqDto;
import gg.updown.backend.main.api.ranking.model.SiteRankingResDto;
import gg.updown.backend.main.api.ranking.model.SummonerBasicInfoDto;
import gg.updown.backend.main.api.ranking.model.SummonerTagDto;
import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
//                champion.setReviewCount(ratingDto.getTotalReviewCnt());
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

    @Cacheable(value = "topRankers", key = "#reqDto")
    public List<SiteRankingResDto> selectTopRankersBulk(SiteRankingReqDto reqDto) {
        // 1. 랭커 목록 조회
        List<ReviewStatsDto> rankerRatingList = siteRankingMapper.selectTopRankerRatings(reqDto);

        // 2. puuid 리스트 추출
        List<String> puuids = rankerRatingList.stream()
                .map(ReviewStatsDto::getPuuid)
                .collect(Collectors.toList());

        if(puuids.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 벌크 데이터 조회
        Map<String, List<ChampionStatsDto>> championStats = siteRankingMapper
                .selectBulkMostChampionUpdownCount(puuids).stream()
                .collect(Collectors.groupingBy(ChampionStatsDto::getPuuid));

        Map<String, List<ReviewTagDto>> tagStats = siteRankingMapper
                .selectBulkTopTagsBySummoner(puuids)
                .stream()
                .collect(Collectors.toMap(
                        SummonerTagDto::getPuuid,
                        dto -> Optional.ofNullable(dto.getTagDtoList()).orElse(Collections.emptyList()),
                        (existing, replacement) -> existing
                ));

        Map<String, SummonerBasicInfoDto> basicInfos = siteRankingMapper
                .selectBulkTopRankerBasicInfo(puuids).stream()
                .collect(Collectors.toMap(SummonerBasicInfoDto::getPuuid, Function.identity()));

        // 4. 데이터 조합, 매핑
        return rankerRatingList.stream()
                .map(ratingDto -> {
                    String puuid = ratingDto.getPuuid();

                    // 평가 점수, 비율 계산
                    ratingDto.setScore(CalculateUtil.calculateWilsonScore(
                            (int)ratingDto.getUpCount(),
                            (int)ratingDto.getDownCount()
                    ));
                    ratingDto.setUpRatio(CalculateUtil.getRatio(
                            ratingDto.getTotalReviewCnt(),
                            ratingDto.getUpCount()
                    ));
                    ratingDto.setDownRatio(CalculateUtil.getRatio(
                            ratingDto.getTotalReviewCnt(),
                            ratingDto.getDownCount()
                    ));

                    // 챔피언 정보 설정
                    List<ChampionStatsDto> championList = championStats.getOrDefault(puuid, Collections.emptyList());
                    championList.forEach(champion ->
                            champion.setIconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(
                                    latestVersion,
                                    champion.getNameUs()
                            ))
                    );

                    // 기본 정보 설정
                    SummonerBasicInfoDto basicInfo = basicInfos.get(puuid);
                    if(basicInfo != null) {
                        basicInfo.setProfileIconUrl(RiotDdragonUrlBuilder.getSummonerIconUrl(
                                latestVersion,
                                basicInfo.getProfileIconId()
                        ));
                    }

                    return SiteRankingResDto.builder()
                            .reviewStatsDto(ratingDto)
                            .championStatsDtoList(championList)
                            .reviewTagDtoList(tagStats.getOrDefault(puuid, Collections.emptyList()))
                            .summonerBasicInfoDto(basicInfo)
                            .build();

                })
                .sorted(Comparator.comparing(dto -> dto.getReviewStatsDto().getScore(), Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
