import  apiClient  from './axios'
import type { LolMatchInfoRes } from '@/types/match'
import type {ReviewRequestDto} from "@/types/review.ts";

export const matchApi = {
    getMatchList: (puuid: string, startIndex: number, count: number) =>
        apiClient.get<LolMatchInfoRes[]>('/api/v1/match/list', {
            params: {
                puuid,
                startIndex,
                count
            }
        }),
    updateMatchList: (puuid: string) =>
        apiClient.get<string[]>('/api/v1/match/update', {
            params: {
                puuid,
            }
        }),

    getMatchInfo: (matchId: string) =>
        apiClient.get<LolMatchInfoRes>(`/api/v1/match/${matchId}`)
}