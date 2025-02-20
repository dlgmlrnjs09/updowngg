package gg.updown.backend.main.api.community.party.model;

import gg.updown.backend.main.api.community.duo.model.DuoSummonerInfoDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyCommunityParticipantDto {
    Boolean isOpenPosition;
    String position;

    DuoSummonerInfoDto summonerInfoDto;
}
