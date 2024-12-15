package gg.updown.backend.main.api.review.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewSubmitReqDto {
    private int summonerReviewSeq;
    private String reviewerSiteCode;
    private String reviewerPuuid;
    private String targetPuuid;
    private double skillRating;
    private double teamworkRating;
    private double mannerRating;
    private String comment;
    private List<String> tagCodeList;
}
