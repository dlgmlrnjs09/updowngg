package gg.updown.backend.main.api.community.common.service;

import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import gg.updown.backend.main.api.community.common.model.CommunityPostSubmitReqDto;

import java.util.List;

public interface CommunityInterface {
    List<CommunityPostEntity> getPostList(String communityCode);
    void insertPost(String communityCode, CommunityPostSubmitReqDto post);
    void updatePost(String communityCode, CommunityPostEntity post);
}
