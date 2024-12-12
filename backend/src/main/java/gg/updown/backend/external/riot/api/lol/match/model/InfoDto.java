package gg.updown.backend.external.riot.api.lol.match.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List; /** 매치의 상세 정보 DTO */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfoDto {
    /** 게임 생성 시간 (Unix Timestamp in milliseconds) */
    private long gameCreation;

    /** 게임 진행 시간 (seconds) */
    private long gameDuration;

    /** 게임 종료 시간 (Unix Timestamp in milliseconds) */
    private long gameEndTimestamp;

    /** 게임 ID */
    private long gameId;

    /** 게임 모드 (CLASSIC, ARAM 등) */
    private String gameMode;

    /** 게임 이름 */
    private String gameName;

    /** 게임 시작 시간 (Unix Timestamp in milliseconds) */
    private long gameStartTimestamp;

    /** 게임 타입 */
    private String gameType;

    /** 게임 버전 */
    private String gameVersion;

    /** 맵 ID */
    private int mapId;

    /** 게임 참가자들의 상세 정보 리스트 */
    private List<ParticipantDto> participants;

    /** 플랫폼 ID (서버 지역) */
    private String platformId;

    /** 큐 ID (게임 타입) */
    private int queueId;

    /** 팀 정보 리스트 */
    private List<TeamDto> teams;

    /** 토너먼트 코드 (토너먼트 게임인 경우) */
    private String tournamentCode;
}
