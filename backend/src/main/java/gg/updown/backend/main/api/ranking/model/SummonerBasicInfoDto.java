package gg.updown.backend.main.api.ranking.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SummonerBasicInfoDto {
    private String puuid;
    private String gameName;
    private String tagLine;
    private String profileIconId;
    private String profileIconUrl;
}