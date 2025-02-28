package gg.updown.backend.external.riot.api.rso.service;

import gg.updown.backend.external.riot.api.rso.model.RiotTokenDto;
import gg.updown.backend.external.riot.api.rso.model.RiotUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class RiotRsoService {
    @Value("${spring.security.oauth2.client.provider.riot.token-uri}")
    private String tokenUrl;
    @Value("${spring.security.oauth2.client.registration.riot.redirect-uri}")
    private String redirectUrl;
    @Value("${spring.security.oauth2.client.registration.riot.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.riot.client-secret}")
    private String clientSecret;

    private final WebClient riotRsoWebClient;

    public RiotTokenDto exchangeCodeToToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", redirectUrl);

        return riotRsoWebClient.post()
                .uri(tokenUrl)
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(RiotTokenDto.class)
                .block();
    }

    public RiotUserInfoDto getUserInfo(String accessToken) {
        return riotRsoWebClient.get()
                .uri("https://auth.riotgames.com/userinfo")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(RiotUserInfoDto.class)
                .block();
    }

}
