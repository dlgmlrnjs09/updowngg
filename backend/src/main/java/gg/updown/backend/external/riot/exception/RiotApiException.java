package gg.updown.backend.external.riot.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
public class RiotApiException extends RuntimeException  {
    public static String defaultMessage = "데이터를 읽어오는 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.";
    private final HttpStatusCode statusCode;
//    private final String code;    // 필요한 경우 에러 코드

    public RiotApiException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
