package gg.updown.backend.external.riot.api.ddragon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DdragonApiService {

    private final String latestVersion = "14.24.1";

    private final WebClient ddragonWebClient;

    public List<String> getVersionList() {
        return ddragonWebClient.get()
                .uri("/api/versions.json")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }
}
