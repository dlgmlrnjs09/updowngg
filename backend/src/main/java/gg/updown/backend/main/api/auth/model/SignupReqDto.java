package gg.updown.backend.main.api.auth.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupReqDto {
    private String email;
    private String password;
    private String passwordConfirm;
    private String riotId;
    private String riotTag;
}
