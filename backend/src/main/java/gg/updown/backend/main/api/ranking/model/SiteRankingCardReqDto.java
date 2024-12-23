package gg.updown.backend.main.api.ranking.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SiteRankingCardReqDto {
    @Min(value = 0, message = "offset은 0 이상이어야 합니다.")
    private Integer offset = 0;
    @Max(value = 50, message = "limit은 10 이상이어야 합니다.")
    private Integer limit = 10;
}
