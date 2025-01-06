package gg.updown.backend.main.api.lol.match.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "라이엇계정 고유 ID", required = true)
    @NotNull(message = "puuid는 필수값입니다.")
    String puuid;

    @Schema(description = "검색시작 Index", example = "0", minimum = "0", defaultValue = "0")
    @Min(value = 0, message = "startIndex는 0 이상이어야 합니다.")
    Integer startIndex;

    @Schema(description = "조회할 Match 수", example = "10", minimum = "1", defaultValue = "5")
    @Min(value = 1, message = "count는 1 이상이어야 합니다.")
    @Max(value = 5, message = "count는 100 이하여야 합니다.")
    Integer count;
}
