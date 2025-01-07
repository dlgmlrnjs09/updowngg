package gg.updown.backend.main.api.community.duo.service;

import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import gg.updown.backend.main.api.community.common.model.CommunityPostSubmitReqDto;
import gg.updown.backend.main.api.community.common.service.CommunityInterface;
import gg.updown.backend.main.api.community.duo.mapper.DuoCommunityMapper;
import gg.updown.backend.main.api.community.duo.model.DuoCommunityEntity;
import gg.updown.backend.main.api.community.duo.model.DuoCommunityPostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DuoCommunityService implements CommunityInterface {
    private final DuoCommunityMapper duoCommunityMapper;
    private final DuoCommunityTransactionService transactionService;

    @Override
    public List<CommunityPostEntity> getPostList(String communityCode) {
        return List.of();
    }

    @Override
    public void insertPost(String communityCode, CommunityPostSubmitReqDto post) {
        CommunityPostEntity wrapperEntity = new CommunityPostEntity();
        DuoCommunityEntity innerEntity = new DuoCommunityEntity();
        BeanUtils.copyProperties(post, wrapperEntity);
        BeanUtils.copyProperties(post.getDuoCommunityEntity(), innerEntity);
        wrapperEntity.setDuoCommunityEntity(innerEntity);
        transactionService.insertDuoCommunityPost(communityCode, wrapperEntity);
    }

    @Override
    public void updatePost(String communityCode, CommunityPostEntity post) {

    }
}
