package gg.updown.backend.main.enums;

import lombok.Getter;

@Getter
public enum SiteTagSuggestStatus {
    PENDING("PENDING", "확인중"),
    APPROVED("APPROVED", "승인완료"),
    REJECTED("JRECTED", "승인거절");

    private final String statusName;
    private final String statusDesciption;

    private SiteTagSuggestStatus(String statusName, String statusDesciption) {
        this.statusName = statusName;
        this.statusDesciption = statusDesciption;
    }
}
