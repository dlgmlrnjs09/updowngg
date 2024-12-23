package gg.updown.backend.main.api.lol.summoner.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Setter
public class LolSummonerProfileReqDto {
    @NotNull(message = "summonerId는 필수값입니다. | 소환사 검색은 플레이어이름#태그 형식으로 입력해주세요.")
    String summonerId;

    @NotNull(message = "tagLine은 필수값입니다. | 소환사 검색은 플레이어이름#태그 형식으로 입력해주세요.")
    String tagLine;
}
