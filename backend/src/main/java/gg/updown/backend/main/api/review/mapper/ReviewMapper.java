package gg.updown.backend.main.api.review.mapper;

import gg.updown.backend.main.api.review.model.dto.*;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    List<ReviewTagEntity> getReviewTagList();
    void insertReview(ReviewSubmitReqDto reqDto);
    void insertReviewTags(@Param("reviewSeq") Long reviewSeq, @Param("tagCodeList") List<String> tagCodeList);
    void updateReview(ReviewUpdateReqDto reqDto);
    void deleteReviewTags(Long reviewSeq);
    List<ReviewDto> getWroteReviewList(String reviewerPuuid);
    ReviewStatsDto getReviewStatus(String targetPuuid);
    ReviewStatsDto getReviewAvgRating(String targetPuuid);
    List<ReviewTagDto> getFrequentTagCount(String targetPuuid);
    List<ReviewDto> getRecentReviewList(@Param("targetPuuid") String targetPuuid, @Param("limit")int limit);
    List<ReviewRatingByChampDto> getAvgRatingByChamp(String targetPuuid);
    List<ReviewRatingByPositionDto> getAvgRatingByPosition(String targetPuuid);
}
