package gg.updown.backend.main.api.auth.controller;

import gg.updown.backend.main.api.auth.model.*;
import gg.updown.backend.main.api.auth.service.AuthService;
import gg.updown.backend.main.api.auth.service.JwtTokenProvider;
import gg.updown.backend.main.api.auth.service.UserDetailServiceImpl;
import gg.updown.backend.main.exception.SiteCommonException;
import gg.updown.backend.main.exception.SiteErrorMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 API 컨트롤러
 * 로그인 및 토큰 갱신 처리
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "사이트 인증관련 API")
public class AuthController {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입", description = "일반 회원가입")
    @PutMapping("/signup")
    public ResponseEntity<SignupResDto> signup(@RequestBody SignupReqDto signupReqDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.signup(signupReqDto));
    }

    /**
     * 로그인 처리
     * @param loginDto 로그인 정보
     * @return JWT 토큰 쌍 (access token + refresh token)
     */
    @Operation(summary = "로그인", description = "일반 로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReqDto loginDto) {
        try {
            JwtToken jwtToken = authService.authAndCreateJwtToken(loginDto);
            return ResponseEntity.ok(jwtToken);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/discord/connect")
    public void connectDiscord(HttpSession session, Authentication auth) {
        session.setAttribute("CONNECTING_USER_ID", ((UserDetailImpl) auth.getPrincipal()).getSiteCode());
    }

    @PostMapping("/discord/disconnect")
    public void connectDiscord(@AuthenticationPrincipal UserDetails userDetails) {
        authService.disconnectDiscordAccount(((UserDetailImpl) userDetails).getSiteCode());
    }

    @GetMapping("/discord/account")
    public ResponseEntity<DiscordAccountEntity> getConnectStatus(@AuthenticationPrincipal UserDetails userDetails) {
        DiscordAccountEntity entity = authService.getDiscordAccountInfoBySiteCode(((UserDetailImpl) userDetails).getSiteCode());
        return ResponseEntity.ok(entity);
    }

    @Operation(summary = "소셜계정 연동", description = "디스코드 연동")
    @GetMapping("/discord/callback")
    public void discordCallback(HttpSession session, HttpServletResponse response) {
        long siteCode = Long.parseLong(session.getAttribute("CONNECTING_USER_ID").toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> attributes = ((DefaultOAuth2User)authentication.getPrincipal()).getAttributes();
        authService.connectDiscordAccount(siteCode, attributes);
        response.setHeader("Location", frontendUrl + "/setting/account");
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
    }

    @Operation(summary = "로그아웃", description = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Boolean> logout(@AuthenticationPrincipal UserDetails userDetails, @RequestHeader("Authorization") String bearerToken) {
        try {
            jwtTokenProvider.deleteRefreshToken(userDetails.getUsername());
            String token = bearerToken.substring(7); // "Bearer " 제거
            jwtTokenProvider.addToBlacklist(token);
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Access Token 갱신
     * @param tokenRefreshDto refresh token 정보
     * @return 새로운 access token
     */
    @Operation(summary = "액세스토큰 갱신", description = "액세스토큰 갱신")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody JwtToken tokenRefreshDto) {
        String refreshToken = tokenRefreshDto.getRefreshToken();

        try {
            JwtToken jwtToken = authService.refreshAccessToken(refreshToken);
            if (jwtToken != null && !jwtToken.getAccessToken().isEmpty()) {
                return ResponseEntity.ok(jwtToken);
            } else {
                throw new SiteCommonException(HttpStatus.UNAUTHORIZED, SiteErrorMessage.INVALID_TOKEN.getMessage());
            }
        } catch (AuthenticationException e) {
            throw new SiteCommonException(HttpStatus.UNAUTHORIZED, SiteErrorMessage.INVALID_TOKEN.getMessage());
        }
    }

    @Operation(summary = "회원정보 조회", description = "사이트 회원정보 조회")
    @GetMapping("/member-info")
    public ResponseEntity<SiteAccountResEntity> getMemberInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails.getUsername() == null) {
            throw new SiteCommonException(HttpStatus.UNAUTHORIZED, SiteErrorMessage.NOT_FOUND_SITE_ACCOUNT.getMessage());
        } else {
            UserDetailImpl memberEntity = (UserDetailImpl) userDetails;
            SiteAccountResEntity resEntity = SiteAccountResEntity.builder()
                    .memberEmail(userDetails.getUsername())
                    .memberSiteCode(memberEntity.getSiteCode())
                    .puuid(memberEntity.getPuuid())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(resEntity);
        }
    }
}