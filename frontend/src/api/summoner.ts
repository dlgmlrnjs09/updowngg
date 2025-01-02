// src/api/summoner.ts
import apiClient from './axios'
import type { LolSummonerProfileResDto } from '@/types/summoner'

export const summonerApi = {
    getInfo: (summonerId: string, tagLine: string) =>
        apiClient.get<LolSummonerProfileResDto>('/api/v1/summoner/info', {
            params: {
                summonerId,
                tagLine
            }
        })
}