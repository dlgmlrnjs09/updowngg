package gg.updown.backend.external.riot.api.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoResDto {
    private String puuid;
    private String gameName;
    private String tagLine;
}
