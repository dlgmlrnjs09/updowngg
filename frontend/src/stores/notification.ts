import {defineStore} from "pinia";
import type {Notification} from "@/types/notification.ts";
import {notificationApi} from '@/api/notification'

const NOTIFICATION_TYPES = [
    { code: 'SITE_REVIEW', eventName: 'review-notification', description: '리뷰' },
    { code: 'PARTY_COMMUNITY', eventName: 'party-notification', description: '파티 커뮤니티' },
];

export const useNotificationStore = defineStore('notification', {
    state: () => ({
        eventSource: null as EventSource | null,
        notifications: [] as Notification[],
        unreadCount: 0
    }),

    actions: {
        initSSE() {
            if (this.eventSource) {
                this.eventSource.close();
            }


            this.eventSource = notificationApi.subscribe();

            // forEach 전후에 로그 추가
            NOTIFICATION_TYPES.forEach((type, index) => {

                if (this.eventSource) {
                    this.eventSource.addEventListener(type.code, (event) => {
                        const notification = JSON.parse(event.data);
                        this.notifications = [notification, ...this.notifications];
                        this.unreadCount++;
                    });
                } else {
                    console.error("eventSource가 null입니다");
                }
            });

            if (this.eventSource) {
                this.eventSource.onopen = () => {
                };

                this.eventSource.onerror = (error) => {
                    console.error("SSE 오류 발생:", error);
                    this.eventSource?.close();
                    setTimeout(() => this.initSSE(), 10000);
                };
            }

            this.fetchNotifications();
        },

        async fetchNotifications() {
            try {
                const response = await notificationApi.getNotifications()
                this.notifications = response.data
                this.unreadCount = this.notifications.filter(n => !n.readYn).length
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