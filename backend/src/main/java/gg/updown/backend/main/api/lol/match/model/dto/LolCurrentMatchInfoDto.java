package gg.updown.backend.main.api.lol.match.model.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LolCurrentMatchInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private LolMatchInfoDto matchInfoDto;
    private List<LolCurrentMatchParticipantDto> participantDtoList;
}
