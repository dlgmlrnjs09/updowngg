package gg.updown.backend.main.api.lol.match.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LolMatchMapper {
    public String getLatestMatchId(String puuid);
}
