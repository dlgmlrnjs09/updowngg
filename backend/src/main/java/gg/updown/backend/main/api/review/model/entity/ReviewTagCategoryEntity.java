package gg.updown.backend.main.api.review.model.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewTagCategoryEntity {
    private String categoryKey;
    private String categoryValue;
    private LocalDateTime regDt;
}
