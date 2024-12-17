package gg.updown.backend.main.api.review.service;

import gg.updown.backend.main.api.review.mapper.ReviewMapper;
import gg.updown.backend.main.api.review.model.ReviewDto;
import gg.updown.backend.main.api.review.model.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.ReviewSubmitReqDto;
import gg.updown.backend.main.api.review.model.ReviewTagEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewMapper reviewMapper;
    private final ReviewTransactionService transactionService;

    public List<ReviewTagEntity> getReviewTagList() {
        return reviewMapper.getReviewTagList();
    }

    public List<ReviewDto> getWroteReviewList(String reviewerPuuid) {
        return reviewMapper.getWroteReviewList(reviewerPuuid);
    }

    public void submitReview(ReviewSubmitReqDto reqDto, HttpMethod httpMethod) {
        if (httpMethod == HttpMethod.POST) {
            transactionService.insertSummonerReview(reqDto);
        } else {
            transactionService.updateSummonerReview(reqDto);
        }
    }

    public ReviewStatsDto getReviewStats(String targetPuuid) {
        return reviewMapper.getReviewStatus(targetPuuid);
    }
}
