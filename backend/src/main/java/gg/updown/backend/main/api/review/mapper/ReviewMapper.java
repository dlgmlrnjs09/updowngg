package gg.updown.backend.main.api.review.mapper;

import gg.updown.backend.main.api.review.model.ReviewSubmitReqDto;
import gg.updown.backend.main.api.review.model.ReviewTagEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    List<ReviewTagEntity> getReviewTagList();
    void insertReview(ReviewSubmitReqDto reqDto);
    void insertReviewTags(@Param("reviewSeq") int reviewSeq, @Param("tagCodeList") List<String> tagCodeList);
    List<String> getWroteReviewTargetPuuidList(String reviewerPuuid);
}
