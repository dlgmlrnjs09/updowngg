import apiClient from './axios'
import type {
    ReviewHistoryDto,
    ReviewRatingByChampDto,
    ReviewRatingByPositionDto,
    ReviewRequestDto,
    ReviewStatsDto, ReviewTagCategoryDto,
    ReviewTagDto, ReviewTagSuggestDto
} from '@/types/review'
import type {LolSummonerProfileResDto} from "@/types/summoner.ts";

export const reviewApi = {
    getTagList: () =>
        apiClient.get<ReviewTagDto[]>('/api/v1/review/tag', {}),

    getTagCategoryList: () =>
        apiClient.get<ReviewTagCategoryDto[]>('/api/v1/review/tag-category', {}),

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

    suggestReviewTags: (tag: ReviewTagSuggestDto[]) =>
        apiClient.post<number>('/api/v1/review/tag-suggest', tag),

    getWrittenReviewHistory: (limit: number, offset: number) =>
        apiClient.get<ReviewHistoryDto[]>('/api/v1/review/history/written', {
            params: {
                limit,
                offset
            }
        }),
}

