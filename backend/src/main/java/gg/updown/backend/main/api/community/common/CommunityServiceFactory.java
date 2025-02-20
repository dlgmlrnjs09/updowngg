package gg.updown.backend.main.api.community.common;

import gg.updown.backend.main.api.community.common.service.CommunityInterface;
import gg.updown.backend.main.api.community.duo.service.DuoCommunityService;
import gg.updown.backend.main.api.community.party.service.PartyCommunityService;
import org.springframework.stereotype.Service;

@Service
public class CommunityServiceFactory {
    private final DuoCommunityService duoCommunityService;
    private final PartyCommunityService partyCommunityService;

    public CommunityServiceFactory(DuoCommunityService duoCommunityService, PartyCommunityService partyCommunityService) {
        this.duoCommunityService = duoCommunityService;
        this.partyCommunityService = partyCommunityService;
    }

    public CommunityInterface getCommunityService(String communityCode) {
        if ("duo".equals(communityCode)) {
            return duoCommunityService;
        } else if ("party".equals(communityCode)) {
            return partyCommunityService;
        }else {
            throw new IllegalArgumentException("Unknown community code: " + communityCode);
        }
    }
}
