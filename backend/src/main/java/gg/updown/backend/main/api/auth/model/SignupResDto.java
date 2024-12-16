package gg.updown.backend.main.api.auth.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResDto {
    private boolean success;
    private String message;
}
