package gg.updown.backend.external.riot.api.lol.match.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gg.updown.backend.external.riot.api.lol.match.mapper.RiotMatchRawMapper;
import gg.updown.backend.external.riot.api.lol.match.model.MatchDto;
import gg.updown.backend.external.riot.api.lol.match.model.RiotMatchRawEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RiotMatchRawService {
    
    private final RiotMatchRawMapper riotMatchRawMapper;
    private final ObjectMapper objectMapper;
    
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
}