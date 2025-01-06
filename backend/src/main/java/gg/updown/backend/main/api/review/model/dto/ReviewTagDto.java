package gg.updown.backend.main.api.review.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리뷰 태그 DTO")
public class ReviewTagDto {
    @Schema(description = "태그 고유코드")
    private String tagCode;
    @Schema(description = "태그 내용")
    private String tagValue;
    @Schema(description = "태그 설명")
    private String tagDescription;
    @Schema(description = "긍정/부정 여부")
    private boolean tagUpdown;

    @Schema(description = "받은태그 수")
    private int frequentCount;
}
