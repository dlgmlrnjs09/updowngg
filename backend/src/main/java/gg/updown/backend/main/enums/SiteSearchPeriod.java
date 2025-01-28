package gg.updown.backend.main.enums;

import lombok.Getter;

@Getter
public enum SiteSearchPeriod {
    ALL("ALL", "전체"),
    TODAY("TODAY", "오늘"),
    WEEK("WEEK", "최근 7일"),
    MONTH("MONTH", "최근 1달");

    private final String value;
    private final String description;

    SiteSearchPeriod(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
