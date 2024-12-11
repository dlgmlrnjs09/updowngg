package gg.updown.backend.external.riot.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RiotApiException extends RuntimeException  {
    private final HttpStatus status;
    private final String code;    // 필요한 경우 에러 코드

    public RiotApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.code = null;
    }

    public RiotApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
