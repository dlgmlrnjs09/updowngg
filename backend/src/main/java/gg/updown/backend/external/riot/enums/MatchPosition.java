package gg.updown.backend.external.riot.enums;

public enum MatchPosition {
    TOP("탑"),
    JUNGLE("정글"),
    MIDDLE("미드"),
    BOTTOM("원딜"),
    UTILITY("서폿");

    private final String nameKr;

    MatchPosition(String nameKr) {
        this.nameKr = nameKr;
    }

}
