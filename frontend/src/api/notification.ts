import apiClient from './axios'
import type {Notification, ReviewNotification} from '@/types/notification'

export const notificationApi = {
    // 알림 목록 조회
    getNotifications: () =>
        apiClient.get<ReviewNotification[]>('/api/v1/notification/list'),

    // 알림 읽음 처리
    markAsRead: (id: string) =>
        apiClient.post(`/api/v1/notification/read/${id}`),

    markAsReadAll: (ids: string[]) =>
        apiClient.post('/api/v1/notification/read/all', ids),

    // 실시간 알림 구독
    subscribe: () => {
        const token = localStorage.getItem('accessToken')
        return new EventSource(
            `${import.meta.env.VITE_API_URL}/api/v1/notification/subscribe?token=${token}`,
            { withCredentials: true }
        )
    },
}