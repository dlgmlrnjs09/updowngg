package gg.updown.backend.main.api.lol.match.model.dto;

import gg.updown.backend.main.api.review.model.dto.ReviewByMatchSummaryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "LOL 경기/참여자/리뷰 정보 DTO")
public class LolMatchInfoResDto {
    private LolMatchInfoDto matchInfo;
    private List<LolMatchParticipantDto> participantList;
    private ReviewByMatchSummaryDto reviewByMatchSummaryDto;
}
