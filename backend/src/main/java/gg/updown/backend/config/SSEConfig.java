package gg.updown.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Configuration
public class SSEConfig {
    @Bean
    public SseEmitter sseEmitter() {
        return new SseEmitter(Long.MAX_VALUE);
    }
}
