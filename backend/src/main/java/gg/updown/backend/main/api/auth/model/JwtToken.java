package gg.updown.backend.main.api.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사이트 JWT 토큰정보")
public class JwtToken {
    @Schema(description = "사이트 액세스토큰")
    private String accessToken;
    @Schema(description = "사이트 리프레쉬토큰")
    private String refreshToken;
}
