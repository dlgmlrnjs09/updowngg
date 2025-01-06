package gg.updown.backend.main.api.lol.summoner.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "LOL 소환사 정보 DTO")
public class LolSummonerDto {
    @Schema(description = "사이트 소환사 Seq")
    private Integer lolAccountSeq;
    @Schema(description = "Riot 소환사 고유 ID")
    private String summonerId;
    @Schema(description = "LOL 계정 고유 ID")
    private String accountId;
    @Schema(description = "LOL 소환사 레벨")
    private Long summonerLevel;
    @Schema(description = "Riot에서의 마지막 수정일시")
    private LocalDateTime externalModDt;
    @Schema(description = "LOL 프로필 아이콘 ID")
    private String profileIconId;
    @Schema(description = "LOL 프로필 아이콘 URL")
    private String profileIconUrl;
}
