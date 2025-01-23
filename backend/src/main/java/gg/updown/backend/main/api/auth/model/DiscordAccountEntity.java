package gg.updown.backend.main.api.auth.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscordAccountEntity {
    private Integer memberSiteCode;
    private String discordCode;
    private String username;
    private String globalName;
    private String avatar;
    private String banner;
    private String bannerColor;
    private String accentColor;
    private String avatarDecorationData;
    private String email;
    private Boolean verified;
    private Boolean mfaEnabled;
    private Boolean publicFlags;
    private Boolean flags;
    private String locale;
    private String clan;
    private String primaryGuild;
    private LocalDateTime regDt;
    private String updDt;
    private LocalDateTime delDt;
}
