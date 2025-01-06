package gg.updown.backend.main.api.auth.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteAccountResEntity {
    @Schema(description = "사이트 계정 고유ID")
    private Long memberSiteCode;
    @Schema(description = "라이엇 계정 고유ID")
    private String puuid;
    @Schema(description = "사이트 계정 이메일")
    private String memberEmail;
}
