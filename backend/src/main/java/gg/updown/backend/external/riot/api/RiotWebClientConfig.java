package gg.updown.backend.external.riot.api;

import gg.updown.backend.external.riot.RiotApiBasePath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RiotWebClientConfig {

    @Value("${external.riot.api-key}")
    private String apiKey;

    @Bean
    public WebClient riotKrWebClient() {
        return createRiotWebClient("kr");
    }

    @Bean
    public WebClient riotAsiaWebClient() {
        return createRiotWebClient("asia");
    }

    private WebClient createRiotWebClient(String region) {
        return WebClient.builder()
                .baseUrl(RiotApiBasePath.API.getUrl(region))
                .defaultHeader("X-Riot-Token", apiKey)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(loggingRequestUrl())
                .build();
    }

    @Bean
    WebClient ddragonWebClient() {
        return WebClient.builder()
                .baseUrl(RiotApiBasePath.DDRAGON.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(loggingRequestUrl())
                .build();
    }

    @Bean
    WebClient ddragonRawWebClient() {
        return WebClient.builder()
                .baseUrl(RiotApiBasePath.DDRAGON_RAW.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(loggingRequestUrl())
                .build();
    }

    @Bean
    WebClient riotRsoWebClient() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .filter(loggingRequestUrl())
                .build();
    }

    @Bean
    public WebClient riotOAuthWebClient() {
        return WebClient.builder()
                .baseUrl("https://asia.api.riotgames.com")
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(loggingRequestUrl())
                .build();
    }

    private ExchangeFilterFunction loggingRequestUrl() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request URL: {}", clientRequest.url());
            return Mono.just(clientRequest);
        });
    }
}
