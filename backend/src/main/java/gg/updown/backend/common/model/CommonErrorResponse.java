package gg.updown.backend.common.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommonErrorResponse {
    private final String message;
    private final LocalDateTime timestamp;
    private String returnMessage;

    public CommonErrorResponse(String message, String returnMessage) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.returnMessage = returnMessage;
    }
}
