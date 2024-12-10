package gg.updown.backend.external.riot.api.account;

import gg.updown.backend.external.riot.api.account.dto.AccountInfoResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountApiService {
    private final WebClient riotAsiaWebClient;
    private final String basePath = "/riot/account/v1/accounts";

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
                .bodyToMono(AccountInfoResDto.class)
                .block();
    }
}
