package gg.updown.backend.main.api.lol.match.mapper;

import gg.updown.backend.main.api.lol.match.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LolMatchMapper {
    String getLatestMatchId(String puuid);
    int countingMatchByMatchId(String matchId);
    LolMatchEntity getMatchInfo(String matchId);

    List<LolMatchParticipantEntity> getMatchParticipantList(String matchId);
    List<LolMatchInfoResDto> getMatchesByPuuid(@Param("puuid") String puuid, @Param("offset") int offset, @Param("limit") int limit);
    void insertMatch(LolMatchEntity match);
    void insertMatchParticipants(List<LolMatchParticipantEntity> participants);
}
