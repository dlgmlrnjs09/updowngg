// src/api/summoner.ts
import apiClient from './axios'
import type { LolSummonerProfileResDto } from '@/types/summoner'

export const summonerApi = {
    getInfoBySummonerId: (summonerId: string, tagLine: string) =>
        apiClient.get<LolSummonerProfileResDto>(`/api/v1/summoner/info/by-summoner`, {
            params: {
                summonerId,
                tagLine
            }
        }),
    getInfoByPuuid: (puuid: string) =>
        apiClient.get<LolSummonerProfileResDto>(`/api/v1/summoner/info/by-puuid`, {
            params: {
                puuid
            }
        })
}