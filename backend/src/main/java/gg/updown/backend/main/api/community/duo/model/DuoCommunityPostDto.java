package gg.updown.backend.main.api.community.duo.model;

import gg.updown.backend.main.api.community.common.model.CommunityPostDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuoCommunityPostDto/* extends CommunityPostDto */{
    private String gameMode;
    private String positionSelf;
    private String positionFind;
    private boolean isUseMic;
}
