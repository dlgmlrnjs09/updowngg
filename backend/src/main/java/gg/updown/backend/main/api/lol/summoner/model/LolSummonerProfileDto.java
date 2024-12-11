package gg.updown.backend.main.api.lol.summoner.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolSummonerProfileDto {
    // 라이엇 계정정보
    private String puuid;
    private String gameName;
    private String tagLine;

    // 롤 계정정보
    private Integer lolAccountSeq;
    private String summonerId;
    private String accountId;
    private Long summonerLevel;
    private LocalDateTime externalModDt;
    private String profileIconId;
    private String profileIconUrl;


}
