package gg.updown.backend.main.api.review.model.dto;

import gg.updown.backend.main.api.stats.model.dto.ChampionStatsDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewStatsDto {
    private String puuid;
    private Long upCount;
    private Long downCount;
    private double upRatio;
    private double downRatio;
    private Long totalReviewCnt;
    private Long playCount;
}
