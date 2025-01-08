package gg.updown.backend.main.api.lol.summoner.mapper;

import gg.updown.backend.main.api.lol.summoner.model.dto.LolSummonerMostChampionDto;
import gg.updown.backend.main.api.lol.summoner.model.entity.LolSummonerEntity;
import gg.updown.backend.main.api.lol.summoner.model.entity.LolSummonerLeagueEntity;
import gg.updown.backend.main.api.ranking.model.SummonerBasicInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LolSummonerMapper {
    LolSummonerEntity getSummonerInfoByPuuid(String puuid);
    LolSummonerEntity getSummonerInfoBySiteCode(long siteCode);
    void insertSummonerInfo(LolSummonerEntity entity);
    void updateSummonerInfo(LolSummonerEntity entity);
    LolSummonerLeagueEntity getSummonerLeagueInfo(@Param("summonerId") String summonerId, @Param("queueType") String queueType);
    void insertSummonerLeagueInfo(LolSummonerLeagueEntity entity);
    void updateSummonerLeagueInfo(LolSummonerLeagueEntity entity);
    void conflictSummonerLeagueInfo(LolSummonerLeagueEntity entity);
    SummonerBasicInfoDto getSummonerBasicInfo(long siteCode);
    List<LolSummonerMostChampionDto> getSummonerMostChampions(@Param("puuid")String puuid, @Param("limit")int limit);
}
