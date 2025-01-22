package gg.updown.backend.main.api.review.model.entity;

import gg.updown.backend.main.enums.SiteTagSuggestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewTagSuggestEntity {
    private int suggestSeq;
    private String tagValue;
    private String tagDescription;
    private String tagCategory;
    private boolean isUp;
    private SiteTagSuggestStatus status;
    private String reason;
    private LocalDateTime regDt;
    private Long reviewSeq;
}
