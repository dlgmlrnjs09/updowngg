package gg.updown.backend.main.api.review.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRatingByChampDto {
    @Schema(description = "챔피언 고유ID")
    private String champId;
    @Schema(description = "챔피언 명(영문)")
    private String champName;
    @Schema(description = "챔피언 아이콘 URL")
    private String champIconUrl;
    @Schema(description = "해당 챔피언으로 플레이한 수")
    private int playCount;
    @Schema(description = "해당 챔피언으로 받은 추천 수")
    private int upCount;
    @Schema(description = "해당 챔피언으로 받은 비추천 수")
    private int downCount;
}
