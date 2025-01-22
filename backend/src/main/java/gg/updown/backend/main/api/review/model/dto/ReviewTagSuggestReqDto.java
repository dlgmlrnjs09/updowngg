package gg.updown.backend.main.api.review.model.dto;

import gg.updown.backend.main.enums.SiteTagSuggestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewTagSuggestReqDto {
    @NotNull(message = "tagValue는 필수값입니다.")
    private String tagValue;
    @NotNull(message = "tagDescription는 필수값입니다.")
    private String tagDescription;
    @NotNull(message = "isUp은 필수값입니다.")
    private boolean isUp;
    @NotNull(message = "targetPuuid는 필수값입니다.")
    private String targetPuuid;
    @NotNull(message = "matchId는 필수값입니다.")
    private String matchId;
    @NotNull(message = "reviewSeq는 필수값입니다.")
    private Long reviewSeq;
    @NotNull(message = "tagCategory는 필수값입니다.")
    private String tagCategory;
}
