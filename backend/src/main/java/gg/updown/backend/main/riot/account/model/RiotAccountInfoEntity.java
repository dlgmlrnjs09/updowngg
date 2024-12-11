package gg.updown.backend.main.riot.account.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiotAccountInfoEntity {
    private String puuid;
    private String gameName;
    private String tagLine;
}
