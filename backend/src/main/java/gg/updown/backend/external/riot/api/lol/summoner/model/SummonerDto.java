package gg.updown.backend.external.riot.api.lol.summoner.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummonerDto {
    private String accountId;
    private String profileIconId;
    private Long revisionDate;
    private String id;
    private String puuid;
    private Long summonerLevel;
}
