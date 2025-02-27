package gg.updown.backend.external.riot.api.rso.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RiotUserInfoDto {
    private String sub;  // 라이엇 계정 고유 ID
    private String cpid; // 게임 지역 (NA1, KR 등)
}