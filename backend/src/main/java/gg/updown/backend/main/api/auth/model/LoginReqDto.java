package gg.updown.backend.main.api.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginReqDto {
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "비밀번호")
    private String password;
}
