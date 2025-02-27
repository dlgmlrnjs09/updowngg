package gg.updown.backend.main.api.auth.service;

import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import gg.updown.backend.main.enums.TokenStatus;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.refresh.valid-milliseconds}")
    private long refreshValidMilliseconds;

    @Value("${jwt.access.valid-milliseconds}")
    private long accessValidMilliseconds;

    private final RedisTemplate<String, String> redisTemplate;
    private final UserDetailsService userDetailsService;

    /**
     * 초기화 메서드
     * secretKey를 Base64로 인코딩
     */
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * Access Token 생성
     * @param authentication 인증 정보
     * @return 생성된 access token
     */
    public String createAccessToken(Authentication authentication) {
        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
        Claims claims = Jwts.claims().setSubject(userDetails.getPuuid());
        claims.put("puuid", userDetails.getPuuid());
        // 사용자 권한 정보를 토큰에 추가
//        claims.put("roles", userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessValidMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성
     * @param authentication 인증 정보
     * @return 생성된 refresh token
     */
    public String createRefreshToken(Authentication authentication) {
        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshValidMilliseconds);

        String refreshToken = Jwts.builder()
                .setSubject(userDetails.getPuuid())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Redis에 refresh token 저장
        redisTemplate.opsForValue().set(
                "RT:" + userDetails.getPuuid(),
                refreshToken,
                refreshValidMilliseconds,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    /**
     * 토큰에서 인증 정보 추출
     * @param token JWT 토큰
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 토큰 유효성 검증
     * @param token JWT 토큰
     * @return 유효성 여부
     */
    public TokenStatus validateToken(String token) {
        try {
            // 블랙리스트 체크
            Boolean isBlacklisted = redisTemplate.hasKey("BL:" + token);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                return TokenStatus.BLACKLISTED;
            }

            // 토큰 검증 및 만료 체크
            try {
                Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token);
                return TokenStatus.VALID;
            } catch (ExpiredJwtException e) {
                return TokenStatus.EXPIRED;
            }
        } catch (JwtException | IllegalArgumentException e) {
            return TokenStatus.INVALID;
        }
    }

    /**
     * Refresh Token 유효성 검증
     * Redis에 저장된 토큰과 비교
     */
    public TokenStatus validateRefreshToken(String username, String refreshToken) {
        String storedRefreshToken = redisTemplate.opsForValue().get("RT:" + username);

        log.info("puuid: {}", username);
        log.info("Received refreshToken: {}", refreshToken);
        log.info("Stored refreshToken in Redis: {}", storedRefreshToken);

        // Redis에 저장된 토큰이 없는 경우
        if (storedRefreshToken == null) {
            return TokenStatus.INVALID;
        }

        // Redis에 저장된 토큰과 다른 경우
        if (!refreshToken.equals(storedRefreshToken)) {
            return TokenStatus.INVALID;
        }

        // 토큰 자체의 유효성 검증
        return validateToken(refreshToken);
    }

    /**
     * AccessToken 블랙리스트 추가
     */
    public void addToBlacklist(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 토큰의 남은 유효시간 계산
        Date expiration = claims.getExpiration();
        long remainingTime = expiration.getTime() - System.currentTimeMillis();

        // 블랙리스트에 추가 (토큰의 남은 유효기간만큼만 저장)
        redisTemplate.opsForValue().set(
                "BL:" + token,
                "blacklisted",
                remainingTime,
                TimeUnit.MILLISECONDS
        );
    }
}
