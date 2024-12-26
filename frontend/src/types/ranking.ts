import type {ReviewTagDto} from "@/types/review.ts";

export interface Tab {
    label: string
    value: string
    iconPath: string
}

export interface Player {
    id: string
    name: string
    tag: string
    iconUrl: string
    totalReviews: number
    recentReviews: number
    upCount: number
    downCount: number
    tags: string[]
}

export interface RankingCard {
    puuid: string
    profileIconId: string
    profileIconUrl: string
    gameName: string
    tagLine: string
    upCount: number
    downCount: number
    totalReviewCount: number
    last30DayReviewCnt: number
    recentTags: ReviewTagDto[]
}