package gg.updown.backend.main.api.lol.summoner.mapper;

import gg.updown.backend.main.api.lol.summoner.model.entity.LolSummonerEntity;
import gg.updown.backend.main.api.lol.summoner.model.entity.LolSummonerLeagueEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LolSummonerMapper {
    LolSummonerEntity getSummonerInfoByPuuid(String puuid);
    void insertSummonerInfo(LolSummonerEntity entity);
    void updateSummonerInfo(LolSummonerEntity entity);
    LolSummonerLeagueEntity getSummonerLeagueInfo(@Param("String") String summonerId, @Param("queueType") String queueType);
    void insertSummonerLeagueInfo(LolSummonerLeagueEntity entity);
    void updateSummonerLeagueInfo(LolSummonerLeagueEntity entity);
    void conflictSummonerLeagueInfo(LolSummonerLeagueEntity entity);
}
