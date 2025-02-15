// src/utils/cookie.ts
export interface RecentSummoner {
    gameName: string;
    tagLine: string;
    lastViewedAt: number;
}

const RECENT_SUMMONERS_KEY = 'recent_summoners';
const MAX_RECENT_SUMMONERS = 5;

export const cookieUtils = {
    addRecentSummoner(summoner: RecentSummoner) {
        try {
            // 기존 검색 기록 가져오기
            let recentSummoners: RecentSummoner[] = [];
            const storedData = localStorage.getItem(RECENT_SUMMONERS_KEY);

            if (storedData) {
                recentSummoners = JSON.parse(storedData);
            }

            // 중복 제거 (같은 소환사가 있으면 제거)
            recentSummoners = recentSummoners.filter(
                s => !(s.gameName === summoner.gameName && s.tagLine === summoner.tagLine)
            );

            // 새로운 소환사 추가
            recentSummoners.unshift(summoner);

            // 최대 개수 유지
            if (recentSummoners.length > MAX_RECENT_SUMMONERS) {
                recentSummoners = recentSummoners.slice(0, MAX_RECENT_SUMMONERS);
            }

            // 저장
            localStorage.setItem(RECENT_SUMMONERS_KEY, JSON.stringify(recentSummoners));
        } catch (error) {
            console.error('Failed to save recent summoner:', error);
        }
    },

    getRecentSummoners(): RecentSummoner[] {
        try {
            const storedData = localStorage.getItem(RECENT_SUMMONERS_KEY);
            if (storedData) {
                return JSON.parse(storedData);
            }
        } catch (error) {
            console.error('Failed to get recent summoners:', error);
        }
        return [];
    }
};