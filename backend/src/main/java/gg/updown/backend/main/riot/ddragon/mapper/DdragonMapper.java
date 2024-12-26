package gg.updown.backend.main.riot.ddragon.mapper;

import gg.updown.backend.external.riot.api.ddragon.model.Champion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DdragonMapper {
    void insertVersion(String version);
    String getLatestVersion();
    void insertChampions(Champion champion);
    List<Champion> getAllChampions();
}
