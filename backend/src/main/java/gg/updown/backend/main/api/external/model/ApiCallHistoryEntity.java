package gg.updown.backend.main.api.external.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCallHistoryEntity {
    private Integer seq;
    private String type;
    private String url;
    private String request;
    private String response;
    private LocalDateTime regDt;
}