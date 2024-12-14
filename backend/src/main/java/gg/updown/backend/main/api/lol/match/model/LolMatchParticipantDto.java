package gg.updown.backend.main.api.lol.match.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolMatchParticipantDto {
    private String matchId;
    private String puuid;
    private String riotIdGameName;
    private String riotIdTagline;
    private String role;
    private int teamId;
    private String teamName;
    private int kills;
    private int assists;
    private int deaths;
    private int champLevel;
    private int champId;
    private String champName;
    private String champProfileIconUrl;
    private int totalDamageToChampion;
    private int totalDamageTaken;
}
