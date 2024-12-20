package gg.updown.backend.main.api.review.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRatingByChampDto {
    private String champId;
    private String champName;
    private int playCount;
    private double totalAvgRating;
}
