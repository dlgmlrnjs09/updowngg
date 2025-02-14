package gg.updown.backend.external.riot.api.lol.spectator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "게임 관전자 정보")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ObserverDto {
    @Schema(description = "게임 데이터 복호화에 사용되는 암호화 키", example = "abcd1234")
    private String encryptionKey;
}
