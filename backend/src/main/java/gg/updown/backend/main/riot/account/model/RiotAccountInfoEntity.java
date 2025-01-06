package gg.updown.backend.main.riot.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "라이엇 계정정보 DTO")
public class RiotAccountInfoEntity {
    @Schema(description = "Riot 계정 고유 ID")
    private String puuid;
    @Schema(description = "Riot 닉네임")
    private String gameName;
    @Schema(description = "Riot 배틀태그")
    private String tagLine;
}
