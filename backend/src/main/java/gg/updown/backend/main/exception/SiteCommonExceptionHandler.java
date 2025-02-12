package gg.updown.backend.main.exception;


import gg.updown.backend.common.model.CommonErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import org.springframework.security.access.AccessDeniedException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class SiteCommonExceptionHandler {

    @ExceptionHandler(SiteCommonException.class)
    public ResponseEntity<CommonErrorResponse> handleSiteCommonApiException(SiteCommonException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.valueOf(e.getStatusCode().value()))
                .body(CommonErrorResponse.builder()
                    .devMessage(e.getDevMessage())
                    .userMessage(e.getUserMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonErrorResponse> handleGenericException(Exception e, WebRequest request) {
        log.error(e.getMessage(), e);
        // 일반적인 예외에 대해 공통적인 응답 생성
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonErrorResponse.builder()
                        .devMessage(SiteErrorDevMessage.INTERNAL_SERVER_ERROR.getMessage())
                        .userMessage(SiteErrorDevMessage.INTERNAL_SERVER_ERROR.getMessage())
                .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CommonErrorResponse.builder()
                        .devMessage("로그인이 필요한 서비스입니다.")
                        .userMessage("로그인이 필요한 서비스입니다.")
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        // | 를 기준으로 메시지 분리
        String devMessage = errorMessage;
        String userMessage = null;

        if (errorMessage.contains("|")) {
            String[] messages = errorMessage.split("\\|");
            devMessage = messages[0].trim();
            userMessage = messages.length > 1 ? messages[1].trim() : null;
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonErrorResponse.builder()
                        .devMessage(devMessage)
                        .userMessage(userMessage)
                        .build());
    }

}
