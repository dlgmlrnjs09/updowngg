package gg.updown.backend.main.api.community.duo.mapper;

import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import gg.updown.backend.main.api.community.duo.model.DuoCommunityEntity;
import gg.updown.backend.main.api.community.duo.model.DuoCommunityPostDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DuoCommunityMapper {
    void insertDuoPost(CommunityPostEntity dto);
    List<DuoCommunityEntity> getDuoPostList();
}
