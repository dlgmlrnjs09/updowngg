package gg.updown.backend.main.api.external.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gg.updown.backend.main.api.external.mapper.ApiCallHistoryMapper;
import gg.updown.backend.main.api.external.model.ApiCallHistoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiCallHistoryService {
    
    private final ApiCallHistoryMapper apiCallHistoryMapper;
    private final ObjectMapper objectMapper;
    
    @Async
    public void logApiCall(String type, String url, Object request, Object response) {
        try {
            String requestJson = request != null ? objectMapper.writeValueAsString(request) : null;
            String responseJson = response != null ? objectMapper.writeValueAsString(response) : null;
            
            ApiCallHistoryEntity entity = ApiCallHistoryEntity.builder()
                    .type(type)
                    .url(url)
                    .request(requestJson)
                    .response(responseJson)
                    .regDt(LocalDateTime.now())
                    .build();
            
            apiCallHistoryMapper.insertApiCallHistory(entity);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize API call data", e);
        } catch (Exception e) {
            log.error("Failed to log API call", e);
        }
    }
}