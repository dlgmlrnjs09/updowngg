package gg.updown.backend.main.api.community.party.mapper;

import gg.updown.backend.main.api.community.party.model.PartyCommunityEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PartyCommunityMapper {
    void insertPartyPost(PartyCommunityEntity paramEntity);
}
