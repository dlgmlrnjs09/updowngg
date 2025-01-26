package gg.updown.backend.main.api.review.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewHistoryReqDto {
    @Min(value = 0, message = "offset은 0 이상이어야 합니다.")
    private Integer offset;
    @Max(value = 50, message = "limit은 10 이상이어야 합니다.")
    private Integer limit;
}
