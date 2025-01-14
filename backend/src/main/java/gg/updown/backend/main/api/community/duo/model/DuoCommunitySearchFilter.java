package gg.updown.backend.main.api.community.duo.model;

import gg.updown.backend.main.api.community.common.model.CommunitySearchFilter;
import gg.updown.backend.main.enums.SiteLeagueTier;
import gg.updown.backend.main.enums.SiteMatchGameMode;
import gg.updown.backend.main.enums.SiteMatchPosition;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuoCommunitySearchFilter extends CommunitySearchFilter {
    SiteMatchGameMode gameMode;
    SiteLeagueTier tier;
    SiteMatchPosition positionSelf;
    SiteMatchPosition positionFind;
}
