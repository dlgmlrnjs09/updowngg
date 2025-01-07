import apiClient from "@/api/axios.ts";
import type {LolMatchInfoRes} from "@/types/match.ts";
import type {CommunityPostDto} from "@/types/community.ts";

export const communityApi = {
    insertPost: (communityCode: string, communityPostDto: CommunityPostDto) =>
        apiClient.post<void>('/api/v1/community/' + communityCode + '/submit', communityPostDto),
}