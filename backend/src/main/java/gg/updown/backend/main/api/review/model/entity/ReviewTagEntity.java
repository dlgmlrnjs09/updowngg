package gg.updown.backend.main.api.review.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewTagEntity {
    @Schema(description = "태그 고유코드")
    private String tagCode;
    @Schema(description = "태그 내용")
    private String tagValue;
    @Schema(description = "태그 설명")
    private String tagDescription;
    @Schema(description = "긍정/부정 여부")
    private boolean tagUpdown;
}
