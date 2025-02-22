package gg.updown.backend.main.api.community.party.model;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PartyCommunityAppliedHistoryDto extends PartyCommunityHistoryBaseDto {
    private PartyCommunityApplicantDto applicantDto;
}
