package gg.updown.backend.main.api.lol.match.mapper;

import gg.updown.backend.main.api.lol.match.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LolMatchMapper {
    String getLatestMatchId(String puuid);
    String getLatestRequestMatchId(String puuid);
    int countingMatchByMatchId(String matchId);
    LolMatchEntity getMatchInfo(String matchId);

    List<LolMatchParticipantEntity> getMatchParticipantList(String matchId);
    List<LolMatchInfoResDto> getMatchesByPuuid(@Param("puuid") String puuid, @Param("offset") int offset, @Param("limit") int limit);
    void insertMatch(LolMatchEntity match);
    void insertMatchParticipants(List<LolMatchParticipantEntity> participants);
    void insertMatchIdList(List<String> matchIdList);
    void insertMatchRequests(@Param("puuid")String puuid, @Param("matchIdList")List<String> matchIdList);
    void updateMatch(LolMatchEntity match);
    List<String> selectMatchRequestList(@Param("puuid") String puuid, @Param("offset") int offset, @Param("limit") int limit);
}
