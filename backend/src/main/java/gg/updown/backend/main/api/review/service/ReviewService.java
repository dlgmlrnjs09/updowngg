package gg.updown.backend.main.api.review.service;

import gg.updown.backend.common.util.DateUtil;
import gg.updown.backend.external.riot.RiotApiBasePath;
import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.main.api.lol.match.service.LolMatchService;
import gg.updown.backend.main.api.lol.summoner.service.LolSummonerService;
import gg.updown.backend.main.api.review.mapper.ReviewMapper;
import gg.updown.backend.main.api.review.model.dto.*;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReviewService {
    @Value("${riot-api.latest-version}")
    private String latestVersion;

    @Value("${match.basic.start-time}")
    private String basicStartTime;

    private final ReviewMapper reviewMapper;
    private final ReviewTransactionService transactionService;
    private final LolSummonerService summonerService;
    private final LolMatchService matchService;

    public ReviewService(
            ReviewMapper reviewMapper,
            ReviewTransactionService transactionService,
            LolSummonerService summonerService,
            @Lazy LolMatchService matchService
    ) {
        this.reviewMapper = reviewMapper;
        this.transactionService = transactionService;
        this.summonerService = summonerService;
        this.matchService = matchService;
    }

    public List<ReviewTagEntity> getReviewTagList() {
        return reviewMapper.getReviewTagList();
    }

    public List<ReviewDto> getWroteReviewList(String reviewerPuuid) {
        return reviewMapper.getWroteReviewList(reviewerPuuid);
    }

    public void submitReview(ReviewSubmitReqDto reqDto) {
        transactionService.insertSummonerReview(reqDto);
        // 리뷰남긴 사용자 정보, 경기목록 가져오기
        // 리뷰작성과 무관하므로 log만 출력
        try {
            summonerService.conflictSummonerInfo(reqDto.getTargetPuuid());
            Long startDate = DateUtil.yyyyMMddToMilliseconds(basicStartTime);
            Long endDate = DateUtil.getCurrentTimeMillis();
            matchService.getAndInsertMatchIdList(reqDto.getTargetPuuid(), startDate, endDate);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void updateReview(ReviewUpdateReqDto reqDto) {
        transactionService.updateSummonerReview(reqDto);
    }

    public ReviewStatsDto getReviewStats(String targetPuuid) {
        return reviewMapper.getReviewStatus(targetPuuid);
    }

    public ReviewStatsDto getReviewAvgRating(String targetPuuid) {
        return reviewMapper.getReviewAvgRating(targetPuuid);
    }

    public List<ReviewTagDto> getFrequentTagCount(String targetPuuid) {
        return reviewMapper.getFrequentTagCount(targetPuuid);
    }

    public List<ReviewDto> getRecentReviewList(String targetPuuid, int limit) {
        return reviewMapper.getRecentReviewList(targetPuuid, limit);
    }

    public List<ReviewRatingByChampDto> getAvgRatingByChamp(String targetPuuid) {
        List<ReviewRatingByChampDto> dtoList = reviewMapper.getAvgRatingByChamp(targetPuuid);
        for (ReviewRatingByChampDto dto : dtoList) {
            dto.setChampIconUrl(RiotDdragonUrlBuilder.getChampionIconUrl(latestVersion, dto.getChampName()));
        }
        return dtoList;
    }

    public List<ReviewRatingByPositionDto> getAvgRatingByPosition(String targetPuuid) {
        return reviewMapper.getAvgRatingByPosition(targetPuuid);
    }
}
