package gg.updown.backend.main.api.community.party.model;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyCommunityHistoryReqDto {
    @Min(value = 1, message = "page는 0 이상이어야 합니다.")
    private Integer page = 0;
    @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
    private Integer limit = 10;
}
