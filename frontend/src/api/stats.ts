import  apiClient  from './axios'
import type {ChampionResDto} from "@/types/stats.ts";

export const statsApi = {
    getChampionList: () =>
        apiClient.get<ChampionResDto[]>('/stats/champions', {
            params: {
            }
        })
}