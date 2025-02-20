package gg.updown.backend.main.api.community.party.service;

import gg.updown.backend.main.api.community.common.model.CommunityPostDto;
import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import gg.updown.backend.main.api.community.common.model.CommunityPostSubmitReqDto;
import gg.updown.backend.main.api.community.common.service.CommunityInterface;
import gg.updown.backend.main.api.community.party.model.PartyCommunityEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyCommunityService implements CommunityInterface {
    private final PartyCommunityTransactionService transactionService;

    @Override
    public <T extends CommunityPostDto> List<T> getPostList(String communityCode, Map<String, String> searchParamMap) {
        return null;
    }

    @Override
    public void insertPost(String communityCode, CommunityPostSubmitReqDto post) {
        PartyCommunityEntity sqlParamEntity = new PartyCommunityEntity();
        BeanUtils.copyProperties(post, sqlParamEntity);
        BeanUtils.copyProperties(post.getPartyCommunityDto(), sqlParamEntity);
        transactionService.insertPartyCommunityPost(communityCode, sqlParamEntity);
    }

    @Override
    public void updatePost(String communityCode, CommunityPostEntity post) {

    }
}
