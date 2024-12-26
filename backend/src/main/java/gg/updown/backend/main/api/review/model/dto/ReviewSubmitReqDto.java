package gg.updown.backend.main.api.review.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewSubmitReqDto {
    private Long summonerReviewSeq;
    @NotNull(message = "reviewerSiteCode는 필수값입니다.")
    private String reviewerSiteCode;
    @NotNull(message = "reviewerPuuid는 필수값입니다.")
    private String reviewerPuuid;
    @NotNull(message = "targetPuuid는 필수값입니다.")
    private String targetPuuid;
    private String comment;
    @NotNull(message = "ipUp은 필수값입니다.")
    private Boolean isUp;
    private List<String> tagCodeList;
    @NotNull(message = "matchId는 필수값입니다.")
    private String matchId;
}
