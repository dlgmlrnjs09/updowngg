package gg.updown.backend.external.riot.api.ddragon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class DdragonApiService {

    @Value("${riot-api.latest-version}")
    private String latestVersion;

    private final WebClient ddragonWebClient;

    public List<String> getVersionList() {
        return ddragonWebClient.get()
                .uri("/api/versions.json")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }

    public Map<String, Object> getChampionData() {
        Map response = ddragonWebClient.get()
                .uri("/cdn/" + latestVersion + "/data/ko_KR/champion.json")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (Map<String, Object>) response.get("data");
    }
}
