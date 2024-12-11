package gg.updown.backend.external.riot.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(RiotApiException.class)
    public ResponseEntity<CommonErrorResponse> handleRiotApiException(RiotApiException e) {
        CommonErrorResponse error = new CommonErrorResponse(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.valueOf(e.getCode()))
                .body(error);
    }

}
