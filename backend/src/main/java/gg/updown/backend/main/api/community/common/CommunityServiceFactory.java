package gg.updown.backend.main.api.community.common;

import gg.updown.backend.main.api.community.common.service.CommunityInterface;
import gg.updown.backend.main.api.community.duo.service.DuoCommunityService;
import org.springframework.stereotype.Service;

@Service
public class CommunityServiceFactory {
    private final DuoCommunityService duoCommunityService;

    public CommunityServiceFactory(DuoCommunityService duoCommunityService) {
        this.duoCommunityService = duoCommunityService;
    }

    public CommunityInterface getCommunityService(String communityCode) {
        if ("duo".equals(communityCode)) {
            return duoCommunityService;
        } else {
            throw new IllegalArgumentException("Unknown community code: " + communityCode);
        }
    }
}
