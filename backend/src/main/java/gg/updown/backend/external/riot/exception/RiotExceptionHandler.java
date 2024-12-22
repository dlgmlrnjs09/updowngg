package gg.updown.backend.external.riot.exception;


import gg.updown.backend.common.model.CommonErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class RiotExceptionHandler {

    @ExceptionHandler(RiotApiException.class)
    public ResponseEntity<CommonErrorResponse> handleRiotApiException(RiotApiException e) {
        CommonErrorResponse errorResponse = new CommonErrorResponse(e.getMessage(), null);

        if (e.getMessage() != null) {
            errorResponse.setReturnMessage(e.getMessage());
        } else {
            errorResponse.setReturnMessage("데이터를 읽어오는 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }

        return ResponseEntity
                .status(HttpStatus.valueOf(e.getStatusCode().value()))
                .body(errorResponse);
    }

}
