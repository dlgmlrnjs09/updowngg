package gg.updown.backend.main.api.search.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteSearchSummonerDto {
    private String gameName;
    private String tagLine;
    private String profileIconId;
    private String profileIconUrl;
    private long upCount;
    private long downCount;
}
