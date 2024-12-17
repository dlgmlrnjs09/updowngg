package gg.updown.backend.main.api.review.controller;

import gg.updown.backend.main.api.review.model.ReviewStatsDto;
import gg.updown.backend.main.api.review.model.ReviewSubmitReqDto;
import gg.updown.backend.main.api.review.model.ReviewTagEntity;
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
}
