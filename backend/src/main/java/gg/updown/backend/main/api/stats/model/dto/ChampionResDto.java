package gg.updown.backend.main.api.stats.model.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampionResDto {
    private String nameUs;
    private String nameKr;
    private String iconUrl;
    private Long playCount;
}
