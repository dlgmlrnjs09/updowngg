package gg.updown.backend.main.api.lol.match.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolMatchInfoResDto {
    private LolMatchInfoDto matchInfo;
    private List<LolMatchParticipantDto> participantList;
}
