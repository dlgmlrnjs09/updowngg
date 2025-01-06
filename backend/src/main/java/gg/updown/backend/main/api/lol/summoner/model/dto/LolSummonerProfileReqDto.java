package gg.updown.backend.main.api.lol.summoner.model.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Setter
public class LolSummonerProfileReqDto {
    @Schema(description = "라이엇 닉네임", required = true, example = "조용조용조용필")
    @NotNull(message = "summonerId는 필수값입니다. | 소환사 검색은 플레이어이름#태그 형식으로 입력해주세요.")
    String summonerId;

    @Schema(description = "라이엇 배틀태그", required = true, example = "KR2")
    @NotNull(message = "tagLine은 필수값입니다. | 소환사 검색은 플레이어이름#태그 형식으로 입력해주세요.")
    String tagLine;
}
