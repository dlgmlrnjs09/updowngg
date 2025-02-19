package gg.updown.backend.main.api.lol.match.model.dto;

import gg.updown.backend.main.enums.SiteMatchFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolMatchInfoReqDto {
    @Schema(description = "라이엇계정 고유 ID", required = true)
    @NotNull(message = "puuid는 필수값입니다.")
    private String puuid;

    @Schema(description = "검색시작 Index", example = "0", minimum = "0", defaultValue = "0")
    @Min(value = 0, message = "startIndex는 0 이상이어야 합니다.")
    private Integer startIndex;

    @Schema(description = "조회할 Match 수", example = "10", minimum = "1", defaultValue = "5")
    @Min(value = 1, message = "count는 1 이상이어야 합니다.")
    @Max(value = 100, message = "count는 100 이하여야 합니다.")
    private Integer count;

    @Schema(
            description = "게임 모드 필터",
            example = "ALL",
            defaultValue = "ALL",
            implementation = SiteMatchFilter.class
    )
    private SiteMatchFilter gameMode = SiteMatchFilter.ALL;


    @Schema(
            description = "리뷰 필터 (reviewed: 받은 평가가 있는 게임, unreviewed: 작성한 평가가 없는 게임)",
            example = "reviewed",
            allowableValues = {"reviewed", "unreviewed"}
    )
    private String reviewFilter;
}
