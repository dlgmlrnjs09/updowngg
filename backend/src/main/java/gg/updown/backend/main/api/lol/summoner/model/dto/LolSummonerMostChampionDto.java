package gg.updown.backend.main.api.lol.summoner.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LolSummonerMostChampionDto {
    private String nameUs;
    private String nameKr;
    private int playCount;
    private int winCount;
    private int winRate;
    private int upCount;
    private int downCount;
    private String iconUrl;
}
