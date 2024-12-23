package gg.updown.backend.main.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
@Slf4j
public class SiteCommonException extends RuntimeException  {
    private final HttpStatusCode statusCode;
    private String exceptionMessage;
    private String devMessage;
    private String userMessage;

    public SiteCommonException(HttpStatusCode statusCode, String exceptionMessage) {
        super(exceptionMessage);
        this.statusCode = statusCode;
    }

    public SiteCommonException(HttpStatusCode statusCode, String exceptionMessage, String devMessage, String userMessage) {
        super(exceptionMessage);
        this.statusCode = statusCode;
        this.devMessage = devMessage;
        this.userMessage = userMessage;
    }
}
