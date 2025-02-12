package gg.updown.backend.main.exception;

import lombok.Getter;

@Getter
public enum SiteErrorMessage {
    INTERNAL_RIOT_SERVER_ERROR("현재 라이엇 서버가 불안정합니다. 다시 시도해주세요.");


    private final String message;

    SiteErrorMessage(String message) {
        this.message = message;
    }
}
