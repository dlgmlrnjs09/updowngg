import apiClient from "@/api/axios.ts";
import type {SearchSummonerDto} from "@/types/search.ts";

export const searchApi = {
    searchSummoner: (keyword: string) =>
        apiClient.get<SearchSummonerDto[]>(`/api/v1/search/summoner`, {
            params: {
                keyword
            }
        }),
}