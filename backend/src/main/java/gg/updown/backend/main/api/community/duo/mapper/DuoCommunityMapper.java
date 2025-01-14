package gg.updown.backend.main.api.community.duo.mapper;

import gg.updown.backend.main.api.community.common.model.CommunityPostEntity;
import gg.updown.backend.main.api.community.duo.model.DuoCommunityEntity;
import gg.updown.backend.main.api.community.duo.model.DuoCommunityPostDto;
import gg.updown.backend.main.api.community.duo.model.DuoCommunitySearchFilter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DuoCommunityMapper {
    void insertDuoPost(CommunityPostEntity dto);
    List<DuoCommunityEntity> getDuoPostList(DuoCommunitySearchFilter searchParamMap);
}
