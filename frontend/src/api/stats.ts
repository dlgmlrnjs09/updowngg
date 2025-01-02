import  apiClient  from './axios'
import type {ChampionResDto} from "@/types/stats.ts";
import type {SearchFilter} from "@/types/stats.ts";

export const statsApi = {
    getChampionList: (filter?: SearchFilter) =>
        apiClient.get<ChampionResDto[]>('/api/v1/stats/champions', {
            params: {
                matchGameMode: filter?.queueType,
                matchPosition: filter?.position,
                tier: filter?.tier,
                period: filter?.period
            }
        })
}