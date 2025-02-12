package gg.updown.backend.main.api.community.duo.model;

import gg.updown.backend.main.api.community.common.model.CommunityPostDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuoPostCardDto extends CommunityPostDto {
    // 게시글 상세정보 DTO
    private DuoCommunityPostDto postDto;
    // 소환사 정보 DTO
    private DuoSummonerInfoDto duoSummonerInfoDto;
}
