import router from "@/router";
import type {PagingDTO} from "@/types/common.ts";
import {computed} from "vue";

export const goSelectedSummonerProfile = (name: string, tag: string) => {
    router.push({
        name: 'summoner',
        params: {
            name: name,
            tag: tag
        }
    })
}

export const formatTimeAgo = (dateString: string) => {
    const now = new Date();
    const date = new Date(dateString);
    const diff = now.getTime() - date.getTime();

    const minutes = Math.floor(diff / (1000 * 60));
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const days = Math.floor(hours / 24);

    if (minutes < 60) {
        return `${minutes}분 전`;
    } else if (hours < 24) {
        return `${hours}시간 전`;
    } else if (days < 30) {
        return `${days}일 전`;
    }
    return date.toLocaleDateString();
}

export const createInitialPaging = <T>(): PagingDTO<T> => ({
    items: [],
    currentPage: 1,
    itemsPerPage: 10,
    totalItems: 0,
    totalPages: 5,
    displayPageCount: 5,
    startPage: 1,
    endPage: 1,
    hasPrevious: false,
    hasNext: false
})

export const getPageNumbers = (paging: PagingDTO<any>): number[] => {
    return Array.from(
        { length: paging.endPage - paging.startPage + 1 },
        (_, i) => paging.startPage + i
    )
}