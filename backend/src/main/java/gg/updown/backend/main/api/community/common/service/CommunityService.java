package gg.updown.backend.main.api.community.common.service;

import gg.updown.backend.main.api.community.common.mapper.CommunityMapper;
import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {
    private final CommunityMapper communityMapper;

    public long insertPost(String communityCode, CommunityPostEntity entity) {
        communityMapper.insertPost(communityCode, entity);
        return entity.getPostId();
    }
}
