package gg.updown.backend.main.api.community.party.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyCommunityLeaveReqDto {
    @NotNull(message = "postId는 필수값입니다.")
    private long postId;
}
