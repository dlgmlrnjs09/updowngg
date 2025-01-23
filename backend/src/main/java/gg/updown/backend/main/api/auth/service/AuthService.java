package gg.updown.backend.main.api.auth.service;

import gg.updown.backend.external.riot.api.account.model.AccountInfoResDto;
import gg.updown.backend.external.riot.api.account.service.AccountApiService;
import gg.updown.backend.main.api.auth.mapper.AuthMapper;
import gg.updown.backend.main.api.auth.model.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${jwt.secret}")
    private String secretKey;

    private final UserDetailServiceImpl userDetailService;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AccountApiService accountApiService;

    public SignupResDto signup(SignupReqDto signupReqDto) {
        SignupResDto resDto = new SignupResDto();

        if (authMapper.isExistDuplicateEmail(signupReqDto.getEmail())) {
            resDto.setSuccess(false);
            resDto.setMessage("이미 존재하는 이메일입니다.");
        } else {
            resDto.setSuccess(true);
            resDto.setMessage("회원가입에 성공했습니다.");
            AccountInfoResDto riotAccountDto = accountApiService.getAccountInfoByRiotId(signupReqDto.getRiotId(), signupReqDto.getRiotTag());
            authMapper.insertSiteAccount(riotAccountDto.getPuuid(), signupReqDto.getEmail(), passwordEncoder.encode(signupReqDto.getPassword()));
        }

        return resDto;
    }

    public JwtToken authAndCreateJwtToken(LoginReqDto reqDto) {
        // 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(reqDto.getEmail(), reqDto.getPassword())
        );

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public JwtToken refreshAccessToken(String refreshToken) {
        // Refresh Token에서 사용자 정보 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes()))
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        String username = claims.getSubject();

        // Refresh Token 유효성 검증
        if (jwtTokenProvider.validateRefreshToken(username, refreshToken)) {
            UserDetails userDetails = userDetailService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

            // 새로운 Access Token 발급
            String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

            JwtToken tokens = JwtToken.builder()
                    .accessToken(newAccessToken)
                    .build();

            return tokens;
        } else {
            return new JwtToken();
        }
    }

    public SiteAccountEntity getMemberInfo(String email) {
        return authMapper.getSiteAccountByEmail(email);
    }

    public DiscordAccountEntity getDiscordAccountInfoBySiteCode(Long siteCode) {
        return authMapper.getDiscordAccountBySiteCode(siteCode);
    }

    public void connectDiscordAccount(long siteCode, Map<String, Object> discordAttr) {
        DiscordAccountEntity convertedEntity = this.convertAttrToDiscordAccountEntity(discordAttr);
        authMapper.conflictDiscordAccount(siteCode, convertedEntity);
    }

    public void disconnectDiscordAccount(long siteCode) {
        authMapper.disconnectDiscordAccount(siteCode);
    }

    private DiscordAccountEntity convertAttrToDiscordAccountEntity(Map<String, Object> attr) {
        return DiscordAccountEntity.builder()
                .discordCode(Optional.ofNullable(attr.get("id")).map(Object::toString).orElse(null))
                .username(Optional.ofNullable(attr.get("username")).map(Object::toString).orElse(null))
                .globalName(Optional.ofNullable(attr.get("global_name")).map(Object::toString).orElse(null))
                .avatar(Optional.ofNullable(attr.get("avatar")).map(Object::toString).orElse(null))
                .banner(Optional.ofNullable(attr.get("banner")).map(Object::toString).orElse(null))
                .bannerColor(Optional.ofNullable(attr.get("banner_color")).map(Object::toString).orElse(null))
                .accentColor(Optional.ofNullable(attr.get("accent_color")).map(Object::toString).orElse(null))
                .avatarDecorationData(Optional.ofNullable(attr.get("avatar_decoration_data")).map(Object::toString).orElse(null))
                .email(Optional.ofNullable(attr.get("email")).map(Object::toString).orElse(null))
                .verified((Boolean) attr.get("verified"))
                .mfaEnabled((Boolean) attr.get("mfa_enabled"))
                .publicFlags(Optional.ofNullable(attr.get("public_flags")).map(f -> ((Integer)f) == 1).orElse(false))
                .flags(Optional.ofNullable(attr.get("flags")).map(f -> ((Integer)f) == 1).orElse(false))
                .locale(Optional.ofNullable(attr.get("locale")).map(Object::toString).orElse(null))
                .clan(Optional.ofNullable(attr.get("clan")).map(Object::toString).orElse(null))
                .primaryGuild(Optional.ofNullable(attr.get("primary_guild")).map(Object::toString).orElse(null))
                .build();
    }
}
