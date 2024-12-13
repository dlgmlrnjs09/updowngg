package gg.updown.backend.main.api.lol.match.mapper;

import gg.updown.backend.main.api.lol.match.model.LolMatchEntity;
import gg.updown.backend.main.api.lol.match.model.LolMatchInfoDto;
import gg.updown.backend.main.api.lol.match.model.LolMatchParticipantDto;
import gg.updown.backend.main.api.lol.match.model.LolMatchParticipantEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LolMatchMapper {
    String getLatestMatchId(String puuid);
    int countingMatchByMatchId(String matchId);
    LolMatchEntity getMatchInfo(String matchId);
    List<LolMatchParticipantEntity> getMatchParticipantList(String matchId);
    void insertMatch(LolMatchEntity match);
    void insertMatchParticipants(List<LolMatchParticipantEntity> participants);
}
