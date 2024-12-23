package gg.updown.backend.main.api.review.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewChampRatingReqDto {
    @NotNull(message = "puuid는 필수값입니다.")
    private String puuid;
}
