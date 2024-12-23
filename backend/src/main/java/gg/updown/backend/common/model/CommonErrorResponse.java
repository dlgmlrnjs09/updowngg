package gg.updown.backend.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommonErrorResponse {
    private String devMessage;
    private String userMessage;
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();
}