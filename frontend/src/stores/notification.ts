import {defineStore} from "pinia";
import type {Notification} from "@/types/notification.ts";
import {notificationApi} from '@/api/notification'

export const useNotificationStore = defineStore('notification', {
    state: () => ({
        eventSource: null as EventSource | null,
        notifications: [] as Notification[],
        unreadCount: 0
    }),

    actions: {
        initSSE() {
            console.log("SSE 초기화");
            if (this.eventSource) {
                console.log("기존 SSE 연결 종료");
                this.eventSource.close()
            }

            this.eventSource = notificationApi.subscribe()
            console.log("새 SSE 연결 생성:", this.eventSource.readyState);

            this.eventSource.addEventListener('review-notification', (event) => {
                console.log("리뷰 알림 수신");
                const notification = JSON.parse(event.data)
                this.notifications = [notification, ...this.notifications]
                this.unreadCount++
            })

            this.eventSource.onerror = () => {
                this.eventSource?.close()
                setTimeout(() => this.initSSE(), 10000)
            }

            this.fetchNotifications()
        },

        async fetchNotifications() {
            try {
                const response = await notificationApi.getNotifications()
                this.notifications = response.data
                this.unreadCount = this.notifications.filter(n => !n.readYn).length
                console.log('response == ' + JSON.stringify(response.data))
            } catch (error) {
                console.error('Failed to fetch notifications:', error)
            }
        },

        async markAsRead(id: string) {
            try {
                await notificationApi.markAsRead(id)
                const notification = this.notifications.find(n => n.notificationId === id)
                if (notification && !notification.readYn) {
                    notification.readYn = true
                    this.unreadCount--
                }
            } catch (error) {
                console.error('Failed to mark notification as read:', error)
            }
        },

        async markAsReadAll(ids: string[]) {
            try {
                await notificationApi.markAsReadAll(ids)
                this.notifications.forEach(notification => {
                    notification.readYn = true
                })
                this.unreadCount = 0
            } catch (error) {
                console.error('Failed to mark all notifications as read:', error)
            }
        },

        clearEventSource() {
            if (this.eventSource) {
                this.eventSource.close()
                this.eventSource = null
            }
        },
    }
})