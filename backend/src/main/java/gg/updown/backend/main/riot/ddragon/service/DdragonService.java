package gg.updown.backend.main.riot.ddragon.service;

import gg.updown.backend.external.riot.api.ddragon.service.DdragonApiService;
import gg.updown.backend.main.riot.ddragon.mapper.DdragonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
}
