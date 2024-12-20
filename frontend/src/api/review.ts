import apiClient from './axios'
import type {ReviewRequestDto, ReviewStatsDto, ReviewTagDto} from '@/types/review'
import type {LolSummonerProfileResDto} from "@/types/summoner.ts";

export const reviewApi = {
    getTagList: () =>
        apiClient.get<ReviewTagDto[]>('/review/tag', {}),

    submitReview: (reviewData: ReviewRequestDto) =>
        apiClient.post('/review/submit', reviewData),

    updateReview: (reviewData: ReviewRequestDto) =>
        apiClient.put('/review/update', reviewData),

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
        })
}

