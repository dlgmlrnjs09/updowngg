package gg.updown.backend.external.riot.api.lol.match.service;

import gg.updown.backend.external.riot.api.lol.match.model.GetMatchIdListReqDto;
import gg.updown.backend.external.riot.api.lol.match.model.MatchDto;
import gg.updown.backend.external.riot.exception.RiotApiException;
import gg.updown.backend.main.api.external.service.ApiCallHistoryService;
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
    private final ApiCallHistoryService apiCallHistoryService;
    private final RiotMatchRawService riotMatchRawService;

    /**
     * puuid로 경기 ID 리스트 가져오기
     * @param reqDto
     * @return
     */
    public List<String> getMatchIdListByPuuid(GetMatchIdListReqDto reqDto) {
        String url = basePath + "/by-puuid/" + reqDto.getPuuid() + "/ids";
        List<String> response = riotAsiaWebClient.get()
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
                .onStatus(HttpStatusCode::isError, responseError -> {
                    return Mono.error(new RiotApiException(responseError.statusCode(), RiotApiException.defaultMessage));
                })
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
                
        // Log API call
        apiCallHistoryService.logApiCall("match", url, reqDto, response);
        
        return response;
    }

    /**
     * matchId로 경기 상세정보 가져오기
     * @param matchId
     * @return
     */
    public MatchDto getMatchDetailByMatchId(String matchId) {
        // First check if we already have the data in our raw storage
        MatchDto cachedResponse = riotMatchRawService.getRawMatchData(matchId);
        if (cachedResponse != null) {
            log.info("Retrieved match data from local storage for matchId: {}", matchId);
            return cachedResponse;
        }
        
        // If not in local storage, fetch from Riot API
        String url = basePath + "/" + matchId;
        MatchDto response = riotAsiaWebClient.get()
                .uri(basePath + "/{matchId}", matchId).retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, responseError -> {
                    return Mono.error(new RiotApiException(responseError.statusCode(), RiotApiException.defaultMessage));
                })
                .bodyToMono(MatchDto.class)
                .block();
                
        // Log API call
        apiCallHistoryService.logApiCall("match", url, matchId, response);
        
        // Save raw data to database
        if (response != null) {
            // Extract region from matchId (e.g., KR_6123456789)
            String region = "KR";
            if (matchId.contains("_")) {
                region = matchId.substring(0, matchId.indexOf("_"));
            }
            
            riotMatchRawService.saveRawMatchData(matchId, response, region);
        }
        
        return response;
    }
}
