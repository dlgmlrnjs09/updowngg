package gg.updown.backend.main.api.review.controller;

import gg.updown.backend.main.api.review.model.dto.*;
import gg.updown.backend.main.api.review.model.entity.ReviewTagCategoryEntity;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import gg.updown.backend.main.api.review.model.entity.ReviewTagSuggestEntity;
import gg.updown.backend.main.api.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "review", description = "사이트 리뷰관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "등록된 태그목록 조회", description = "사이트 등록된 태그목록 조회")
    @GetMapping("/tag")
    public List<ReviewTagEntity> getReviewTagList() {
        return reviewService.getReviewTagList();
    }

    @Operation(summary = "등록된 태그 카테고리 조회", description = "사이트 등록된 태그 카테고리 목록조회")
    @GetMapping("/tag-category")
    public List<ReviewTagCategoryEntity> getReviewTagCategoryList() {
        return reviewService.getReviewTagCategoryList();
    }

    @Operation(summary = "리뷰 등록", description = "소환사 리뷰 등록")
    @PostMapping("/submit")
    public ResponseEntity<Long> submitReview(@RequestBody @Valid ReviewSubmitReqDto reviewSubmitReqDto) {
        reviewService.submitReview(reviewSubmitReqDto);
        return ResponseEntity.ok(reviewSubmitReqDto.getSummonerReviewSeq());
    }

    @Operation(summary = "리뷰 수정", description = "소환사 리뷰 수정")
    @PutMapping("/update")
    public ResponseEntity<Long> updateReview(@RequestBody @Valid ReviewUpdateReqDto reviewUpdateReqDto) {
        reviewService.updateReview(reviewUpdateReqDto);
        return ResponseEntity.ok(reviewUpdateReqDto.getSummonerReviewSeq());
    }

    @Operation(summary = "태그 신청", description = "신규태그 신청")
    @PostMapping("/tag-suggest")
    public ResponseEntity<Long> insertSuggestedTag(@RequestBody List<ReviewTagSuggestReqDto> reqDto) {
        reviewService.insertSuggestTagList(reqDto);
        // 필요시 시퀀스 리턴 추가
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "받은리뷰 통계 조회", description = "특정 대상의 받은리뷰 통계조회")
    @GetMapping("/stats")
    public ResponseEntity<ReviewStatsDto> getReviewStats(@Valid ReviewStatsReqDto reqDto) {
        ReviewStatsDto returnDto = reviewService.getReviewStats(reqDto.getPuuid());
        return ResponseEntity.status(HttpStatus.OK).body(returnDto);
    }

    @Operation(summary = "최근받은태그목록 조회", description = "특정 대상의 최근받은태그목록 조회")
    @GetMapping("/tag-frequent")
    public ResponseEntity<List<ReviewTagDto>> getReviewTagFrequent(@Valid ReviewTagFrequentReqDto reqDto) {
        List<ReviewTagDto> reviewTagDto = reviewService.getFrequentTagCount(reqDto.getPuuid());
        return ResponseEntity.status(HttpStatus.OK).body(reviewTagDto);
    }

    @Operation(summary = "최근받은리뷰 조회", description = "특정 대상의 최근받은리뷰 조회")
    @GetMapping("/recent")
    public ResponseEntity<List<ReviewDto>> getRecentReviews(@Valid ReviewRecentReqDto reqDto) {
        List<ReviewDto> reviewList = reviewService.getRecentReviewList(reqDto.getPuuid(), reqDto.getLimit());
        return ResponseEntity.status(HttpStatus.OK).body(reviewList);
    }

    @GetMapping("/rating-avg/champ")
    @Operation(summary = "챔피언별 리뷰 통계조회", description = "특정 대상의 챔피언별 리뷰 통계조회")
    public ResponseEntity<List<ReviewRatingByChampDto>> getReviewChampRating(@Valid ReviewChampRatingReqDto reqDto) {
        List<ReviewRatingByChampDto> resultList = reviewService.getAvgRatingByChamp(reqDto.getPuuid());
        return ResponseEntity.status(HttpStatus.OK).body(resultList);
    }

    @Operation(summary = "포지션별 리뷰 통계조회", description = "특정 대상의 챔피언별 리뷰 통계조회")
    @GetMapping("/rating-avg/position")
    public ResponseEntity<List<ReviewRatingByPositionDto>> getReviewPositionRating(@Valid ReviewPositionRatingReqDto reqDto) {
        List<ReviewRatingByPositionDto> resultList = reviewService.getAvgRatingByPosition(reqDto.getPuuid());
        return ResponseEntity.status(HttpStatus.OK).body(resultList);
    }
}
