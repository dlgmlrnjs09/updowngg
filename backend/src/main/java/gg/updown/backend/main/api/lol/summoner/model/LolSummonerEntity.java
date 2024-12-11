package gg.updown.backend.main.api.lol.summoner.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolSummonerEntity {
    private Integer lolAccountSeq;
    private String puuid;
    private String summonerId;
    private String accountId;
    private Long summonerLevel;
    private LocalDateTime externalModDt;
    private String profileIconId;
}
