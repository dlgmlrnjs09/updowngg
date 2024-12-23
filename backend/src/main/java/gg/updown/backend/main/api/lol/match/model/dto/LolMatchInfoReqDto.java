package gg.updown.backend.main.api.lol.match.model.dto;

import jakarta.annotation.security.DenyAll;
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
    @NotNull(message = "puuid는 필수값입니다.")
    String puuid;

    @Min(value = 0, message = "startIndex는 0 이상이어야 합니다.")
    Integer startIndex;
    @Min(value = 1, message = "count는 1 이상이어야 합니다.")
    @Max(value = 5, message = "count는 5 이하여야 합니다.")
    Integer count;
}
