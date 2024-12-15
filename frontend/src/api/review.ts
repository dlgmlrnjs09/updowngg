import { apiClient } from './axios'
import type { ReviewTagDto } from '@/types/review'

export const reviewApi = {
    getTagList: () =>
        apiClient.get<ReviewTagDto[]>('/review/tag', {})
}