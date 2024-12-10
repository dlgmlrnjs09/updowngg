package gg.updown.backend.external.riot.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class SummonerApiService {
    private final WebClient riotKrWebClient;
    private final String basePath = "/lol/summoner/v4/summoners";
}
