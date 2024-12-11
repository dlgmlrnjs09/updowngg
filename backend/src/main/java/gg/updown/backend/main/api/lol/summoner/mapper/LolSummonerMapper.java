package gg.updown.backend.main.api.lol.summoner.mapper;

import gg.updown.backend.main.api.lol.summoner.model.LolSummonerEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LolSummonerMapper {
    LolSummonerEntity getSummonerInfoByPuuid(String puuid);
    void insertSummonerInfo(LolSummonerEntity entity);
    void updateSummonerInfo(LolSummonerEntity entity);
}
