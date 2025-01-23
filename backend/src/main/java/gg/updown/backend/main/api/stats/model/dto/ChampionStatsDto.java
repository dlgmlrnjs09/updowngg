package gg.updown.backend.main.api.stats.model.dto;

import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "챔피언 통계 DTO")
public class ChampionStatsDto {
    private String puuid;

    @Schema(description = "챔피언 명(영문)")
    private String nameUs;
    @Schema(description = "챔피언 명(국문)")
    private String nameKr;
    @Schema(description = "챔피언 아이콘 URL")
    private String iconUrl;
    @Schema(description = "챔피언으로 치룬 경기 수")
    private long playCount;
    @Schema(description = "챔피언으로 받은 경기 수")
    private long reviewCount;
    @Schema(description = "챔피언으로 받은 추천 수")
    private long upCount;
    @Schema(description = "챔피언 추천 비율")
    private double upRatio;
    @Schema(description = "챔피언 비추천 비율")
    private double downRatio;
    @Schema(description = "챔피언 자주받은 태그 DTO 목록")
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

    public void setIconUrl(String iconUrl) {
        iconUrl = iconUrl.replaceAll("FiddleSticks", "Fiddlesticks");
        this.iconUrl = iconUrl;
    }
}
