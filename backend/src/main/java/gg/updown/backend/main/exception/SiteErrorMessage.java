package gg.updown.backend.main.exception;

import lombok.Getter;

@Getter
public enum SiteErrorMessage {
    INTERNAL_RIOT_SERVER_ERROR("현재 라이엇 서버가 불안정합니다. 다시 시도해주세요."),

    ALREADY_PARTY_POSITION("이미 마감된 포지션입니다."),
    PARTICIPATED_ANOTHER_POSITION("이미 참가한 포지션이 있습니다."),
    NOT_PARTY_READER("파티장만 신청 승인/거절할 수 있습니다.");

    private final String message;

    SiteErrorMessage(String message) {
        this.message = message;
    }
}
