package gg.updown.backend.main.api.community.party.model;

import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartyCommunityEntity extends CommunityPostEntity {
    private long postId;
    private String gameMode;
    private Boolean isOpenTop;
    private Boolean isOpenJungle;
    private Boolean isOpenMid;
    private Boolean isOpenAd;
    private Boolean isOpenSup;
    private Boolean isUseMic;
    private String postStatus;
    private LocalDateTime regDt;
    private LocalDateTime updDt;
    private LocalDateTime delDt;
}
