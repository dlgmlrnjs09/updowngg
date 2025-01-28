package gg.updown.backend.main.api.review.model.dto;

import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리뷰 통계 DTO")
public class ReviewStatsDto {
    @Schema(description = "라이엇 계정 고유ID")
    private String puuid;
    @Schema(description = "추천 수")
    private long upCount;
    @Schema(description = "비추천 수")
    private long downCount;
    @Schema(description = "추천 비율")
    private double upRatio;
    @Schema(description = "비추천 비율")
    private double downRatio;
    @Schema(description = "총 받은 리뷰 수")
    private long totalReviewCnt;
    @Schema(description = "치룬 경기 수")
    private long playCount;
    @Schema(description = "최근30일 받은리뷰 수")
    private long last30DayReviewCnt;
    @Schema(description = "추천점수")
    private double score;
}
