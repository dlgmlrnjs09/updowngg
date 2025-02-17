// stores/notice.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { noticeApi } from "@/api/notice.ts"
import type { NoticeResDto } from "@/types/notice.ts"

export const useNoticeStore = defineStore('notice', () => {
    const notices = ref<NoticeResDto[]>([])
    const lastFetchTime = ref<Date | null>(null)
    const REFRESH_INTERVAL = 10 * 60 * 1000 // 10분을 밀리초로 변환

    const fetchNotices = async () => {
        try {
            const response = await noticeApi.getDisplayNoticeList();
            notices.value = response.data;
            lastFetchTime.value = new Date();
        } catch (error) {
            console.error('Failed to fetch notices:', error);
        }
    };

    const shouldRefetch = () => {
        if (!lastFetchTime.value) return true;

        const now = new Date();
        const timeDiff = now.getTime() - lastFetchTime.value.getTime();
        return timeDiff >= REFRESH_INTERVAL;
    };

    const getNotices = async () => {
        if (shouldRefetch()) {
            await fetchNotices();
        }
        return notices.value;
    };

    return {
        notices,
        getNotices,
        fetchNotices
    }
})