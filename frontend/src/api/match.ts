import  apiClient  from './axios'
import type { LolMatchInfoRes } from '@/types/match'

export const matchApi = {
    getMatchList: (puuid: string, startIndex: number, count: number) =>
        apiClient.get<LolMatchInfoRes[]>('/match/list', {
            params: {
                puuid,
                startIndex,
                count
            }
        }),
    updateMatchList: (puuid: string) =>
        apiClient.get<string[]>('/match/update', {
            params: {
                puuid,
            }
        }),
}