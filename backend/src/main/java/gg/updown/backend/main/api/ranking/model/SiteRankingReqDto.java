package gg.updown.backend.main.api.ranking.model;

import gg.updown.backend.main.api.stats.model.dto.SortTypeReqDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class SiteRankingReqDto extends SortTypeReqDto {
//    private SortTypeReqDto sortTypeReqDto;
    private String puuid;
    private int limit;
    private int offset;
}
