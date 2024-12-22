export interface ReviewTagDto {
    tagCode: string
    tagValue: string
    tagDescription: string
    frequentCount: number
}

export interface ReviewRequestDto {
    summonerReviewSeq: number
    reviewerSiteCode: string;
    reviewerPuuid: string;
    targetPuuid: string;
    skillRating: number;
    teamworkRating: number;
    mannerRating: number;
    totalAvgRating: number | null;
    comment?: string;
    tagCodeList: string[];  // 선택한 태그의 코드 배열
    reviewable: boolean | false;
    regDt: string | null;
    matchId: string;
}

export interface ReviewStatsDto {
    puuid: string;
    skillRatingAvg: number;
    teamworkRatingAvg: number;
    mannerRatingAvg: number;
    totalReviewCnt: number;
    last30DayReviewCnt: number;
}

export interface ReviewRatingByChampDto {
    champId: string;
    champName: string;
    champIconUrl: string;
    playCount: number;
    totalAvgRating: number;
}

export interface ReviewRatingByPositionDto {
    position: string;
    playCount: number;
    totalAvgRating: number;
}