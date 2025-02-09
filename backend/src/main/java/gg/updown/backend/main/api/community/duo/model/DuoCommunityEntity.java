package gg.updown.backend.main.api.community.duo.model;

import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuoCommunityEntity extends CommunityPostEntity {
    private String gameMode;
    private String positionSelf;
    private String positionFind;
    private Boolean isUseMic;
}
