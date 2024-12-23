package gg.updown.backend.main.api.lol.match.model.dto;

import gg.updown.backend.main.api.review.model.dto.ReviewDto;
import gg.updown.backend.main.api.review.model.dto.ReviewStatsDto;
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
    private String position;
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

    // 리뷰정보 객체
    private ReviewDto reviewDto;
    // 리뷰평점 객체
    private ReviewStatsDto reviewStatsDto;
}
