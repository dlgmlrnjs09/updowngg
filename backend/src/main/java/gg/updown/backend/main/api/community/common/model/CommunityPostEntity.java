package gg.updown.backend.main.api.community.common.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostEntity {
    private long postId;
    private long writerSiteCode;
    private String writerPuuid;
    private String communityCode;
    private String title;
    private String content;
    private LocalDateTime regDt;
    private LocalDateTime updDt;
    private LocalDateTime delDt;
}
