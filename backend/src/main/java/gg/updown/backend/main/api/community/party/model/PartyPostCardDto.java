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
    private String postStatus;
    private String gameMode;
    private String positionSelf;
    private Boolean isUseMic;
    private Boolean isOpenTop;
    private Boolean isOpenJungle;
    private Boolean isOpenMid;
    private Boolean isOpenAd;
    private Boolean isOpenSup;
    private String tierMin;
    private String tierMax;
    private String topPuuid;
    private String junglePuuid;
    private String midPuuid;
    private String adPuuid;
    private String supPuuid;

    // 참가자 수
    private int participantCount;
    // 모집인원 수
    private int recruitCount;

    private List<PartyCommunityParticipantDto> participantDtoList;
}
