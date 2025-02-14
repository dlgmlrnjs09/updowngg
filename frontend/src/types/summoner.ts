// src/types/summoner.ts
import type {SummonerBasicInfoDto} from "@/types/ranking.ts";

export interface LolSummonerDto {
    puuid: string
    lolAccountSeq: number
    summonerId: string
    accountId: string
    summonerLevel: number
    externalModDt: string
    profileIconId: string
    profileIconUrl: string
}

export interface RiotAccountInfoEntity {
    puuid: string
    gameName: string
    tagLine: string
}

export interface LolSummonerProfileResDto {
    summonerBasicInfoDto: SummonerBasicInfoDto
    riotAccountInfoEntity: RiotAccountInfoEntity
}

export interface LolSummonerMostChampionDto {
    nameUs: string
    nameKr: string
    playCount: number
    winCount: number
    winRate: number
    upCount: number
    downCount: number
    iconUrl: string
}