package gg.updown.backend.main.api.community.party.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PartyCommunityKickReqDto {
    @NotNull(message = "postId는 필수값입니다.")
    private long postId;
    @NotNull(message = "puuid는 필수값입니다.")
    private String puuid;
}
