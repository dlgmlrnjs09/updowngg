package gg.updown.backend.main.api.community.common;

import gg.updown.backend.main.api.community.common.service.CommunityInterface;
import gg.updown.backend.main.api.community.common.service.CommunityService;
import gg.updown.backend.main.api.community.duo.service.DuoCommunityService;
import org.springframework.stereotype.Service;

@Service
public class CommunityServiceFactory {
    private final DuoCommunityService duoCommunityService;

    public CommunityServiceFactory(DuoCommunityService duoCommunityService) {
        this.duoCommunityService = duoCommunityService;
    }

    public CommunityInterface getCommunityService(String communityCode) {
        switch (communityCode) {
            case "duo":
                return duoCommunityService;
            default:
                throw new IllegalArgumentException("Unknown community code: " + communityCode);
        }
    }
}
