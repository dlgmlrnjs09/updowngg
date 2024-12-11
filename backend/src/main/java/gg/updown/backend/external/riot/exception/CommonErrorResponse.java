package gg.updown.backend.external.riot.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommonErrorResponse {
    private final String message;
    private final LocalDateTime timestamp;

    public CommonErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
