package gg.updown.backend.external.riot.api.lol.match.service;

import gg.updown.backend.external.riot.api.lol.match.model.GetMatchIdListReqDto;
import gg.updown.backend.external.riot.api.lol.match.model.MatchDto;
import gg.updown.backend.external.riot.exception.RiotApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchApiService {
    private final String basePath = "/lol/match/v5/matches";

    private final WebClient riotAsiaWebClient;

    /**
     * puuid로 경기 ID 리스트 가져오기
     * @param reqDto
     * @return
     */
    public List<String> getMatchIdListByPuuid(GetMatchIdListReqDto reqDto) {
        return riotAsiaWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(basePath + "/by-puuid/{puuid}/ids")
                        .queryParamIfPresent("startTime", Optional.ofNullable(reqDto.getStartTime()))
                        .queryParamIfPresent("endTime", Optional.ofNullable(reqDto.getEndTime()))
                        .queryParamIfPresent("queue", Optional.ofNullable(reqDto.getQueue()))
                        .queryParamIfPresent("type", Optional.ofNullable(reqDto.getType()))
                        .queryParamIfPresent("start", Optional.ofNullable(reqDto.getStart()))
                        .queryParamIfPresent("count", Optional.ofNullable(reqDto.getCount()))
                        .build(reqDto.getPuuid())
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    return Mono.error(new RiotApiException(response.statusCode(), RiotApiException.defaultMessage));
                })
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }

    /**
     * matchId로 경기 상세정보 가져오기
     * @param matchId
     * @return
     */
    public MatchDto getMatchDetailByMatchId(String matchId) {
        return riotAsiaWebClient.get()
                .uri(basePath + "/{matchId}", matchId).retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return Mono.error(new RiotApiException(response.statusCode(), RiotApiException.defaultMessage));
                })
                .bodyToMono(MatchDto.class)
                .block();
    }
}
