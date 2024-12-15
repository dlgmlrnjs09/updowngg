import { apiClient } from './axios'
import type {ReviewRequestDto, ReviewTagDto} from '@/types/review'

export const reviewApi = {
    getTagList: () =>
        apiClient.get<ReviewTagDto[]>('/review/tag', {}),

    submitReview: (reviewData: ReviewRequestDto) =>
        apiClient.put('/review/submit', reviewData)
}

