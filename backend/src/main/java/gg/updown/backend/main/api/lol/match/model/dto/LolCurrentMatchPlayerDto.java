package gg.updown.backend.main.api.lol.match.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolCurrentMatchPlayerDto {
    private String puuid;
    private long championId;
    private String championIconUrl;
    private long teamId;
    private LolMatchPlayerLeagueDto leagueDto;
}
