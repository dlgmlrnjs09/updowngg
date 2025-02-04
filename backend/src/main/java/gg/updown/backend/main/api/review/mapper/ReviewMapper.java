package gg.updown.backend.main.api.review.mapper;

import gg.updown.backend.main.api.review.model.dto.*;
import gg.updown.backend.main.api.review.model.entity.ReviewTagCategoryEntity;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import gg.updown.backend.main.api.review.model.entity.ReviewTagSuggestEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface ReviewMapper {
    List<ReviewTagEntity> getReviewTagList();
    List<ReviewTagCategoryEntity> getReviewTagCategoryList();
    void insertReview(ReviewSubmitReqDto reqDto);
    void insertReviewTags(@Param("reviewSeq") Long reviewSeq, @Param("tagCodeList") List<String> tagCodeList);
    void updateReview(ReviewUpdateReqDto reqDto);
    void deleteReviewTags(Long reviewSeq);
    List<ReviewDto> getWroteReviewList(String reviewerPuuid);
    List<ReviewDto> getWroteReviewListPaging(
            @Param("reviewerSiteCode") long reviewerSiteCode,
            @Param("itemsPerPage") int itemPerPage,
            @Param("offset") int offset
    );
    int getWroteReviewCount(long reviewerSiteCode);
    List<ReviewDto> getReceivedReviewListPaging(
            @Param("targetPuuid") String targetPuuid,
            @Param("itemsPerPage") int itemPerPage,
            @Param("offset") int offset
    );
    int getReceivedReviewCount(String targetPuuid);
    ReviewStatsDto getReviewStatus(String targetPuuid);
    ReviewStatsDto getReviewAvgRating(String targetPuuid);
    List<ReviewTagDto> getFrequentTagCount(@Param("targetPuuid") String targetPuuid, @Param("limit") int limit);
    List<ReviewDto> getRecentReviewList(@Param("targetPuuid") String targetPuuid, @Param("limit")int limit);
    List<ReviewRatingByChampDto> getAvgRatingByChamp(String targetPuuid);
    List<ReviewRatingByPositionDto> getAvgRatingByPosition(String targetPuuid);
    void insertSuggestTagList(List<ReviewTagSuggestEntity> reqDto);
    Map<String, Object> getReviewNotificationElement(@Param("matchId")String matchId, @Param("targetPuuid")String targetPuuid);
    int findReviewIndex(Long reviewSeq);
    int checkExist(@Param("reviewerPuuid")String reviewerPuuid, @Param("targetPuuid") String targetPuuid);
    ReviewDto getWrittenToTarget(@Param("reviewerPuuid")String reviewerPuuid, @Param("targetPuuid") String targetPuuid);
}
