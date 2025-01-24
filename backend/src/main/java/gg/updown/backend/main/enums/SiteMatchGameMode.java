package gg.updown.backend.main.enums;

import lombok.Getter;

import java.util.Arrays;

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

    public static SiteMatchGameMode findByQueueCode(String queueCode) {
        return Arrays.stream(values())
                .filter(mode -> mode.getQueueCode().equals(queueCode))
                .findFirst()
                .orElse(null);
    }
}
