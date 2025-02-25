package gg.updown.backend.main.enums;

import lombok.Getter;

@Getter
public enum SitePartyApplyStatus {
    PENDING("PENDING", "대기중"),
    APPROVE("APPROVE", "승인"),
    REJECT("REJECT", "거절"),
    CANCEL("CANCEL", "취소");

    private final String status;
    private final String description;

    SitePartyApplyStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }
}
