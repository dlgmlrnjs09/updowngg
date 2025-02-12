package gg.updown.backend.main.api.community.duo.service;

import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import gg.updown.backend.main.api.community.common.service.CommunityService;
import gg.updown.backend.main.api.community.duo.mapper.DuoCommunityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DuoCommunityTransactionService {
    private final CommunityService communityService;
    private final DuoCommunityMapper duoCommunityMapper;

    @Transactional
    public void insertDuoCommunityPost(String communityCode, CommunityPostEntity entity) {
        communityService.insertPost(communityCode, entity);
        duoCommunityMapper.insertDuoPost(entity);
    }
}
