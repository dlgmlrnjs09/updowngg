import  apiClient  from './axios'
import type {CurrentMatchInfoDto, LolMatchInfoRes} from '@/types/match'

export const matchApi = {
    getMatchList: (
        puuid: string,
        startIndex: number,
        count: number,
        gameMode: string = 'ALL',
        reviewFilter?: 'reviewed' | 'unreviewed'
    ) =>
        apiClient.get<LolMatchInfoRes[]>('/api/v1/match/list', {
            params: {
                puuid,
                startIndex,
                count,
                gameMode,
                reviewFilter
            }
        }),
    updateMatchList: (puuid: string) =>
        apiClient.get<string[]>('/api/v1/match/update', {
            params: {
                puuid,
            }
        }),

    getMatchInfo: (matchId: string) =>
        apiClient.get<LolMatchInfoRes>(`/api/v1/match/${matchId}`),

    checkPlayedTogether: (targetPuuid: string, startDate: string, endDate: string) =>
        apiClient.get<boolean>(`/api/v1/match/played-together`, {
            params: {
                targetPuuid,
                startDate,
                endDate
            }
        }),

    getLatestMatchInfoTogether: (targetPuuid: string, startDate: string, endDate: string) =>
        apiClient.get<LolMatchInfoRes>(`/api/v1/match/played-together/latest`, {
            params: {
                targetPuuid,
                startDate,
                endDate
            }
        }),

    getCurrentMatchInfo: (puuid: string) =>
        apiClient.get<CurrentMatchInfoDto>(`/api/v1/match/current/${puuid}`),
}