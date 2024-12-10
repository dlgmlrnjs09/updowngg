package gg.updown.backend.external.riot.api.account.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountInfoResDto {
    private String puuid;
    private String gameName;
    private String tagLine;
}
