package gg.updown.backend.main.riot.ddragon.mapper;

import gg.updown.backend.external.riot.api.ddragon.model.Champion;
import gg.updown.backend.main.riot.ddragon.model.ArenaAugmentDataValueEntity;
import gg.updown.backend.main.riot.ddragon.model.ArenaAugmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DdragonMapper {
    void insertVersion(String version);
    String getLatestVersion();
    void insertChampions(Champion champion);
    List<Champion> getAllChampions();

    void insertArenaAugment(ArenaAugmentEntity entity);

    ArenaAugmentEntity findAugmentById(@Param("id") Integer id);

    List<ArenaAugmentEntity> findAugmentListById(List<Integer> paramList);

    List<ArenaAugmentEntity> findAllAugments();

    List<ArenaAugmentEntity> findAugmentsByRarityId(@Param("rarityId") Integer rarityId);

    // Augment Data Values CRUD
    void insertAugmentDataValue(ArenaAugmentDataValueEntity entity);

    void insertAugmentDataValues(List<ArenaAugmentDataValueEntity> entities);

    void deleteAugmentDataValuesByAugmentId(@Param("augmentId") Integer augmentId);

    List<ArenaAugmentDataValueEntity> findDataValuesByAugmentId(@Param("augmentId") Integer augmentId);
}
