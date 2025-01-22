package gg.updown.backend.main.api.ranking.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Schema(description = "LOL 소환사 기본정보")
public class SummonerBasicInfoDto {
    @Schema(description = "라이엇 계정 고유ID")
    private String puuid;
    @Schema(description = "라이엇 닉네임")
    private String gameName;
    @Schema(description = "라이엇 배틀태그")
    private String tagLine;
    @Schema(description = "LOL 프로필 아이콘 ID")
    private String profileIconId;
    @Schema(description = "LOL 프로필 아이콘 URL")
    private String profileIconUrl;
}