package gg.updown.backend.main.api.community.common.service;

import gg.updown.backend.main.api.community.common.model.CommunityPostDto;
import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import gg.updown.backend.main.api.community.common.model.CommunityPostSubmitReqDto;

import java.util.List;
import java.util.Map;

public interface CommunityInterface {
    <T extends CommunityPostDto> List<T> getPostList(String communityCode, Map<String, String> searchParamMap);
    void insertPost(String communityCode, CommunityPostSubmitReqDto post);
    void updatePost(String communityCode, CommunityPostEntity post);
}
