import apiClient from './axios'
import type {
    ReviewRatingByChampDto,
    ReviewRatingByPositionDto,
    ReviewRequestDto,
    ReviewStatsDto,
    ReviewTagDto
} from '@/types/review'
import type {LolSummonerProfileResDto} from "@/types/summoner.ts";

export const reviewApi = {
    getTagList: () =>
        apiClient.get<ReviewTagDto[]>('/review/tag', {}),

    submitReview: (reviewData: ReviewRequestDto) =>
        apiClient.post<number>('/review/submit', reviewData),

    updateReview: (reviewData: ReviewRequestDto) =>
        apiClient.put<number>('/review/update', reviewData),

    getReviewStats: (puuid: string) =>
        apiClient.get<ReviewStatsDto>('/review/stats', {
            params: {
                puuid
            }
        }),

    getReviewTagFrequent: (puuid: string) =>
        apiClient.get<ReviewTagDto>('/review/tag-frequent', {
            params: {
                puuid
            }
        }),

    getRecentReviews: (puuid: string) =>
        apiClient.get<ReviewRequestDto>('/review/recent', {
            params: {
                puuid
            }
        }),

    getRatingByChamp: (puuid: string) =>
        apiClient.get<ReviewRatingByChampDto>('/review/rating-avg/champ', {
            params: {
                puuid
            }
        }),

    getRatingByPosition: (puuid: string) =>
        apiClient.get<ReviewRatingByPositionDto>('/review/rating-avg/position', {
            params: {
                puuid
            }
        }),
}

