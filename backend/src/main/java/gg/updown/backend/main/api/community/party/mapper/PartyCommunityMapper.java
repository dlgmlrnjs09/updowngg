package gg.updown.backend.main.api.community.party.mapper;

import gg.updown.backend.main.api.community.duo.model.DuoCommunitySearchFilter;
import gg.updown.backend.main.api.community.party.model.PartyCommunityEntity;
import gg.updown.backend.main.api.community.party.model.PartyCommunityParticipantEntity;
import gg.updown.backend.main.api.community.party.model.PartyPostCardDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartyCommunityMapper {
    List<PartyPostCardDto> getPartyPostList(DuoCommunitySearchFilter searchParamMap);
    void insertPartyPost(PartyCommunityEntity paramEntity);
    void insertPartyParticipant(PartyCommunityParticipantEntity paramEntity);
}
