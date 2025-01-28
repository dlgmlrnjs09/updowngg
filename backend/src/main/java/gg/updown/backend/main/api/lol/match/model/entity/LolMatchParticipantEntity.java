package gg.updown.backend.main.api.lol.match.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolMatchParticipantEntity {
    private int matchParticipantSeq;
    private String matchId;
    private String puuid;
    private String riotIdGameName;
    private String riotIdTagline;
    private String position;
    private String summonerId;
    private int summonerLevel;
    private String summonerName;
    private int teamId;
    private int timePlayed;
    private int kills;
    private int assists;
    private int deaths;
    private int champLevel;
    private int champId;
    private String champName;
    private int totalDamageToChampion;
    private int totalDamageTaken;
    private Boolean isWin;
}
