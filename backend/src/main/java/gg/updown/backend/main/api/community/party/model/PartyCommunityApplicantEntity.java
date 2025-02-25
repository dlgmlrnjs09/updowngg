package gg.updown.backend.main.api.community.party.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartyCommunityApplicantEntity {
    private long postId;
    private long applicantSeq;
    private String applicantPuuid;
    private String position;
    private String applyStatus;
    private LocalDateTime regDt;
    private LocalDateTime updDt;
    private LocalDateTime delDt;
}
