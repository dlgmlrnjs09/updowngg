package gg.updown.backend.main.api.community.common.model;

import gg.updown.backend.main.api.community.duo.model.DuoCommunityPostDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostDto {
    private long postId;
    private long writerSiteCode;
    private String communityCode;
    private String title;
    private String content;
}
