package gg.updown.backend.main.api.review.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRatingByPositionDto {
    @Schema(description = "포지션 명", allowableValues = {"TOP", "JUNGLE", "MIDDLE", "BOTTOM", "UTILITY"})
    private String position;
    @Schema(description = "해당 포지션으로 플레이한 수")
    private int playCount;
    @Schema(description = "해당 포지션으로 받은 추천 수")
    private int upCount;
    @Schema(description = "해당 포지션으로 받은 비추천 수")
    private int downCount;
}
