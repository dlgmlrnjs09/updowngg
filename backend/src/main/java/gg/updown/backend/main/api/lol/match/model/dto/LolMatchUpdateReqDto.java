package gg.updown.backend.main.api.lol.match.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LolMatchUpdateReqDto {
    @Schema(description = "라이엇 계정 고유ID")
    @NotNull(message = "puuid는 필수값입니다.")
    private String puuid;
}
