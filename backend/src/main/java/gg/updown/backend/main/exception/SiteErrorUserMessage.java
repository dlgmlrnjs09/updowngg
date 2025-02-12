package gg.updown.backend.main.exception;

import lombok.Getter;

@Getter
public enum SiteErrorUserMessage {
    EXPIRED_TOKEN("인증이 만료되었습니다. 다시 로그인해주세요."),
    INVALID_TOKEN("유효하지 않은 인증입니다. 다시 로그인해주세요.");


    private final String message;

    SiteErrorUserMessage(String message) {
        this.message = message;
    }
}
