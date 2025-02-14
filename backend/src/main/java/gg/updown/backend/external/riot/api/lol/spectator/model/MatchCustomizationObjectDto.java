package gg.updown.backend.external.riot.api.lol.spectator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "게임 맞춤 설정 정보")
public class MatchCustomizationObjectDto {
    @Schema(description = "맞춤 설정 카테고리", example = "loadout")
    private String category;

    @Schema(description = "맞춤 설정 내용", example = "customization details")
    private String content;
}
