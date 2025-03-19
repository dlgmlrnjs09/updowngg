package gg.updown.backend.external.riot.api.lol.match.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiotMatchRawEntity {
    private Long id;
    private String matchId;
    private String matchData; // Raw JSON data as string
    private String region;
    private LocalDateTime savedAt;
}