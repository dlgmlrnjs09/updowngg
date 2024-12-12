package gg.updown.backend.external.riot.api.lol.match.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMatchIdListReqDto {
    private String puuid;
    /**
     * 2021년 6월 16일 이후 데이터만 가능
     */
    private Long startTime;
    private Long endTime;
    /**
     * 특정 큐 ID로 매치 ID 목록을 필터링합니다.
     */
    private Integer queue;
    /**
     * 매치 ID 목록을 매치 유형별로 필터링합니다
     */
    private String type;
    /**
     * 기본값은 0입니다. 시작 인덱스.
     */
    private Integer start;
    /**
     * 기본값은 20입니다. 유효한 값: 0~100. 반환할 일치 ID 수입니다.
     */
    private Integer count;
}
