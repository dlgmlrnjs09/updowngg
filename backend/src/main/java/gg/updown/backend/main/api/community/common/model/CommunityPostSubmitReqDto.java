package gg.updown.backend.main.api.community.common.model;

import gg.updown.backend.main.api.community.duo.model.DuoCommunityPostDto;
import gg.updown.backend.main.api.community.party.model.PartyCommunityReqDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostSubmitReqDto {
    private long postId;
    private long writerSiteCode;
    private String writerPuuid;
    private String communityCode;
    private String title;
    private String content;

    private DuoCommunityPostDto duoCommunityEntity;
    private PartyCommunityReqDto partyCommunityDto;
}
