package gg.updown.backend.main.api.community.party.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartyParticipantInfoDto{
    private long postId;
    private String position;
    private String puuid;
    private String gameName;
    private String tagLine;
}
