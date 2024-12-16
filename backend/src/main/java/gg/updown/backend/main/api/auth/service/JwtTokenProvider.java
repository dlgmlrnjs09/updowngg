package gg.updown.backend.main.api.auth.service;

import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰 유효 시간 설정
    private final long accessTokenValidityInMilliseconds = 30 * 60 * 1000L; // 30분
    private final long refreshTokenValidityInMilliseconds = 7 * 24 * 60 * 60 * 1000L; // 7일

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
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put("email", userDetails.getUsername());
        // 사용자 권한 정보를 토큰에 추가
//        claims.put("roles", userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

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
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        String refreshToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Redis에 refresh token 저장
        redisTemplate.opsForValue().set(
                "RT:" + userDetails.getUsername(),
                refreshToken,
                refreshTokenValidityInMilliseconds,
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
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Refresh Token 유효성 검증
     * Redis에 저장된 토큰과 비교
     */
    public boolean validateRefreshToken(String username, String refreshToken) {
        String storedRefreshToken = redisTemplate.opsForValue().get("RT:" + username);
        return refreshToken.equals(storedRefreshToken) && validateToken(refreshToken);
    }
}
