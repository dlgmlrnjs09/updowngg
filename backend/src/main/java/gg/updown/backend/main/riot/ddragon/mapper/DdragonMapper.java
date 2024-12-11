package gg.updown.backend.main.riot.ddragon.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DdragonMapper {
    void insertVersion(String version);
    String getLatestVersion();
}
