package gg.updown.backend.external.riot.api.account.service;

import gg.updown.backend.external.riot.exception.RiotApiException;
import gg.updown.backend.main.riot.account.mapper.RiotAccountMapper;
import gg.updown.backend.external.riot.api.account.model.AccountInfoResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountApiService {
    private final String basePath = "/riot/account/v1/accounts";

    private final WebClient riotAsiaWebClient;

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
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return Mono.error(new RiotApiException(HttpStatus.BAD_REQUEST, "존재하지 않는 라이엇 계정입니다."));
                })
                .bodyToMono(AccountInfoResDto.class)
                .block();
    }
}
