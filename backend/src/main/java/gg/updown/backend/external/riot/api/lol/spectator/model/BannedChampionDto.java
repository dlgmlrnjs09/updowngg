package gg.updown.backend.external.riot.api.lol.spectator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "밴된 챔피언에 대한 정보")
public class BannedChampionDto {
    @Schema(description = "챔피언이 밴된 턴 순서", example = "1")
    private int pickTurn;

    @Schema(description = "밴된 챔피언의 고유 식별 번호", example = "238")
    private long championId;

    @Schema(description = "챔피언을 밴한 팀의 식별 번호", example = "100")
    private long teamId;
}