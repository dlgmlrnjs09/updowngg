package gg.updown.backend.main.api.review.mapper;

import gg.updown.backend.main.api.review.model.ReviewTagEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewMapper {
    List<ReviewTagEntity> getReviewTagList();
}
