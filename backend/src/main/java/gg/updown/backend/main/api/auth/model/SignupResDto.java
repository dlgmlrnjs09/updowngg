package gg.updown.backend.main.api.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 성공여부 DTO")
public class SignupResDto {
    @Schema(description = "성공 여부")
    private boolean success;
    @Schema(description = "메시지")
    private String message;
}
