package gg.updown.backend.main.enums;

import lombok.Getter;

@Getter
public enum SiteLeagueTier {

    IRON("IRON", "아이언"),
    BRONZE("BRONZE", "브론즈"),
    SILVER("SILVER", "실버"),
    GOLD("GOLD", "골드"),
    PLATINUM("PLATINUM", "플래티넘"),
    EMERALD("EMERALD", "에메랄드"),
    DIAMOND("DIAMOND", "다이아몬드"),
    MASTER("MASTER", "마스터"),
    GRANDMASTER("GRANDMASTER", "그랜드마스터"),
    CHALLENGER("CHALLENGER", "챌린저");

    private final String tierCode;
    private final String tierName;

    SiteLeagueTier(String tierCode, String tierName) {
        this.tierCode = tierCode;
        this.tierName = tierName;
    }
}
