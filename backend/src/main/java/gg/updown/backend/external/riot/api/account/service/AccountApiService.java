package gg.updown.backend.external.riot.api.account.service;

import gg.updown.backend.external.riot.api.account.model.AccountInfoResDto;
import gg.updown.backend.external.riot.exception.RiotApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountApiService {
    @Value("${riot-api.account.base-path}")
    private String basePath;

    private final WebClient riotAsiaWebClient;
    private final WebClient riotOAuthWebClient;

    /**
     * 라이엇 계정 정보 가져오기 (https://developer.riotgames.com/apis#account-v1/GET_getByRiotId)
     * @param riotId
     * @param tag
     * @return puuid
     */
    public AccountInfoResDto getAccountInfoByRiotId(String riotId, String tag) {
        return riotAsiaWebClient.get()
                .uri(basePath + "/by-riot-id/{riotId}/{tag}", riotId, tag)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new RiotApiException(response.statusCode(), "존재하지 않는 라이엇 계정입니다."));
                    } else {
                        return Mono.error(new RiotApiException(response.statusCode(), RiotApiException.defaultMessage));
                    }
                })
                .bodyToMono(AccountInfoResDto.class)
                .block();
    }

    public AccountInfoResDto getAccountInfoByPuuid(String puuid) {
        return riotAsiaWebClient.get()
                .uri(basePath + "/by-puuid/{puuid}", puuid)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new RiotApiException(response.statusCode(), "존재하지 않는 라이엇 계정입니다."));
                    } else {
                        return Mono.error(new RiotApiException(response.statusCode(), RiotApiException.defaultMessage));
                    }
                })
                .bodyToMono(AccountInfoResDto.class)
                .block();
    }

    public AccountInfoResDto getAccountInfoByToken(String token) {
        return riotOAuthWebClient.get()
                .uri("/riot/account/v1/accounts/me")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new RiotApiException(response.statusCode(), "존재하지 않는 라이엇 계정입니다."));
                    } else {
                        return Mono.error(new RiotApiException(response.statusCode(), RiotApiException.defaultMessage));
                    }
                })
                .bodyToMono(AccountInfoResDto.class)
                .block();
    }
}
