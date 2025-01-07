package gg.updown.backend.main.api.stats.model.dto;

import gg.updown.backend.external.riot.enums.Tier;
import gg.updown.backend.external.riot.enums.MatchPosition;
import gg.updown.backend.external.riot.enums.MatchGameMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortTypeReqDto {
    @Schema(description = "검색할 게임 유형")
    private MatchGameMode matchGameMode;
    @Schema(description = "검색할 게임 포지션")
    private MatchPosition matchPosition;
    @Schema(description = "검색할 티어")
    private Tier tier;
}
