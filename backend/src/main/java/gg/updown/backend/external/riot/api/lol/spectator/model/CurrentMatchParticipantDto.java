package gg.updown.backend.external.riot.api.lol.spectator.model;

import gg.updown.backend.external.riot.api.lol.match.model.PerksDto;
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
@Schema(description = "게임 참가자 상세 정보")
public class CurrentMatchParticipantDto {
    @Schema(description = "플레이중인 챔피언의 고유 식별 번호", example = "84")
    private long championId;

    @Schema(description = "선택된 룬/페어크 정보")
    private PerksDto perks;

    @Schema(description = "참가자의 프로필 아이콘 식별 번호", example = "4711")
    private long profileIconId;

    @Schema(description = "해당 참가자가 봇인지 여부", example = "false")
    private boolean bot;

    @Schema(description = "참가자의 팀 식별 번호", example = "100")
    private long teamId;

    @Schema(description = "소환사의 암호화된 고유 식별자", example = "암호화된소환사ID")
    private String summonerId;

    @Schema(description = "소환사의 암호화된 범용 고유 식별자", example = "암호화된PUUID")
    private String puuid;

    @Schema(description = "첫 번째 소환사 주문의 식별 번호", example = "14")
    private long spell1Id;

    @Schema(description = "두 번째 소환사 주문의 식별 번호", example = "4")
    private long spell2Id;

    @Schema(description = "게임 맞춤 설정 목록")
    private List<MatchCustomizationObjectDto> gameCustomizationObjects;
}
