export interface ReviewTagDto {
    tagCode: string
    tagValue: string
    tagDescription: string
}

export interface ReviewRequestDto {
    summonerReviewSeq: number
    reviewerSiteCode: string;
    reviewerPuuid: string;
    targetPuuid: string;
    skillRating: number;
    teamworkRating: number;
    mannerRating: number;
    comment?: string;
    tagCodeList: string[];  // 선택한 태그의 코드 배열
    reviewable: boolean | false;
}