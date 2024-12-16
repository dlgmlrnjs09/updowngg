package gg.updown.backend.main.api.auth.service;

import gg.updown.backend.main.api.auth.mapper.AuthMapper;
import gg.updown.backend.main.api.auth.model.SiteAccountEntity;
import gg.updown.backend.main.api.auth.model.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 사용자 정보 로드
 */
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final AuthMapper authMapper;

    /**
     * email로 사용자 정보 조회
     * @param email 이메일
     * @return UserDetails 구현체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        SiteAccountEntity user = authMapper.getSiteAccountByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserDetailImpl(user);
    }
}