package gg.updown.backend.main.riot.ddragon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gg.updown.backend.common.util.StringUtil;
import gg.updown.backend.external.riot.api.ddragon.model.ArenaDto;
import gg.updown.backend.external.riot.api.ddragon.model.Champion;
import gg.updown.backend.external.riot.api.ddragon.service.DdragonApiService;
import gg.updown.backend.main.riot.ddragon.mapper.DdragonMapper;
import gg.updown.backend.main.riot.ddragon.model.ArenaAugmentDataValueEntity;
import gg.updown.backend.main.riot.ddragon.model.ArenaAugmentEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DdragonService {
    private final DdragonMapper ddragonMapper;
    private final DdragonApiService ddragonApiService;
    private final ObjectMapper objectMapper;

    public void loadAndInsertVersionList() {
        List<String> versionList = ddragonApiService.getVersionList();
        Collections.reverse(versionList);
        for (String version : versionList) {
            ddragonMapper.insertVersion(version);
        }
    }

    public String getLatestVersion() {
        return ddragonMapper.getLatestVersion();
    }

    public void insertChampionNameList() {
        Map<String, Object> championMap = ddragonApiService.getChampionData();
        for (Map.Entry<String, Object> entry : championMap.entrySet()) {
            Map<String, Object> innerJson = (Map<String, Object>) entry.getValue();
            ddragonMapper.insertChampions(Champion.builder()
                    .nameUs(innerJson.get("id").toString())
                    .nameKr(innerJson.get("name").toString())
                    .key(innerJson.get("key").toString())
                    .build());
        }
    }

    /**
     * 증강 데이터 저장
     *
     * @param arenaDto 증강 DTO
     */
    @Transactional
    public void saveArenaAugmentData(ArenaDto arenaDto) {
        try {
            if (arenaDto == null || arenaDto.getAugments() == null) {
                log.error("ArenaDto or augments is null");
                return;
            }

            log.info("Saving {} augments", arenaDto.getAugments().size());

            for (Map<String, Object> augmentMap : arenaDto.getAugments()) {
                try {
                    // 증강 기본 정보 저장
                    String description = getStringValue(augmentMap, "desc");
                    ArenaAugmentEntity augmentEntity = ArenaAugmentEntity.builder()
                            .id(getIntegerValue(augmentMap, "id"))
                            .apiName(getStringValue(augmentMap, "apiName"))
                            .name(getStringValue(augmentMap, "name"))
                            .description(description)
                            .cleanDescription(StringUtil.removeHtmlTags(description))
                            .tooltip(getStringValue(augmentMap, "tooltip"))
                            .rarityId(getIntegerValue(augmentMap, "rarity"))
                            .iconLarge(getStringValue(augmentMap, "iconLarge"))
                            .iconSmall(getStringValue(augmentMap, "iconSmall"))
                            .build();

                    ddragonMapper.insertArenaAugment(augmentEntity);

                    // 기존 데이터 값 삭제
                    ddragonMapper.deleteAugmentDataValuesByAugmentId(augmentEntity.getId());

                    // 데이터 값 저장
                    if (augmentMap.containsKey("dataValues") && augmentMap.get("dataValues") instanceof Map) {
                        Map<String, Object> dataValues = (Map<String, Object>) augmentMap.get("dataValues");
                        List<ArenaAugmentDataValueEntity> dataValueEntities = new ArrayList<>();

                        for (Map.Entry<String, Object> entry : dataValues.entrySet()) {
                            if (entry.getValue() instanceof Number) {
                                ArenaAugmentDataValueEntity dataValueEntity = ArenaAugmentDataValueEntity.builder()
                                        .augmentId(augmentEntity.getId())
                                        .key(entry.getKey())
                                        .value(((Number) entry.getValue()).doubleValue())
                                        .build();

                                dataValueEntities.add(dataValueEntity);
                            }
                        }

                        if (!dataValueEntities.isEmpty()) {
                            ddragonMapper.insertAugmentDataValues(dataValueEntities);
                        }
                    }

                } catch (Exception e) {
                    log.error("Failed to save augment: {}", augmentMap.get("id"), e);
                }
            }

            log.info("Successfully saved augment data");
        } catch (Exception e) {
            log.error("Failed to save arena augment data", e);
            throw new RuntimeException("Failed to save arena augment data", e);
        }
    }

    /**
     * 모든 증강 조회
     *
     * @return 증강 목록
     */
    public List<ArenaAugmentEntity> getAllAugments() {
        return ddragonMapper.findAllAugments();
    }

    /**
     * 희귀도별 증강 조회
     *
     * @param rarityId 희귀도 ID
     * @return 증강 목록
     */
    public List<ArenaAugmentEntity> getAugmentsByRarity(Integer rarityId) {
        return ddragonMapper.findAugmentsByRarityId(rarityId);
    }

    /**
     * 증강 상세 정보 조회
     *
     * @param augmentId 증강 ID
     * @return 증강 정보와 데이터 값
     */
    public Map<String, Object> getAugmentDetails(Integer augmentId) {
        ArenaAugmentEntity augment = ddragonMapper.findAugmentById(augmentId);
        if (augment == null) {
            return null;
        }

        List<ArenaAugmentDataValueEntity> dataValues = ddragonMapper.findDataValuesByAugmentId(augmentId);

        Map<String, Object> result = objectMapper.convertValue(augment, new TypeReference<Map<String, Object>>() {});
        result.put("dataValues", dataValues);

        return result;
    }

    public List<ArenaAugmentEntity> findAugmentListById(List<Integer> paramList) {
        return ddragonMapper.findAugmentListById(paramList);
    }

    // 유틸리티 메서드
    private String getStringValue(Map<String, Object> map, String key) {
        return map.containsKey(key) && map.get(key) instanceof String ? (String) map.get(key) : null;
    }

    private Integer getIntegerValue(Map<String, Object> map, String key) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        }
        return null;
    }
}
