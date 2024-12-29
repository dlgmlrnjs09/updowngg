package gg.updown.backend.main.api.lol.summoner.model.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolSummonerLeagueEntity {
    private String leagueId;
    private String summonerId;
    private String queueType;
    private String tier;
    private String rank;
    private Integer leaguePoints;
    private Integer wins;
    private Integer losses;
    private Boolean hotStreak;
    private Boolean veteran;
    private Boolean freshBlood;
    private Boolean inactive;
    private LocalDateTime regDt;
    private LocalDateTime updDt;
}
