package gg.updown.backend.main.api.lol.match.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LolMatchTogetherReqDto {
    @Schema(description = "라이엇 계정 고유ID")
    @NotNull(message = "targetPuuid는 필수값입니다.")
    private String targetPuuid;

    @Schema(description = "조회 시작일자", example = "20240101")
    @NotNull(message = "시작일자는 필수값입니다.")
    @Pattern(regexp = "^\\d{8}$", message = "시작일자는 YYYYMMDD 형식이어야 합니다.")
    private String startDate;

    @Schema(description = "조회 종료일자", example = "20240131")
    @NotNull(message = "종료일자는 필수값입니다.")
    @Pattern(regexp = "^\\d{8}$", message = "종료일자는 YYYYMMDD 형식이어야 합니다.")
    private String endDate;
}
