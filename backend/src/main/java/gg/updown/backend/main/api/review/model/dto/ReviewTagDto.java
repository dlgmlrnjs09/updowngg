package gg.updown.backend.main.api.review.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewTagDto {
    private String tagCode;
    private String tagValue;
    private String tagDescription;
    private boolean tagUpdown;

    // 받은 태그 수
    private int frequentCount;
}
