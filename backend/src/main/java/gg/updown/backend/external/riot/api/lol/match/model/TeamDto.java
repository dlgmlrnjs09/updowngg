package gg.updown.backend.external.riot.api.lol.match.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List; /** 팀 정보 DTO */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDto {
    /** 챔피언 밴 정보 리스트 */
    private List<BanDto> bans;

    /** 팀 목표물 처치 정보 */
    private ObjectivesDto objectives;

    /** 팀 ID (100: 블루팀, 200: 레드팀) */
    private int teamId;

    /** 승리 여부 */
    private boolean win;

    /** 챔피언 밴 정보 DTO */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BanDto {
        /** 밴한 챔피언 ID */
        private int championId;

        /** 밴 픽 순서 */
        private int pickTurn;
    }

    /** 팀 목표물 처치 정보 DTO */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ObjectivesDto {
        /** 바론 처치 정보 */
        private ObjectiveDto baron;

        /** 챔피언 처치 정보 */
        private ObjectiveDto champion;

        /** 드래곤 처치 정보 */
        private ObjectiveDto dragon;

        /** 억제기 파괴 정보 */
        private ObjectiveDto inhibitor;

        /** 전령 처치 정보 */
        private ObjectiveDto riftHerald;

        /** 포탑 파괴 정보 */
        private ObjectiveDto tower;

        /** 개별 목표물 처치 정보 DTO */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ObjectiveDto {
            /** 첫 처치 여부 */
            private boolean first;

            /** 처치 횟수 */
            private int kills;
        }
    }
}
