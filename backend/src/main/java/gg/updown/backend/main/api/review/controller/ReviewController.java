package gg.updown.backend.main.api.review.controller;

import gg.updown.backend.main.api.review.model.dto.*;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import gg.updown.backend.main.api.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/tag")
    public List<ReviewTagEntity> getReviewTagList() {
        return reviewService.getReviewTagList();
    }

    @PostMapping("/submit")
    public ResponseEntity<Long> submitReview(@RequestBody @Valid ReviewSubmitReqDto reviewSubmitReqDto) {
        reviewService.submitReview(reviewSubmitReqDto);
        return ResponseEntity.ok(reviewSubmitReqDto.getSummonerReviewSeq());
    }

    @PutMapping("/update")
    public ResponseEntity<Long> updateReview(@RequestBody @Valid ReviewUpdateReqDto reviewUpdateReqDto) {
        reviewService.updateReview(reviewUpdateReqDto);
        return ResponseEntity.ok(reviewUpdateReqDto.getSummonerReviewSeq());
    }

    @GetMapping("/stats")
    public ResponseEntity<ReviewStatsDto> getReviewStats(@Valid ReviewStatsReqDto reqDto) {
        ReviewStatsDto returnDto = reviewService.getReviewStats(reqDto.getPuuid());
        return ResponseEntity.status(HttpStatus.OK).body(returnDto);
    }

    @GetMapping("/tag-frequent")
    public ResponseEntity<List<ReviewTagDto>> getReviewTagFrequent(@Valid ReviewTagFrequentReqDto reqDto) {
        List<ReviewTagDto> reviewTagDto = reviewService.getFrequentTagCount(reqDto.getPuuid());
        return ResponseEntity.status(HttpStatus.OK).body(reviewTagDto);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ReviewDto>> getRecentReviews(@Valid ReviewRecentReqDto reqDto) {
        List<ReviewDto> reviewList = reviewService.getRecentReviewList(reqDto.getPuuid(), reqDto.getLimit());
        return ResponseEntity.status(HttpStatus.OK).body(reviewList);
    }

    @GetMapping("/rating-avg/champ")
    public ResponseEntity<List<ReviewRatingByChampDto>> getReviewChampRating(@Valid ReviewChampRatingReqDto reqDto) {
        List<ReviewRatingByChampDto> resultList = reviewService.getAvgRatingByChamp(reqDto.getPuuid());
        return ResponseEntity.status(HttpStatus.OK).body(resultList);
    }

    @GetMapping("/rating-avg/position")
    public ResponseEntity<List<ReviewRatingByPositionDto>> getReviewPositionRating(@Valid ReviewPositionRatingReqDto reqDto) {
        List<ReviewRatingByPositionDto> resultList = reviewService.getAvgRatingByPosition(reqDto.getPuuid());
        return ResponseEntity.status(HttpStatus.OK).body(resultList);
    }
}
