package gg.updown.backend.main.api.community.party.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartyCommunityApplicantDto {
    private long postId;
    private String puuid;
    private String position;
    private String applyStatus;
}
