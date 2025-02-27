package gg.updown.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:4173",
                        "http://13.124.117.180",
                        "http://13.124.117.180:4173",
                        "http://www.updowngg.lol",
                        "https://www.updowngg.lol",
                        "https://13.124.117.180",
                        "https://13.124.117.180:4173",
                        "updowngg.lol",
                        "www.updowngg.lol",
                        "https://updowngg.lol",
                        "dev.updowngg.lol:4173",
                        "http://dev.updowngg.lol:4173",
                        "https://dev.updowngg.lol:4173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}