package gg.updown.backend.main.api.lol.summoner.model;

import gg.updown.backend.main.riot.account.model.RiotAccountInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LolSummonerProfileResDto {
    // lol_summoner
//    private Integer lolAccountSeq;
//    private String puuid;
//    private String summonerId;
//    private String accountId;
//    private Long summonerLevel;
//    private LocalDateTime externalModDt;
//    private String profileIconId;
//    private String profileIconUrl;

    private LolSummonerDto lolSummonerDto;
    private RiotAccountInfoEntity riotAccountInfoEntity;
}
