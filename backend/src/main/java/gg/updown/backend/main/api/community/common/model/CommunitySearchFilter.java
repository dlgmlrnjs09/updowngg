package gg.updown.backend.main.api.community.common.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommunitySearchFilter {
    private int limit;
    private int offset;
}
