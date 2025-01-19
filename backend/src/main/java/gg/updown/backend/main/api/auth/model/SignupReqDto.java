package gg.updown.backend.main.api.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupReqDto {
    private String email;
    private String password;
    private String passwordConfirm;
    private String riotId;
    private String riotTag;
}
