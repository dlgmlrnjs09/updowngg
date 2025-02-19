package gg.updown.backend.main.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SiteMatchGameMode {
    ALL("ALL", "전체", "", 0),
    ARAM("ARAM", "칼바람나락", "", 450),
    NORMAL("NORMAL", "일반게임", "", 430),
    NORMAL_DRAFT("NORMAL_DRAFT", "일반게임(드래프트)", "", 400),
    QUICK_PLAY("QUICK_PLAY", "빠른 대전", "", 490),
    SOLO_RANK("SOLO_RANK", "개인랭크", "RANKED_SOLO_5x5", 420),
    FLEX_RANK("FLEX_RANK", "자유랭크", "RANKED_FLEX_SR", 440),
    BOT_INTRO("BOT_INTRO", "AI 대전 입문", "", 830),
    BOT_BEGINNER("BOT_BEGINNER", "AI 대전 초급", "", 840),
    BOT_INTERMEDIATE("BOT_INTERMEDIATE", "AI 대전 중급", "", 850),
    CLASH("CLASH", "클래시", "", 700),
    URF("URF", "우르프", "", 900),
    ONE_FOR_ALL("ONE_FOR_ALL", "단일챔피언", "", 1020),
    NEXUS_BLITZ("NEXUS_BLITZ", "넥서스 블리츠", "", 1300),
    ULTIMATE_SPELLBOOK("ULTIMATE_SPELLBOOK", "궁극기 주문서", "", 1400),
    ARENA("ARENA", "아레나", "", 1700),
    CHERRY("CHERRY", "집중포화", "", 1200);

    private final String queueCode;
    private final String queueName;
    private final String leagueName;
    private final int queueId;

    SiteMatchGameMode(String queueCode, String queueName, String leagueName, int queueId) {
        this.queueCode = queueCode;
        this.queueName = queueName;
        this.leagueName = leagueName;
        this.queueId = queueId;
    }

    public static SiteMatchGameMode findByQueueCode(String queueCode) {
        return Arrays.stream(values())
                .filter(mode -> mode.getQueueCode().equals(queueCode))
                .findFirst()
                .orElse(null);
    }

    public static SiteMatchGameMode findByLeagueName(String leagueName) {
        return Arrays.stream(values())
                .filter(mode -> mode.getLeagueName().equals(leagueName))
                .findFirst()
                .orElse(null);
    }

    public static SiteMatchGameMode findByQueueId(int queueId) {
        return Arrays.stream(values())
                .filter(mode -> mode.getQueueId() == queueId)
                .findFirst()
                .orElse(null);
    }

    public boolean isRankGame() {
        return this == SOLO_RANK || this == FLEX_RANK;
    }
}
