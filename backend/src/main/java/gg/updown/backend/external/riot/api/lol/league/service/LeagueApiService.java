package gg.updown.backend.external.riot.api.lol.league.service;

import gg.updown.backend.external.riot.api.lol.league.model.LeagueInfoBySummonerDto;
import gg.updown.backend.external.riot.exception.RiotApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeagueApiService {
    private final WebClient riotKrWebClient;
    private final String basePath = "/lol/league/v4/entries";

    public List<LeagueInfoBySummonerDto> getSummonerLeagueInfoBySummonerId(String summonerId) {
        return riotKrWebClient.get()
                .uri(basePath + "/by-summoner/{summonerId}", summonerId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    return Mono.error(new RiotApiException(response.statusCode(), RiotApiException.defaultMessage));
                })
                .bodyToMono(new ParameterizedTypeReference<List<LeagueInfoBySummonerDto>>() {})
                .block();
    }
}
