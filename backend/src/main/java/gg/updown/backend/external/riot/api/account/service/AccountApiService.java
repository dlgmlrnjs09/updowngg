package gg.updown.backend.external.riot.api.account.service;

import gg.updown.backend.external.riot.api.account.mapper.AccountApiMapper;
import gg.updown.backend.external.riot.api.account.model.AccountInfoEntity;
import gg.updown.backend.external.riot.api.account.model.AccountInfoResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountApiService {
    private final String basePath = "/riot/account/v1/accounts";

    private final WebClient riotAsiaWebClient;
    private final AccountApiMapper accountApiMapper;

    /**
     * 라이엇 계정 정보 가져오기 (https://developer.riotgames.com/apis#account-v1/GET_getByRiotId)
     * @param riotId
     * @param tag
     * @return puuid
     */
    public AccountInfoResDto getAccountInfoByRiotId(String riotId, String tag) {

        accountApiMapper.insertAccountInfo(AccountInfoEntity.builder()
            .puuid("1234556")
            .gameName("안녕하세요")
            .tagLine("KR2")
        .build());

        return riotAsiaWebClient.get()
                .uri(basePath + "/by-riot-id/{riotId}/{tag}", riotId, tag)
                .retrieve()
                .bodyToMono(AccountInfoResDto.class)
                .block();
    }
}
