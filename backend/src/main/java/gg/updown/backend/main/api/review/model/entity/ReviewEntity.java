package gg.updown.backend.main.api.review.model.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewEntity {
    private int summonerReviewSeq;
    private String reviewerSiteCode;
    private String reviewerPuuid;
    private String targetPuuid;
    private double skillRating;
    private double teamworkRating;
    private double mannerRating;
    private String comment;
    private LocalDateTime regDt;
    private LocalDateTime updDt;
}
