package gg.updown.backend.main.api.community.common.model;

import gg.updown.backend.main.api.community.duo.model.DuoCommunityEntity;
import gg.updown.backend.main.api.community.duo.model.DuoCommunityPostDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostSubmitReqDto {
    private long postId;
    private long writerSiteCode;
    private String communityCode;
    private String title;
    private String content;

    private DuoCommunityPostDto duoCommunityEntity;
}
