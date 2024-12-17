package gg.updown.backend.external.riot.api.lol.match.enums;

public enum MatchQueueId {
    ARAM(450, "칼바람나락"),
    NORMAL(430, "일반게임"),
    SOLO_RANK(420, "개인랭크"),
    FLEX_RANK(440, "자유랭크");

    private final int queueId;
    private final String queueName;

    MatchQueueId(int queueId, String queueName) {
        this.queueId = queueId;
        this.queueName = queueName;
    }

    public static String getQueueName(int queueId) {
        for (MatchQueueId mode : values()) {
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
