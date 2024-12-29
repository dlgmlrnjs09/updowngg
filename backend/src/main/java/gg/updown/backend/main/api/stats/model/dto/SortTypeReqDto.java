package gg.updown.backend.main.api.stats.model.dto;

import gg.updown.backend.external.riot.api.lol.league.enums.Tier;
import gg.updown.backend.external.riot.api.lol.match.enums.MatchPosition;
import gg.updown.backend.external.riot.api.lol.match.enums.MatchGameMode;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortTypeReqDto {
    private MatchGameMode matchGameMode;
    private MatchPosition matchPosition;
    private Tier tier;
}
