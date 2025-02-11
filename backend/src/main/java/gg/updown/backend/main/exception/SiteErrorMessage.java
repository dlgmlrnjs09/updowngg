package gg.updown.backend.main.exception;

import lombok.Getter;

@Getter
public enum SiteErrorMessage {
    NOT_FOUND_RIOT_ACCOUNT("존재하지 않는 라이엇 계정입니다."),
    NOT_FOUND_SITE_ACCOUNT("존재하지 않는 사용자입니다."),

    INVALID_TOKEN("토큰 정보가 유효하지 않습니다."),

    NOT_REVIEWER("리뷰 작성자가 아니거나 리뷰가 존재하지 않습니다."),

    INTERNAL_SERVER_ERROR("데이터를 읽어오는 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");


    private final String message;

    SiteErrorMessage(String message) {
        this.message = message;
    }
}
