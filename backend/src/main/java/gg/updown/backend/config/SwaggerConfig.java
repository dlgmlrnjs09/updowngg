package gg.updown.backend.config;

import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("updownGG API Documentation")
                        /*.description("")*/
                        .version("v2.0")
                        .contact(new Contact()
                                .name("이희권")
                                .email("heekwon0867@naver.com")));
    }
}