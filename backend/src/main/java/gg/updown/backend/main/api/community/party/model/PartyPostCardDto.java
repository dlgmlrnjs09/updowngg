package gg.updown.backend.main.api.community.party.model;

import gg.updown.backend.main.api.community.common.model.CommunityPostDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartyPostCardDto extends CommunityPostDto {
    // 게시글 상세정보
    private String gameMode;
    private String positionSelf;
    private Boolean isUseMic;
    private Boolean isOpenTop;
    private Boolean isOpenJungle;
    private Boolean isOpenMid;
    private Boolean isOpenAd;
    private Boolean isOpenSup;
    private String topPuuid;
    private String junglePuuid;
    private String midPuuid;
    private String adPuuid;
    private String supPuuid;

    private List<PartyCommunityParticipantDto> participantDtoList;
}
