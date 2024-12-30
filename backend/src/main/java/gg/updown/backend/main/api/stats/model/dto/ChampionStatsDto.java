package gg.updown.backend.main.api.stats.model.dto;

import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import io.micrometer.common.util.StringUtils;
import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampionStatsDto {
    private String nameUs;
    private String nameKr;
    private String iconUrl;
    private Long playCount;
    private Long reviewCount;
    private Long upCount;
    private double upRatio;
    private double downRatio;
    private List<ReviewTagDto> topTagList;

    // 좋아요 비율 계산 (백분율)
    public double getUpRatio() {
        if (reviewCount == 0) return 0.0;
        return Math.round((double) upCount / reviewCount * 100 * 10) / 10.0;
    }

    public void setUpRatio() {
        if (reviewCount == 0) {
            this.upRatio = 0.0;
        }
        this.upRatio = Math.round((double) upCount / reviewCount * 100 * 10) / 10.0;
    }

    // 싫어요 수 계산
    public int getDownCount() {
        return (int) (reviewCount - upCount);
    }

    // 싫어요 비율 계산 (백분율)
    public double getDownRatio() {
        if (reviewCount == 0) return 0.0;
        return Math.round((double) getDownCount() / reviewCount * 100 * 10) / 10.0;
    }

    public void setDownRatio() {
        if (reviewCount == 0) {
            this.downRatio = 0.0;
        }
        this.downRatio = Math.round((double) getDownCount() / reviewCount * 100 * 10) / 10.0;
    }
}
