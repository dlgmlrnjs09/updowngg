package gg.updown.backend.main.api.community.party.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyCommunityApproveReqDto {
    @NotNull(message = "postId는 필수값입니다.")
    private long postId;

    @Pattern(regexp = "^(TOP|JG|MID|AD|SUP)$", message = "올바른 포지션 코드가 아닙니다.")
    @NotNull(message = "position은 필수값입니다.")
    private String position;

    @NotNull(message = "applicantPuuid는 필수값입니다.")
    private String applicantPuuid;

    @NotNull(message = "applicantSeq는 필수값입니다.")
    private long applicantSeq;
}
