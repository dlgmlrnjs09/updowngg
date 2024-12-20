package gg.updown.backend.main.api.lol.match.model;

import gg.updown.backend.main.api.review.model.ReviewDto;
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
    // 리뷰작성가능 여부
//    private boolean isReviewable;
}
