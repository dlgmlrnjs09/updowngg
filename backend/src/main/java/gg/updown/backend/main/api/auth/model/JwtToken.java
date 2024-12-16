package gg.updown.backend.main.api.auth.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtToken {
    private String accessToken;
    private String refreshToken;
}
