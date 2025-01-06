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
        apiClient.get<ReviewTagDto[]>('/api/v1/review/tag', {}),

    submitReview: (reviewData: ReviewRequestDto) =>
        apiClient.post<number>('/api/v1/review/submit', reviewData),

    updateReview: (reviewData: ReviewRequestDto) =>
        apiClient.put<number>('/api/v1/review/update', reviewData),

    getReviewStats: (puuid: string) =>
        apiClient.get<ReviewStatsDto>('/api/v1/review/stats', {
            params: {
                puuid
            }
        }),

    getReviewTagFrequent: (puuid: string) =>
        apiClient.get<ReviewTagDto>('/api/v1/review/tag-frequent', {
            params: {
                puuid
            }
        }),

    getRecentReviewsByPuuid: (puuid: string, limit: number) =>
        apiClient.get<ReviewRequestDto[]>('/api/v1/review/recent', {
            params: {
                puuid,
                limit
            }
        }),
    getRecentReviews: (limit: number) =>
        apiClient.get<ReviewRequestDto[]>('/api/v1/review/recent', {
            params: {
                limit
            }
        }),

    getRatingByChamp: (puuid: string) =>
        apiClient.get<ReviewRatingByChampDto>('/api/v1/review/rating-avg/champ', {
            params: {
                puuid
            }
        }),

    getRatingByPosition: (puuid: string) =>
        apiClient.get<ReviewRatingByPositionDto>('/api/v1/review/rating-avg/position', {
            params: {
                puuid
            }
        }),
}

