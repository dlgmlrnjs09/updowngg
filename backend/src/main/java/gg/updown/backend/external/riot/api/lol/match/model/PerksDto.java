package gg.updown.backend.external.riot.api.lol.match.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List; /** 룬 정보 DTO */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerksDto {
    /** 기본 스탯 룬 정보 */
    private StatPerksDto statPerks;

    /** 선택한 룬 스타일들 */
    private List<StyleDto> styles;

    /** 기본 스탯 룬 정보 DTO */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatPerksDto {
        /** 방어 스탯 룬 ID */
        private int defense;

        /** 유연성 스탯 룬 ID */
        private int flex;

        /** 공격 스탯 룬 ID */
        private int offense;
    }

    /** 룬 스타일 정보 DTO */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StyleDto {
        /** 룬 스타일 설명 */
        private String description;

        /** 선택한 룬들 */
        private List<SelectionDto> selections;

        /** 룬 스타일 ID (정밀, 지배 등) */
        private int style;

        /** 개별 룬 선택 정보 DTO */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SelectionDto {
            /** 선택한 룬 ID */
            private int perk;

            /** 룬 효과 변수 1 */
            private int var1;

            /** 룬 효과 변수 2 */
            private int var2;

            /** 룬 효과 변수 3 */
            private int var3;
        }
    }
}
