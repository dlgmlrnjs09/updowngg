
package gg.updown.backend.main.api.review.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {
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
    private LocalDateTime delDt;
    private List<String> tagCodeList;

    private boolean reviewable;
}
