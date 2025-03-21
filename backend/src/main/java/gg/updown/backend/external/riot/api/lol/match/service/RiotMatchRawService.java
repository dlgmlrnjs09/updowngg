package gg.updown.backend.external.riot.api.lol.match.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gg.updown.backend.external.riot.api.ddragon.model.ArenaDto;
import gg.updown.backend.external.riot.api.lol.match.mapper.RiotMatchRawMapper;
import gg.updown.backend.external.riot.api.lol.match.model.MatchDto;
import gg.updown.backend.external.riot.api.lol.match.model.RiotMatchRawEntity;
import gg.updown.backend.external.riot.exception.RiotApiException;
import gg.updown.backend.main.riot.ddragon.service.DdragonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class RiotMatchRawService {
    
    private final RiotMatchRawMapper riotMatchRawMapper;
    private final ObjectMapper objectMapper;
    private final WebClient ddragonRawWebClient;
    private final DdragonService ddragonService;
    
    /**
     * Save raw match data as JSON
     * 
     * @param matchId Match ID
     * @param matchDto Match DTO to save
     * @param region Region code (default is "KR")
     */
    public void saveRawMatchData(String matchId, MatchDto matchDto, String region) {
        try {
            // Convert MatchDto to JSON string
            String matchDataJson = objectMapper.writeValueAsString(matchDto);
            
            // Create entity
            RiotMatchRawEntity entity = RiotMatchRawEntity.builder()
                    .matchId(matchId)
                    .matchData(matchDataJson)
                    .region(region != null ? region : "KR")
                    .build();
            
            // Save to database
            riotMatchRawMapper.insertRiotMatchRaw(entity);
            log.info("Saved raw match data for matchId: {}", matchId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize MatchDto for matchId: {}", matchId, e);
        } catch (Exception e) {
            log.error("Failed to save raw match data for matchId: {}", matchId, e);
        }
    }
    
    /**
     * Get raw match data by match ID
     * 
     * @param matchId Match ID
     * @return MatchDto if found, null otherwise
     */
    public MatchDto getRawMatchData(String matchId) {
        RiotMatchRawEntity entity = riotMatchRawMapper.findByMatchId(matchId);
        
        if (entity == null) {
            return null;
        }
        
        try {
            return objectMapper.readValue(entity.getMatchData(), MatchDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize match data for matchId: {}", matchId, e);
            return null;
        }
    }

    /**
     * 롤 아레나 증강 데이터 가져오기 및 저장
     * @param language 언어 코드 (기본값: ko_kr)
     * @return 성공 여부
     */
    public boolean syncArenaAugmentData(String language) {
        try {
            // CDragon API에서 데이터 가져오기
            String langCode = language != null ? language : "ko_kr";
            String url = "https://raw.communitydragon.org/latest/cdragon/arena/" + langCode + ".json";

            ArenaDto response = ddragonRawWebClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, responseError -> {
                        return Mono.error(new RiotApiException(responseError.statusCode(), "Failed to fetch arena data"));
                    })
                    .bodyToMono(ArenaDto.class)
                    .block();

            // 데이터 처리 및 저장
            if (response != null && response.getAugments() != null) {
                log.info("Retrieved {} augments from CDragon API", response.getAugments().size());
                ddragonService.saveArenaAugmentData(response);
                return true;
            } else {
                log.error("No augment data found in response");
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to sync arena augment data", e);
            return false;
        }
    }
}