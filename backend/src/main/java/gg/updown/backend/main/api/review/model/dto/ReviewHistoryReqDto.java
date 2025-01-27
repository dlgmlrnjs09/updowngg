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
    @Min(value = 1, message = "페이지는 1 이상이어야 합니다.")
    private int page;
}
