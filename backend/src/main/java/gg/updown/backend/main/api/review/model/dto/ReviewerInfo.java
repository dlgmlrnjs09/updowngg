package gg.updown.backend.main.api.review.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewerInfo {
    private Integer reviewerChampId;
    private String reviewerChampName;
    private String reviewerChampIconUrl;
    private Boolean isAnonymous;

    public void setReviewerChampIconUrl(String iconUrl) {
        iconUrl = iconUrl.replaceAll("FiddleSticks", "Fiddlesticks");
        this.reviewerChampIconUrl = iconUrl;
    }

    public void anonymizeReviewerInfo() {
        this.reviewerChampId = null;
        this.reviewerChampName = "";
        this.reviewerChampIconUrl = "";
    }
}
