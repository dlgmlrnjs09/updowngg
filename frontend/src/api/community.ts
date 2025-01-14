import apiClient from "@/api/axios.ts";
import type {LolMatchInfoRes} from "@/types/match.ts";
import type {CommunityPostDto, DuoPostCardDto, SearchFilter} from "@/types/community.ts";
import type {ReviewRequestDto} from "@/types/review.ts";

export const communityApi = {
    insertPost: (communityCode: string, communityPostDto: CommunityPostDto) =>
        apiClient.post<void>('/api/v1/community/' + communityCode + '/submit', communityPostDto),
    getPost: (communityCode: string, filter: SearchFilter) =>
        apiClient.get<DuoPostCardDto[]>('/api/v1/community/' + communityCode +'/list', {
            params: {
                gameMode: filter?.gameMode,
                tier: filter?.tier,
                positionSelf: filter?.positionSelf,
                positionFind: filter?.positionFind,
                limit: filter?.limit,
                offset: filter?.offset
            }
        }),
}