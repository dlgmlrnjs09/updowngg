package gg.updown.backend.external.riot.api.ddragon.service;

import gg.updown.backend.main.exception.SiteCommonException;
import gg.updown.backend.main.exception.SiteErrorDevMessage;
import gg.updown.backend.main.exception.SiteErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

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
        Map<String, Object> response = ddragonWebClient.get()
                .uri("/cdn/" + latestVersion + "/data/ko_KR/champion.json")
                .retrieve()
                .bodyToMono(Map.class)
                .map(map -> (Map<String, Object>) map)
                .block();

        if (response == null) {
            throw new SiteCommonException(HttpStatus.INTERNAL_SERVER_ERROR, SiteErrorMessage.INTERNAL_RIOT_SERVER_ERROR.getMessage());
        }

        Map<String, Object> data = (Map<String, Object>) response.get("data");
        if (data == null) {
            throw new SiteCommonException(HttpStatus.INTERNAL_SERVER_ERROR, SiteErrorMessage.INTERNAL_RIOT_SERVER_ERROR.getMessage());
        }

        return data;
    }
}
