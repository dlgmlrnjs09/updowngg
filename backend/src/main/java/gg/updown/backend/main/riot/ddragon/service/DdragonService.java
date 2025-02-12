package gg.updown.backend.main.riot.ddragon.service;

import gg.updown.backend.external.riot.api.ddragon.model.Champion;
import gg.updown.backend.external.riot.api.ddragon.service.DdragonApiService;
import gg.updown.backend.main.riot.ddragon.mapper.DdragonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DdragonService {
    private final DdragonMapper ddragonMapper;
    private final DdragonApiService ddragonApiService;

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
                    .build());
        }
    }
}
