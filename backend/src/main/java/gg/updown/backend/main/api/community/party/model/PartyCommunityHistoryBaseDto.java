package gg.updown.backend.main.api.community.party.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartyCommunityHistoryBaseDto {
    private long postId;
    private String gameMode;
    private String content;
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
    private String postStatus;
    private LocalDateTime regDt;

    // 참가자 수
    private int participantCount;
    // 모집인원 수
    private int recruitCount;
    // 참가자 목록
    private List<PartyCommunityParticipantDto> participantList;
}
