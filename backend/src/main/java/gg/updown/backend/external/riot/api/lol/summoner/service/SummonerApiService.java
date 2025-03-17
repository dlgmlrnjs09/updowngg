package gg.updown.backend.external.riot.api.lol.summoner.service;

import gg.updown.backend.external.riot.api.lol.summoner.model.SummonerDto;
import gg.updown.backend.external.riot.api.lol.league.model.LeagueInfoBySummonerDto;
import gg.updown.backend.external.riot.exception.RiotApiException;
import gg.updown.backend.main.api.external.service.ApiCallHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class SummonerApiService {
    private final WebClient riotKrWebClient;
    private final ApiCallHistoryService apiCallHistoryService;
    private final String basePath = "/lol/summoner/v4/summoners";

    public SummonerDto getSummonerInfoByPuuid(String puuid) {
        String url = basePath + "/by-puuid/" + puuid;
        SummonerDto response = riotKrWebClient.get()
                .uri(basePath + "/by-puuid/{puuid}", puuid)
                .retrieve()
                .onStatus(HttpStatusCode::isError, responseError -> {
                    return Mono.error(new RiotApiException(responseError.statusCode(), RiotApiException.defaultMessage));
                })
                .bodyToMono(SummonerDto.class)
                .block();
                
        // Log API call
        apiCallHistoryService.logApiCall("summoner", url, puuid, response);
        
        return response;
    }
}