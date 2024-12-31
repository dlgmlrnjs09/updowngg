package gg.updown.backend.main.api.ranking.model;

import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummonerTagDto {
    private String puuid;
    private List<ReviewTagDto> tagDtoList;
}
