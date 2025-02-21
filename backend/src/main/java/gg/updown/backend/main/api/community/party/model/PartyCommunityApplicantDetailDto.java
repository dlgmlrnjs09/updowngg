package gg.updown.backend.main.api.community.party.model;

import gg.updown.backend.main.api.community.duo.model.DuoSummonerInfoDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyCommunityApplicantDetailDto {
    private long postId;
    private long applicantSeq;
    private String puuid;
    private String position;
    private Boolean isApprove;
    DuoSummonerInfoDto summonerInfoDto;
}
