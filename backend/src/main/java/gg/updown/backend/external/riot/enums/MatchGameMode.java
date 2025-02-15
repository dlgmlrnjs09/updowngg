package gg.updown.backend.external.riot.enums;

import lombok.Getter;

@Getter
public enum MatchGameMode {
    ARAM(450, "칼바람나락"),
    NORMAL(430, "일반게임"),
    NORMAL_DRAFT(400, "일반게임(드래프트)"),
    QUICK_PLAY(490, "빠른 대전"),
    SOLO_RANK(420, "개인랭크"),
    FLEX_RANK(440, "자유랭크"),
    BOT_INTRO(830, "AI 대전 입문"),
    BOT_BEGINNER(840, "AI 대전 초급"),
    BOT_INTERMEDIATE(850, "AI 대전 중급"),
    CLASH(700, "클래시"),
    URF(900, "우르프"),
    ONE_FOR_ALL(1020, "단일챔피언"),
    NEXUS_BLITZ(1300, "넥서스 블리츠"),
    ULTIMATE_SPELLBOOK(1400, "궁극기 주문서");

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
