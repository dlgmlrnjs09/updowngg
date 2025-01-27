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
    List<ReviewDto> getWroteReviewList(@Param("reviewerPuuid") String reviewerPuuid, @Param("reviewerSiteCode") String reviewerSiteCode);
    List<ReviewDto> getReceivedReviewList(@Param("targetPuuid") String targetPuuid);
    ReviewStatsDto getReviewStatus(String targetPuuid);
    ReviewStatsDto getReviewAvgRating(String targetPuuid);
    List<ReviewTagDto> getFrequentTagCount(String targetPuuid);
    List<ReviewDto> getRecentReviewList(@Param("targetPuuid") String targetPuuid, @Param("limit")int limit);
    List<ReviewRatingByChampDto> getAvgRatingByChamp(String targetPuuid);
    List<ReviewRatingByPositionDto> getAvgRatingByPosition(String targetPuuid);
    void insertSuggestTagList(List<ReviewTagSuggestEntity> reqDto);
    Map<String, Object> getReviewNotificationElement(@Param("matchId")String matchId, @Param("targetPuuid")String targetPuuid);
}
