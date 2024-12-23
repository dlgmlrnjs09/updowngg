package gg.updown.backend.main.api.review.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewTagEntity {
    private String tagCode;
    private String tagValue;
    private String tagDescription;
}
