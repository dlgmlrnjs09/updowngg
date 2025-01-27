
package gg.updown.backend.main.api.review.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "사이트 소환사 리뷰정보 DTO")
public class ReviewDto {
    @Schema(description = "리뷰 Seq")
    private int summonerReviewSeq;
    @Schema(description = "LOL 경기 고유ID")
    private String matchId;
    @Schema(description = "리뷰 작성자 사이트 고유ID")
    private String reviewerSiteCode;
    @Schema(description = "리뷰 작성자 라이엇계정 고유ID")
    private String reviewerPuuid;
    @Schema(description = "리뷰 대상자 라이엇계정 고유ID")
    private String targetPuuid;
    @Schema(description = "추천 여부")
    private Boolean isUp;
    @Schema(description = "리뷰 내용")
    private String comment;
    @Schema(description = "등록일시")
    private LocalDateTime regDt;
    @Schema(description = "수정일시")
    private LocalDateTime updDt;
    @Schema(description = "삭제일시")
    private LocalDateTime delDt;
    @Schema(description = "리뷰 고유코드 리스트")
    private List<String> tagCodeList;
    @Schema(description = "리뷰 DTO 리스트")
    private List<ReviewTagDto> tagDtoList;
    @Schema(description = "리뷰 대상 프로필아이콘 ID")
    private String targetProfileIconId;
    @Schema(description = "리뷰 대상 프로필아이콘 URL")
    private String targetProfileIconUrl;
    @Schema(description = "리뷰 대상 라이엇 닉네임")
    private String targetGameName;
    @Schema(description = "리뷰 대상 라이엇 배틀태그")
    private String targetTagLine;

    @Schema(description = "이미작성 여부")
    private boolean reviewable;
}
