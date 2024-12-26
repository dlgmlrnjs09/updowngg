package gg.updown.backend.main.api.stats.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ChampionResDto {
    private String nameUs;
    private String nameKr;
    private String iconUrl;
}
