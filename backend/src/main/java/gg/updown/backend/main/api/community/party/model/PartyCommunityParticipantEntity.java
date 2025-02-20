package gg.updown.backend.main.api.community.party.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartyCommunityParticipantEntity extends PartyCommunityEntity{
    private long postId;
    private String topPuuid;
    private String junglePuuid;
    private String midPuuid;
    private String adPuuid;
    private String supPuuid;
    private LocalDateTime updDt;
}
