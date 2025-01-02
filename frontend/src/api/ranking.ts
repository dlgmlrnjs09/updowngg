import apiClient from "@/api/axios.ts";
import type {LolMatchInfoRes} from "@/types/match.ts";
import type {RankerPlayer, RankingSearchFilter} from "@/types/ranking.ts";

export const rankingApi = {
    getRankerCardList: (filter: RankingSearchFilter) =>
        apiClient.get<RankerPlayer[]>('/api/v1/rank/list', {
            params: {
                matchGameMode: filter?.sortTypeReqDto?.queueType,
                matchPosition: filter?.sortTypeReqDto?.position,
                tier: filter?.sortTypeReqDto?.tier,
                period: filter?.sortTypeReqDto?.period,
                limit: filter?.limit,
                offset: filter?.offset,
            }
        }),
}