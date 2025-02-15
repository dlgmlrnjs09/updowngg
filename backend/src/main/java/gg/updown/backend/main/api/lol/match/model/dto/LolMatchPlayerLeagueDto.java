package gg.updown.backend.main.api.lol.match.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolMatchPlayerLeagueDto {
    private String tier;
    private String acronymTier;
    private String tierColor;
    private String rank;
}
