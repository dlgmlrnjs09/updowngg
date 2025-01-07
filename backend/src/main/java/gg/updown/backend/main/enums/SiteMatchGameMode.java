package gg.updown.backend.main.enums;

import lombok.Getter;

@Getter
public enum SiteMatchGameMode {
    ARAM("ARAM", "칼바람나락"),
    NORMAL("NORMAL", "일반게임"),
    SOLO_RANK("SOLO_RANK", "개인랭크"),
    FLEX_RANK("FLEX_RANK", "자유랭크");

    private final String queueCode;
    private final String queueName;

    SiteMatchGameMode(String queueCode, String queueName) {
        this.queueCode = queueCode;
        this.queueName = queueName;
    }
}
