package gg.updown.backend.external.riot.api.lol.summoner.service;

import gg.updown.backend.external.riot.api.lol.summoner.model.SummonerDto;
import gg.updown.backend.external.riot.exception.RiotApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SummonerApiService {
    private final WebClient riotKrWebClient;
    private final String basePath = "/lol/summoner/v4/summoners";

    public SummonerDto getSummonerInfoByPuuid(String puuid) {
        return riotKrWebClient.get()
                .uri(basePath + "/by-puuid/{puuid}", puuid)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    return Mono.error(new RiotApiException(response.statusCode(), RiotApiException.defaultMessage));
                })
                .bodyToMono(SummonerDto.class)
                .block();
    }
}
