import type {
    ReviewStatsDto, ReviewTagDto
} from '@/types/review'
import type {ChampionResDto, SearchFilter} from "@/types/stats.ts";

export interface Tab {
    label: string
    value: string
    iconPath: string
}


export interface SummonerBasicInfoDto {
    puuid: string
    gameName: string
    tagLine: string
    profileIconId: string
    profileIconUrl: string
}

export interface RankerPlayer {
    summonerBasicInfoDto: SummonerBasicInfoDto
    reviewStatsDto: ReviewStatsDto
    championStatsDtoList: ChampionResDto[]
    reviewTagDtoList: ReviewTagDto[]
}

export interface RankingSearchFilter {
    sortTypeReqDto: SearchFilter
    limit: number
    offset: number
}