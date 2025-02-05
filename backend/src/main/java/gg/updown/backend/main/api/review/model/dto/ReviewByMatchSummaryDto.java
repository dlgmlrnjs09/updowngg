package gg.updown.backend.main.api.review.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewByMatchSummaryDto {
    private int upCount;
    private int downCount;
    private List<ReviewerInfo> reviewerInfoList;
    private List<ReviewTagDto> tagDtoList;
}

