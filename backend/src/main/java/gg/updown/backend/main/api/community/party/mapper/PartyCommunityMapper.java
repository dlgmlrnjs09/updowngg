package gg.updown.backend.main.api.community.party.mapper;

import gg.updown.backend.main.api.community.duo.model.DuoCommunitySearchFilter;
import gg.updown.backend.main.api.community.party.model.PartyCommunityApplicantEntity;
import gg.updown.backend.main.api.community.party.model.PartyCommunityEntity;
import gg.updown.backend.main.api.community.party.model.PartyCommunityParticipantEntity;
import gg.updown.backend.main.api.community.party.model.PartyPostCardDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PartyCommunityMapper {
    List<PartyPostCardDto> getPartyPostList(DuoCommunitySearchFilter searchParamMap);
    PartyCommunityParticipantEntity selectParticipantWithLock(long postId);
    List<PartyCommunityApplicantEntity> getApplicantList(@Param("puuid")String puuid, @Param("postIds")List<Long> postIds);

    void insertPartyPost(PartyCommunityEntity paramEntity);
    void insertPartyParticipant(PartyCommunityParticipantEntity paramEntity);
    void insertPartyApplicant(PartyCommunityApplicantEntity paramEntity);
}
