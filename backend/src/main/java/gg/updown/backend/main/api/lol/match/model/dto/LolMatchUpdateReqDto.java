package gg.updown.backend.main.api.lol.match.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LolMatchUpdateReqDto {
    @NotNull(message = "puuid는 필수값입니다.")
    private String puuid;
}
