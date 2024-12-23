package gg.updown.backend.main.api.review.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRatingByPositionDto {
    private String position;
    private int playCount;
    private double totalAvgRating;
}
