package gg.updown.backend.main.api.stats.model.dto;

import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampionStatsTagDto {
    private String nameUs;
    private List<ReviewTagDto> tagDtoList;
}
