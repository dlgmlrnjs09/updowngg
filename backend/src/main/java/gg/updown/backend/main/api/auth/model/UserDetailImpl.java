package gg.updown.backend.main.api.auth.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailImpl implements UserDetails {
    private final SiteAccountEntity user;

    public UserDetailImpl(SiteAccountEntity user) {
        this.user = user;
    }

    public Long getSiteCode() {
        return user.getMemberSiteCode();
    }

    public String getPuuid() {
        return user.getPuuid();
    }

    /**
     * 사용자의 권한 목록 반환 (미사용)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                .collect(Collectors.toList());

        return null;
    }

    @Override
    public String getPassword() {
        return user.getMemberPassword();
    }

    @Override
    public String getUsername() {
        return user.getPuuid();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
