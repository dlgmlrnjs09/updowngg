package gg.updown.backend.main.api.community.duo.model;

import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import gg.updown.backend.main.api.community.common.model.CommunityPostSubmitReqDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuoCommunityPostDto extends CommunityPostSubmitReqDto {
    private String gameMode;
    private String positionSelf;
    private String positionFind;
    private boolean isUseMic;
}
