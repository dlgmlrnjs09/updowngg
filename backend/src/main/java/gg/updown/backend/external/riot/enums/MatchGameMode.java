package gg.updown.backend.external.riot.enums;

public enum MatchGameMode {
    ARAM(450, "칼바람나락"),
    NORMAL(430, "일반게임"),
    SOLO_RANK(420, "개인랭크"),
    FLEX_RANK(440, "자유랭크");

    private final int queueId;
    private final String queueName;

    MatchGameMode(int queueId, String queueName) {
        this.queueId = queueId;
        this.queueName = queueName;
    }

    public static String getQueueName(int queueId) {
        for (MatchGameMode mode : values()) {
            if (mode.queueId == queueId) {
                return mode.queueName;
            }
        }
        return null;
    }

    public String getModeName() {
        return queueName;
    }
}
