package gg.updown.backend.external.riot.api.lol.match.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/** 매치 데이터의 최상위 DTO */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchDto {
    /** 매치의 기본 메타데이터 */
    private MetadataDto metadata;

    /** 매치의 상세 정보 */
    private InfoDto info;
}

