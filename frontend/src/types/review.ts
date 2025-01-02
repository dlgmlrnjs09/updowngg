export interface ReviewTagDto {
    tagCode: string
    tagValue: string
    tagDescription: string
    tagUpdown: boolean
    frequentCount: number
}

export interface ReviewRequestDto {
    summonerReviewSeq: number
    reviewerSiteCode: string;
    reviewerPuuid: string;
    targetPuuid: string;
    comment?: string;
    isUp: boolean | null;
    tagCodeList: string[];  // 선택한 태그의 코드 배열
    tagDtoList: ReviewTagDto[];
    reviewable: boolean | false;
    regDt: string;
    matchId: string;
}

export interface ReviewStatsDto {
    puuid: string;
    upCount: number;
    downCount: number;
    upRatio: number;
    downRatio: number;
    totalReviewCnt: number;
    last30DayReviewCnt: number;
    playCount: number
    score: number
}

export interface ReviewRatingByChampDto {
    champId: string;
    champName: string;
    champIconUrl: string;
    playCount: number;
    upCount: number;
    downCount: number;
}

export interface ReviewRatingByPositionDto {
    position: string;
    playCount: number;
    upCount: number;
    downCount: number;
}