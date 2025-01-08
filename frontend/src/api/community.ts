import apiClient from "@/api/axios.ts";
import type {LolMatchInfoRes} from "@/types/match.ts";
import type {CommunityPostDto, DuoPostCardDto} from "@/types/community.ts";
import type {ReviewRequestDto} from "@/types/review.ts";

export const communityApi = {
    insertPost: (communityCode: string, communityPostDto: CommunityPostDto) =>
        apiClient.post<void>('/api/v1/community/' + communityCode + '/submit', communityPostDto),
    getPost: (communityCode: string) =>
        apiClient.get<DuoPostCardDto[]>('/api/v1/community/' + communityCode +'/list', {
            params: {
            }
        }),
}