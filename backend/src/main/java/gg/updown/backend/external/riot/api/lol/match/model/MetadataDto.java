package gg.updown.backend.external.riot.api.lol.match.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List; /** 매치의 메타데이터 정보 DTO */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataDto {
    /** 데이터 버전 */
    private String dataVersion;

    /** 매치 ID */
    private String matchId;

    /** 매치에 참여한 플레이어들의 PUUID 리스트 */
    private List<String> participants;
}
