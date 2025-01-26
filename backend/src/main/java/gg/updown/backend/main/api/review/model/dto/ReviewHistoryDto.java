package gg.updown.backend.main.api.review.model.dto;

import gg.updown.backend.main.api.lol.match.model.dto.LolMatchInfoDto;
import gg.updown.backend.main.api.lol.match.model.dto.LolMatchParticipantDto;
import gg.updown.backend.main.api.lol.match.model.entity.LolMatchEntity;
import gg.updown.backend.main.api.lol.match.model.entity.LolMatchParticipantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewHistoryDto {
    private ReviewDto reviewDto;
    private LolMatchInfoDto matchDto;
    private List<LolMatchParticipantDto> participantDtoList;
}
