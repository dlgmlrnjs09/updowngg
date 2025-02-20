package gg.updown.backend.main.api.community.party.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyCommunityReqDto {
    private String gameMode;
    private String positionSelf;
    private Boolean isUseMic;
    private Boolean isOpenTop;
    private Boolean isOpenJungle;
    private Boolean isOpenMid;
    private Boolean isOpenAd;
    private Boolean isOpenSup;
}
