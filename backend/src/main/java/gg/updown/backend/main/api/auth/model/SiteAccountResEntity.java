package gg.updown.backend.main.api.auth.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteAccountResEntity {
    private Long memberSiteCode;
    private String puuid;
    private String memberEmail;
}
