package gg.updown.backend.external.riot.exception;


import gg.updown.backend.common.model.CommonErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
@Slf4j
public class RiotExceptionHandler {

    @ExceptionHandler(RiotApiException.class)
    public ResponseEntity<CommonErrorResponse> handleRiotApiException(RiotApiException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.valueOf(e.getStatusCode().value()))
                .body(CommonErrorResponse.builder()
                        .userMessage(e.getMessage())
                        .devMessage(e.getMessage())
                    .build());
    }

}
