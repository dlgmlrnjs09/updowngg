package gg.updown.backend.main.api.review.service;

import gg.updown.backend.external.riot.RiotApiBasePath;
import gg.updown.backend.external.riot.RiotDdragonUrlBuilder;
import gg.updown.backend.main.api.review.mapper.ReviewMapper;
import gg.updown.backend.main.api.review.model.dto.*;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    @Value("${riot-api.latest-version}")
    private String latestVersion;

    private final ReviewMapper reviewMapper;
    private final ReviewTransactionService transactionService;

    public List<ReviewTagEntity> getReviewTagList() {
        return reviewMapper.getReviewTagList();
    }

    public List<ReviewDto> getWroteReviewList(String reviewerPuuid) {
        return reviewMapper.getWroteReviewList(reviewerPuuid);
    }

    public void submitReview(ReviewSubmitReqDto reqDto) {
        transactionService.insertSummonerReview(reqDto);
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

    public List<ReviewDto> getRecentReviewList(String targetPuuid) {
        return reviewMapper.getRecentReviewList(targetPuuid);
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
