package gg.updown.backend.main.api.notice.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeEntity {
    @Schema(description = "시퀀스")
    private Long seq;

    @Schema(description = "공지사항 내용")
    private String content;

    @Schema(description = "배경색")
    private String backgroundColor;

    @Schema(description = "표시 여부")
    private Boolean isDisplay;

    @Schema(description = "등록일시")
    private LocalDateTime regDt;

    @Schema(description = "삭제일시")
    private LocalDateTime delDt;
}
