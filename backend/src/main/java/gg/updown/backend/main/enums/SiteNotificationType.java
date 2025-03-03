package gg.updown.backend.main.enums;

import lombok.Getter;

@Getter
public enum SiteNotificationType {
    SITE_REVIEW("SITE_REVIEW", "review-notification", "리뷰"),
    PARTY_COMMUNITY("PARTY_COMMUNITY", "party-notification", "파티 커뮤니티");

    private final String code;
    private final String eventName;
    private final String description;

    SiteNotificationType(String code, String eventName, String description) {
        this.code = code;
        this.description = description;
        this.eventName = eventName;
    }
}
