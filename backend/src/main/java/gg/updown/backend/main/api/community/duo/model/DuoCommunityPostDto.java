package gg.updown.backend.main.api.community.duo.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuoCommunityPostDto/* extends CommunityPostDto */{
    private String gameMode;
    private String positionSelf;
    private String positionFind;
    private Boolean isUseMic;
}
