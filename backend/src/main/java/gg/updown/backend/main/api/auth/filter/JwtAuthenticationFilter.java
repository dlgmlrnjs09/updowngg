package gg.updown.backend.main.api.auth.filter;

import gg.updown.backend.main.api.auth.service.JwtTokenProvider;
import gg.updown.backend.main.enums.TokenStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 * 요청의 JWT 토큰을 검증하고 인증 정보를 설정
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);
        SecurityContextHolder.clearContext();

        if (token != null) {
            TokenStatus status = jwtTokenProvider.validateToken(token);

            switch (status) {
                case VALID:
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    break;

                case EXPIRED:
                    // 토큰 만료 정보를 응답에 포함
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.setHeader("TOKEN_STATUS", TokenStatus.EXPIRED.name());
                    return;

                case BLACKLISTED:
                case INVALID:
                    // 일반적인 토큰 에러는 그냥 401로 처리
                    break;
            }
        } else {
            // 토큰이 없는 경우
            /*response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            return;*/
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 토큰 추출
     * Bearer 스키마 사용
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
