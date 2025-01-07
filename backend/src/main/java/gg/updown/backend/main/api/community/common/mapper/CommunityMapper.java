package gg.updown.backend.main.api.community.common.mapper;

import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommunityMapper {
    void insertPost(@Param("communityCode")String communityCode, @Param("postEntity")CommunityPostEntity postEntity);
}
