import apiClient from './axios'
import type {Notification} from '@/types/notification'

export const notificationApi = {
    // 알림 목록 조회
    getNotifications: () =>
        apiClient.get<Notification[]>('/api/v1/notification/list'),

    // 알림 읽음 처리
    markAsRead: (id: string) =>
        apiClient.post(`/api/v1/notification/${id}/read`),

    // 실시간 알림 구독
    subscribe: () => {
        const token = localStorage.getItem('accessToken')
        return new EventSource(`${import.meta.env.VITE_API_URL}/api/v1/notification/subscribe`, {
            withCredentials: true,
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
    },
}