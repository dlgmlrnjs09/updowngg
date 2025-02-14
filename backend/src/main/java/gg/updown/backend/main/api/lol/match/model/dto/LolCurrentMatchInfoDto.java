package gg.updown.backend.main.api.lol.match.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolCurrentMatchInfoDto {
    private LolMatchInfoDto matchInfoDto;
    private List<LolCurrentMatchParticipantDto> participantDtoList;
}
