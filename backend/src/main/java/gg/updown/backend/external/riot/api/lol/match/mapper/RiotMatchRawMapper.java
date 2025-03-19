package gg.updown.backend.external.riot.api.lol.match.mapper;

import gg.updown.backend.external.riot.api.lol.match.model.RiotMatchRawEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RiotMatchRawMapper {
    void insertRiotMatchRaw(RiotMatchRawEntity entity);
    
    RiotMatchRawEntity findByMatchId(@Param("matchId") String matchId);
}