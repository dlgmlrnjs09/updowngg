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
    List<PartyCommunityApplicantEntity> getApplicantList(@Param("puuid")String puuid, @Param("postIds")List<Long> postIds, @Param("applyStatus")String applyStatus);
    List<PartyCommunityApplicantEntity> getApplicantListByPuuid(@Param("puuid") String puuid, @Param("applyStatus")String applyStatus);
    PartyPostCardDto getPartyActivePost(String puuid);
    PartyPostCardDto getMyParticipantPost(String puuid);
    boolean checkMyOpenParticipantPost(String puuid);
    PartyCommunityApplicantEntity getApplicant(@Param("postId") long postId, @Param("applicantSeq") long applicantSeq);
    List<PartyCommunityApplicantEntity> getWaitingApplicantList(long postId);
    long getNextApplicantSeq(long postId);
    List<PartyCommunityHistoryDto> getPartyHostedHistory(@Param("puuid") String puuid, @Param("page") int page, @Param("limit") int limit);
    int getPartyHostedHistoryCount(String puuid);
    List<PartyCommunityHistoryDto> getPartyParticipatedHistory(@Param("puuid") String puuid, @Param("page") int page, @Param("limit") int limit);
    int getPartyParticipatedHistoryCount(String puuid);
    List<PartyCommunityAppliedHistoryDto> getPartyAppliedHistory(@Param("puuid") String puuid, @Param("page") int page, @Param("limit") int limit);
    int getPartyAppliedHistoryCount(String puuid);

    void insertPartyPost(PartyCommunityEntity paramEntity);
    void insertPartyParticipant(PartyCommunityParticipantEntity paramEntity);
    void insertPartyApplicant(PartyCommunityApplicantEntity paramEntity);

    boolean checkPartyReader(@Param("postId") long postId, @Param("puuid")String puuid);
    boolean checkAlreadyByPosition(@Param("postId") long postId, @Param("position")String position);
    boolean checkParticipateAnotherPosition(@Param("postId")long postId, @Param("puuid") String puuid);
    boolean checkAlreadyMyParty(String puuid);

    void updateApplicantStatus(@Param("postId") long postId, @Param("applicantSeq") long applicantSeq, @Param("applyStatus")String applyStatus);
    void updateParticipant(PartyCommunityParticipantEntity paramEntity);
    void deleteAnotherApplicant(PartyCommunityApplicantEntity paramEntity);
    void updatePartyStatus(@Param("postId")long postId, @Param("postStatus")String postStatus);
    void leaveParty(@Param("postId")long postId, @Param("puuid")String puuid);
}
