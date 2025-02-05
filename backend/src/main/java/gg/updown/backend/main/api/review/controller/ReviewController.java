package gg.updown.backend.main.api.review.controller;

import gg.updown.backend.common.model.PagingDto;
import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import gg.updown.backend.main.api.review.model.dto.*;
import gg.updown.backend.main.api.review.model.entity.ReviewTagCategoryEntity;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import gg.updown.backend.main.api.review.service.ReviewHistoryService;
import gg.updown.backend.main.api.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
@Tag(name = "review", description = "사이트 리뷰관련 API")
public class ReviewController {

    private static final int ITEMS_PER_PAGE = 10;
    private static final int DISPLAY_PAGE_COUNT = 5;

    private final ReviewService reviewService;
    private final ReviewHistoryService historyService;

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
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/submit")
    public ResponseEntity<Long> submitReview(@RequestBody @Valid ReviewSubmitReqDto reviewSubmitReqDto) {
        reviewService.submitReview(reviewSubmitReqDto);
        return ResponseEntity.ok(reviewSubmitReqDto.getSummonerReviewSeq());
    }

    @Operation(summary = "리뷰 수정", description = "소환사 리뷰 수정")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    public ResponseEntity<Long> updateReview(@RequestBody @Valid ReviewUpdateReqDto reviewUpdateReqDto) {
        reviewService.updateReview(reviewUpdateReqDto);
        return ResponseEntity.ok(reviewUpdateReqDto.getSummonerReviewSeq());
    }

    @Operation(summary = "태그 신청", description = "신규태그 신청")
    @PreAuthorize("isAuthenticated()")
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
        List<ReviewTagDto> reviewTagDto = reviewService.getFrequentTagCount(reqDto.getPuuid(), 5);
        return ResponseEntity.status(HttpStatus.OK).body(reviewTagDto);
    }

    @Operation(summary = "최근받은리뷰 조회", description = "특정 대상의 최근받은리뷰 조회")
    @GetMapping("/recent")
    public ResponseEntity<List<ReviewDto>> getRecentReviews(@Valid ReviewRecentReqDto reqDto) {
        List<ReviewDto> reviewList = reviewService.getRecentReviewList(reqDto.getPuuid(), reqDto.getLimit());
        return ResponseEntity.status(HttpStatus.OK).body(reviewList);
    }

    @Operation(summary = "경기별 평가자 및 태그정보 조회", description = "로그인한 사용자의 특정 경기에서 받은 리뷰의 평가자 및 태그정보 조회")
    @GetMapping("/match/{matchId}")
    public ResponseEntity<ReviewByMatchSummaryDto> getReviewerAndTagsByMatch(
            @PathVariable String matchId,
            @AuthenticationPrincipal UserDetails userDetail
    ) {
        UserDetailImpl userDetails = (UserDetailImpl) userDetail;
        ReviewByMatchSummaryDto result = reviewService.getReviewerAndTagsByMatch(userDetails.getPuuid(), matchId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
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

    @Operation(summary = "작성한 리뷰 조회", description = "로그인한 사용자가 작성한 리뷰 조회")
    @GetMapping("/history/written")
    public ResponseEntity<PagingDto<ReviewHistoryDto>> getReviewWrittenHistory(
            @Valid ReviewHistoryReqDto reviewHistoryReqDto,
            @AuthenticationPrincipal UserDetails userDetail
    ) {
        UserDetailImpl userDetails = (UserDetailImpl) userDetail;

        int totalItems = reviewService.getWroteReviewCount(userDetails.getSiteCode());
        List<ReviewHistoryDto> reviews = historyService.getWrittenHistory(
                userDetails.getSiteCode(),
                reviewHistoryReqDto.getPage(),
                ITEMS_PER_PAGE
        );

        PagingDto<ReviewHistoryDto> response = new PagingDto<>(
                reviews,
                reviewHistoryReqDto.getPage(),
                ITEMS_PER_PAGE,
                totalItems,
                DISPLAY_PAGE_COUNT
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "받은 리뷰 조회", description = "로그인한 사용자가 받은 리뷰 조회")
    @GetMapping("/history/received")
    public ResponseEntity<PagingDto<ReviewHistoryDto>> getReviewReceivedHistory(
            @Valid ReviewHistoryReqDto reviewHistoryReqDto,
            @AuthenticationPrincipal UserDetails userDetail
    ) {
        UserDetailImpl userDetails = (UserDetailImpl) userDetail;

        int totalItems = reviewService.getReceivedReviewCount(userDetails.getPuuid());
        List<ReviewHistoryDto> reviews = historyService.getReceivedHistory(
                userDetails.getPuuid(),
                reviewHistoryReqDto.getPage(),
                ITEMS_PER_PAGE
        );

        PagingDto<ReviewHistoryDto> response = new PagingDto<>(
                reviews,
                reviewHistoryReqDto.getPage(),
                ITEMS_PER_PAGE,
                totalItems,
                DISPLAY_PAGE_COUNT
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 리뷰 페이지번호 조회", description = "특정 리뷰가 존재하는 페이지번호 조회")
    @GetMapping("/find-page")
    public ResponseEntity<Integer> findReviewPage(
            @RequestParam Long reviewSeq
    ) {
        int page = reviewService.findReviewPage(reviewSeq, ITEMS_PER_PAGE);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "특정 대상에게 작성한 리뷰가 존재하는지 여부 확인", description = "로그인 사용자가 특정 대상에게 작성한 리뷰가 존재하는지에 대한 여부 확인")
    @GetMapping("/my/exist")
    public ResponseEntity<Boolean> checkExistWritten(
            String targetPuuid,
            @AuthenticationPrincipal UserDetails userDetail
    ) {
        return ResponseEntity.ok(reviewService.checkExist(((UserDetailImpl) userDetail).getPuuid(), targetPuuid));
    }

    @Operation(summary = "특정 대상에게 작성한 리뷰 조회", description = "로그인 사용자가 특정 대상에게 작성한 리뷰 조회")
    @GetMapping("/my/{targetPuuid}")
    public ResponseEntity<ReviewDto> getWrittenReviewToTarget(
            @AuthenticationPrincipal UserDetails userDetail,
            @PathVariable String targetPuuid
    ) {
        return ResponseEntity.ok(reviewService.getWrittenToTarget(((UserDetailImpl) userDetail).getPuuid(), targetPuuid));
    }
}
