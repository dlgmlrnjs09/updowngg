// src/types/summoner.ts
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
    lolSummonerDto: LolSummonerDto
    riotAccountInfoEntity: RiotAccountInfoEntity
}