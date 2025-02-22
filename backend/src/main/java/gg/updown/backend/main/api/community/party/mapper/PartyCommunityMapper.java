package gg.updown.backend.main.api.community.party.mapper;

import gg.updown.backend.main.api.community.duo.model.DuoCommunitySearchFilter;
import gg.updown.backend.main.api.community.party.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PartyCommunityMapper {
    List<PartyPostCardDto> getPartyPostList(DuoCommunitySearchFilter searchParamMap);
    PartyCommunityParticipantEntity selectParticipantWithLock(long postId);
    List<PartyCommunityApplicantEntity> getApplicantList(@Param("puuid")String puuid, @Param("postIds")List<Long> postIds);
    PartyPostCardDto getPartyActivePost(String puuid);
    PartyCommunityApplicantEntity getApplicant(@Param("postId") long postId, @Param("applicantSeq") long applicantSeq);
    List<PartyCommunityApplicantEntity> getWaitingApplicantList(long postId);
    long getNextApplicantSeq(long postId);

    void insertPartyPost(PartyCommunityEntity paramEntity);
    void insertPartyParticipant(PartyCommunityParticipantEntity paramEntity);
    void insertPartyApplicant(PartyCommunityApplicantEntity paramEntity);

    boolean checkPartyReader(@Param("postId") long postId, @Param("puuid")String puuid);
    boolean checkAlreadyByPosition(@Param("postId") long postId, @Param("position")String position);
    boolean checkParticipateAnotherPosition(@Param("postId")long postId, @Param("puuid") String puuid);
    void updateApplicantStatus(@Param("postId") long postId, @Param("applicantSeq") long applicantSeq, @Param("isApproval")boolean isApproval);
    void updateParticipant(PartyCommunityParticipantEntity paramEntity);
    void deleteAnotherApplicant(PartyCommunityApplicantEntity paramEntity);
    void updatePartyStatus(@Param("postId")long postId, @Param("postStatus")String postStatus);
}
