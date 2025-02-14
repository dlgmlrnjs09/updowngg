package gg.updown.backend.external.riot.api.lol.spectator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "룬/페어크 정보")
public class CurrentMatchPerksDto {
    @Schema(description = "선택된 룬의 식별 번호 목록")
    private List<Long> perkIds;

    @Schema(description = "주요 룬 경로 식별 번호", example = "8100")
    private long perkStyle;

    @Schema(description = "보조 룬 경로 식별 번호", example = "8300")
    private long perkSubStyle;
}
