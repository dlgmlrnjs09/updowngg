package gg.updown.backend.main.api.notice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResDto {
    @Schema(description = "시퀀스")
    private Long seq;

    @Schema(description = "공지사항 내용")
    private String content;

    @Schema(description = "배경색")
    private String backgroundColor;
}
