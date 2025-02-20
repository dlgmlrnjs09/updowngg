package gg.updown.backend.main.api.community.party.service;

import gg.updown.backend.main.api.community.common.service.CommunityService;
import gg.updown.backend.main.api.community.party.mapper.PartyCommunityMapper;
import gg.updown.backend.main.api.community.party.model.PartyCommunityEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyCommunityTransactionService {
    private final CommunityService communityService;
    private final PartyCommunityMapper partyCommunityMapper;

    @Transactional
    public void insertPartyCommunityPost(String communityCode, PartyCommunityEntity entity) {
        communityService.insertPost(communityCode, entity);
        partyCommunityMapper.insertPartyPost(entity);
    }
}
