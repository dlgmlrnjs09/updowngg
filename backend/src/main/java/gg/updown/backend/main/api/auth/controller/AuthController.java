package gg.updown.backend.main.api.auth.controller;

import gg.updown.backend.main.api.auth.model.*;
import gg.updown.backend.main.api.auth.service.AuthService;
import gg.updown.backend.main.api.auth.service.JwtTokenProvider;
import gg.updown.backend.main.api.auth.service.UserDetailServiceImpl;
import gg.updown.backend.main.exception.SiteCommonException;
import gg.updown.backend.main.exception.SiteErrorMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 API 컨트롤러
 * 로그인 및 토큰 갱신 처리
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PutMapping("/signup")
    public ResponseEntity<SignupResDto> signup(@RequestBody SignupReqDto signupReqDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(authService.signup(signupReqDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 로그인 처리
     * @param loginDto 로그인 정보
     * @return JWT 토큰 쌍 (access token + refresh token)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReqDto loginDto) {
        try {
            JwtToken jwtToken = authService.authAndCreateJwtToken(loginDto);
            return ResponseEntity.ok(jwtToken);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Boolean> logout(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            jwtTokenProvider.deleteRefreshToken(userDetails.getUsername());
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
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody JwtToken tokenRefreshDto) {
        String refreshToken = tokenRefreshDto.getRefreshToken();

        try {
            JwtToken jwtToken = authService.refreshAccessToken(refreshToken);
            if (!jwtToken.getAccessToken().isEmpty()) {
                return ResponseEntity.ok(jwtToken);
            } else {
                throw new SiteCommonException(HttpStatus.UNAUTHORIZED, SiteErrorMessage.INVALID_TOKEN.getMessage());
            }
        } catch (AuthenticationException e) {
            throw new SiteCommonException(HttpStatus.UNAUTHORIZED, SiteErrorMessage.INVALID_TOKEN.getMessage());
        }
    }

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