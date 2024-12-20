package gg.updown.backend.main.api.review.controller;

import gg.updown.backend.main.api.review.model.*;
import gg.updown.backend.main.api.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/tag")
    public List<ReviewTagEntity> getReviewTagList() {
        return reviewService.getReviewTagList();
    }

    @PostMapping("/submit")
    public void submitReview(@RequestBody ReviewSubmitReqDto reviewSubmitReqDto) {
        reviewService.submitReview(reviewSubmitReqDto, HttpMethod.POST);
    }

    @PutMapping("/update")
    public void updateReview(@RequestBody ReviewSubmitReqDto reviewSubmitReqDto) {
        reviewService.submitReview(reviewSubmitReqDto, HttpMethod.PUT);
    }

    @GetMapping("/stats")
    public ResponseEntity<ReviewStatsDto> getReviewStats(String puuid) {
        ReviewStatsDto returnDto = reviewService.getReviewStats(puuid);
        return ResponseEntity.status(HttpStatus.OK).body(returnDto);
    }

    @GetMapping("/tag-frequent")
    public ResponseEntity<List<ReviewTagDto>> getReviewTagFrequent(String puuid) {
        List<ReviewTagDto> reviewTagDto = reviewService.getFrequentTagCount(puuid);
        return ResponseEntity.status(HttpStatus.OK).body(reviewTagDto);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ReviewDto>> getRecentReviews(String puuid) {
        List<ReviewDto> reviewList = reviewService.getRecentReviewList(puuid);
        return ResponseEntity.status(HttpStatus.OK).body(reviewList);
    }
}
