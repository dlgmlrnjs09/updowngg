package gg.updown.backend.main.enums;

import lombok.Getter;

@Getter
public enum SiteMatchPosition {
    TOP("TOP","탑", "TOP"),
    JUNGLE("JG", "정글", "JUNGLE"),
    MIDDLE("MID", "미드", "MIDDLE"),
    BOTTOM("AD", "원딜", "BOTTOM"),
    UTILITY("SUP", "서폿", "UTILITY");

    private final String code;
    private final String nameKr;
    private final String nameEn;


    SiteMatchPosition(String code, String nameKr, String nameEn) {
        this.code = code;
        this.nameKr = nameKr;
        this.nameEn = nameEn;
    }

}
