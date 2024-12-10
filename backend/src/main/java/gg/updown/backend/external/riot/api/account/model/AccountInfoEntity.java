package gg.updown.backend.external.riot.api.account.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfoEntity {
    private String puuid;
    private String gameName;
    private String tagLine;
}
