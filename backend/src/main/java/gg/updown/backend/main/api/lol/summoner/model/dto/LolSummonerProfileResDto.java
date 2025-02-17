package gg.updown.backend.main.api.lol.summoner.model.dto;

import gg.updown.backend.main.riot.account.model.RiotAccountInfoEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "LOL 소환사정보 DTO")
public class LolSummonerProfileResDto {
    private LolSummonerDto lolSummonerDto;
    private RiotAccountInfoEntity riotAccountInfoEntity;
    private List<LolSummonerLeagueDto> leagueEntityList;
}
