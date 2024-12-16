package gg.updown.backend.main.api.auth.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteAccountEntity {
    private Long memberSiteCode;
    private String puuid;
    private String memberEmail;
    private String memberPassword;
    private LocalDateTime regDt;
    private LocalDateTime updDt;
    private LocalDateTime delDt;
    private LocalDateTime lastLoginDt;
}
