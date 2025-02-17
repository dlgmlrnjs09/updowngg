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
    const months = Math.floor(days / 30);
    const years = Math.floor(months / 12);

    if (minutes < 60) {
        return `${minutes}분 전`;
    } else if (hours < 24) {
        return `${hours}시간 전`;
    } else if (days < 30) {
        return `${days}일 전`;
    } else if (months < 12) {
        return `${months}달 전`;
    } else {
        return `${years}년 전`;
    }
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

export const convertTierRomanToNumber = (roman: string): number => {
    const romanValues: { [key: string]: number } = {
        'I': 1,
        'II': 2,
        'III': 3,
        'IV': 4
    };

    return romanValues[roman] || 0;
};