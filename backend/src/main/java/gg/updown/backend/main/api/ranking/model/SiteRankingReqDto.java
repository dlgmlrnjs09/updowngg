package gg.updown.backend.main.api.ranking.model;

import gg.updown.backend.main.api.stats.model.dto.SortTypeReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class SiteRankingReqDto extends SortTypeReqDto {
    @Schema(description = "라이엇 계정 고유ID")
    private String puuid;
    @Schema(description = "검색할 랭커 수")
    private int limit;
    @Schema(description = "검색시작 Index")
    private int offset;
}
