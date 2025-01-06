package gg.updown.backend.main.api.auth.service;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DiscordOAuthService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("DiscordOAuthService.loadUser 시작");
        log.debug("ClientRegistration: {}", userRequest.getClientRegistration());
        log.debug("AccessToken: {}", userRequest.getAccessToken().getTokenValue());

        try {
            OAuth2User user = super.loadUser(userRequest);
            log.debug("OAuth2User 로드 성공: {}", user.getAttributes());
            return user;
        } catch (Exception e) {
            log.error("OAuth2User 로드 실패", e);
            throw e;
        }
    }
}