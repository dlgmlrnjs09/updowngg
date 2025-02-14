package gg.updown.backend.external.riot.api.lol.spectator.service;

import gg.updown.backend.external.riot.api.lol.spectator.model.CurrentMatchInfoDto;
import gg.updown.backend.external.riot.exception.RiotApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpectatorService {

    private final String basePath = "/lol/spectator/v5";
    private final WebClient riotAsiaWebClient;
    private final WebClient riotKrWebClient;

    /**
     * 진행중인 게임정보 가져오기
     * @param puuid
     * @return
     */
    public CurrentMatchInfoDto getCurrentMatchInfoByPuuid(String puuid) {
        try {
            return riotKrWebClient.get()
                    .uri(basePath + "/active-games/by-summoner/{puuid}", puuid)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() && status.value() != 404, response -> {
                        return Mono.error(new RiotApiException(response.statusCode(), RiotApiException.defaultMessage));
                    })
                    .bodyToMono(CurrentMatchInfoDto.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            // 진행중인 게임 없음
            return null;
        }
    }
}
