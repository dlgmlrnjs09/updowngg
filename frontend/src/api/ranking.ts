import apiClient from "@/api/axios.ts";
import type {LolMatchInfoRes} from "@/types/match.ts";
import type {RankingCard} from "@/types/ranking.ts";

export const rankingApi = {
    getRankerCardList: (offset: number, limit: number) =>
        apiClient.get<RankingCard[]>('/rank/list', {
            params: {
                offset, limit
            }
        }),
}