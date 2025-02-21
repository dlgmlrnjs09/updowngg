package gg.updown.backend.main.api.community.party.model;

import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPartyDto {
    PartyPostCardDto postCardDto;
    LinkedHashMap<String, List<PartyCommunityApplicantDetailDto>> applicantByPositionMap;
}
