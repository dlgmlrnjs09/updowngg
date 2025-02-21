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
    private String puuid;
    private String position;
    private boolean isApprove;
    private LocalDateTime regDt;
    private LocalDateTime updDt;
}
