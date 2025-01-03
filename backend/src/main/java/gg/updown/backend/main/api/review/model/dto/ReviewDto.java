
package gg.updown.backend.main.api.review.model.dto;

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
    private String matchId;
    private String reviewerSiteCode;
    private String reviewerPuuid;
    private String targetPuuid;
    private Boolean isUp;
    private String comment;
    private LocalDateTime regDt;
    private LocalDateTime updDt;
    private LocalDateTime delDt;
    private List<String> tagCodeList;
    private List<ReviewTagDto> tagDtoList;

    private boolean reviewable;
}
