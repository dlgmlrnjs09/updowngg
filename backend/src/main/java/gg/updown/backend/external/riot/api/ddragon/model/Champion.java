package gg.updown.backend.external.riot.api.ddragon.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Champion {
    private String nameUs;
    private String nameKr;
    private String key;
}
