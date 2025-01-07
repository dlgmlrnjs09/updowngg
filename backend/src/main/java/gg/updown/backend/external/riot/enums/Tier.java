package gg.updown.backend.external.riot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tier {
    CHALLENGER("CHALLENGER", "챌린저"),
    GRANDMASTER("GRANDMASTER", "그랜드마스터"),
    MASTER("MASTER", "마스터"),
    DIAMOND("DIAMOND", "다이아몬드"),
    EMERALD("EMERALD", "에메랄드"),
    PLATINUM("PLATINUM", "플래티넘"),
    GOLD("GOLD", "골드"),
    SILVER("SILVER", "실버"),
    BRONZE("BRONZE", "브론즈"),
    IRON("IRON", "아이언");

    private final String nameEn;
    private final String nameKr;
}
