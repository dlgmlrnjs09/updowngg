package gg.updown.backend.main.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public enum SiteMatchFilter {
    ALL("ALL", "전체"),
    RANKED_SOLO_5x5("RANKED_SOLO_5x5", "솔로랭크"),
    RANKED_FLEX_SR("RANKED_FLEX_SR", "자유랭크"),
    ARAM("ARAM", "무작위 총력전"),
    NORMAL("NORMAL", "일반"),
    BOT("BOT", "AI 대전"),
    URF("URF", "우르프"),
    CLASH("CLASH", "격전"),
    ARENA("ARENA", "아레나"),
    NEXUS_BLITZ("NEXUS_BLITZ", "돌격! 넥서스"),
    SPECIAL("SPECIAL", "특별 게임모드");

    private final String code;
    private final String displayName;

    SiteMatchFilter(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public List<Integer> getQueueIds() {
        switch (this) {
            case ALL:
                return Collections.emptyList(); // 전체 선택시 필터링 하지 않음
            case RANKED_SOLO_5x5:
                return Collections.singletonList(420);
            case RANKED_FLEX_SR:
                return Collections.singletonList(440);
            case ARAM:
                return Collections.singletonList(450);
            case NORMAL:
                return Arrays.asList(400, 430); // 일반게임, 일반게임(드래프트)
            case BOT:
                return Arrays.asList(830, 840, 850); // AI 대전 입문, 초급, 중급
            case URF:
                return Collections.singletonList(900);
            case CLASH:
                return Collections.singletonList(700);
            case ARENA:
                return Collections.singletonList(1700);
            case NEXUS_BLITZ:
                return Collections.singletonList(1300);
            case SPECIAL:
                return Arrays.asList(1020, 1200, 1400); // 단일챔피언, 집중포화, 궁극기 주문서
            default:
                return Collections.emptyList();
        }
    }
}