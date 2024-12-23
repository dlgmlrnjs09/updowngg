package gg.updown.backend.main.api.review.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewStatsDto {
    private String puuid;
    private double skillRatingAvg;
    private double teamworkRatingAvg;
    private double mannerRatingAvg;
    private int totalReviewCnt;
    private int last30DayReviewCnt;
}
