package gg.updown.backend.main.api.review.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewUpdateReqDto {
    @NotNull(message = "summonerReviewSeq는 필수값입니다.")
    private Long summonerReviewSeq;
    @NotNull(message = "reviewerSiteCode는 필수값입니다.")
    private String reviewerSiteCode;
    @NotNull(message = "reviewerPuuid는 필수값입니다.")
    private String reviewerPuuid;
    @NotNull(message = "targetPuuid는 필수값입니다.")
    private String targetPuuid;
    @NotNull(message = "isUp은 필수값입니다.")
    private Boolean isUp;
    private String comment;
    private List<String> tagCodeList;
    @NotNull(message = "matchId는 필수값입니다.")
    private String matchId;
    @NotNull(message = "isAnonymous는 필수값입니다.")
    private Boolean isAnonymous;
}
