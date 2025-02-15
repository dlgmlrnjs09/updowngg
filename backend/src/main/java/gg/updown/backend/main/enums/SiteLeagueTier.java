package gg.updown.backend.main.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SiteLeagueTier {

    IRON("IRON", "아이언", "I", "#545454"),           // 회색
    BRONZE("BRONZE", "브론즈", "B", "#A46628"),       // 구리색
    SILVER("SILVER", "실버", "S", "#96A4B0"),         // 은색
    GOLD("GOLD", "골드", "G", "#F1A64D"),            // 금색
    PLATINUM("PLATINUM", "플래티넘", "P", "#4FC1AA"),  // 청록색
    EMERALD("EMERALD", "에메랄드", "E", "#00BF8C"),   // 에메랄드색
    DIAMOND("DIAMOND", "다이아몬드", "D", "#576BCE"),  // 파란색
    MASTER("MASTER", "마스터", "M", "#9D4DC5"),       // 보라색
    GRANDMASTER("GRANDMASTER", "그랜드마스터", "GM", "#E84057"), // 빨간색
    CHALLENGER("CHALLENGER", "챌린저", "C", "#F4C874"); // 황금색

    private final String tierCode;
    private final String tierName;
    private final String acronym;
    private final String color;

    SiteLeagueTier(String tierCode, String tierName, String acronym, String color) {
        this.tierCode = tierCode;
        this.tierName = tierName;
        this.acronym = acronym;
        this.color = color;
    }

    public static SiteLeagueTier findByTierCode(String tierCode) {
        return Arrays.stream(SiteLeagueTier.values())
                .filter(t -> t.getTierCode().equals(tierCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid position code: " + tierCode));
    }
}
