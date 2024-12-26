package gg.updown.backend.main.api.ranking.model;

import gg.updown.backend.main.api.review.model.dto.ReviewTagDto;
import gg.updown.backend.main.api.review.model.entity.ReviewTagEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SiteRankingCardResDto {
    private String puuid;
    private String profileIconId;
    private String profileIconUrl;
    private String gameName;
    private String tagLine;
    private int upCount;
    private int downCount;

    private int totalReviewCount;
    private int last30DayReviewCnt;
    private List<ReviewTagDto> recentTags;
}