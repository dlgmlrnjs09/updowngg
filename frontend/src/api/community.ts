import apiClient from "@/api/axios.ts";
import type {
    CommunityPostDto,
    DuoPostCardDto,
    PartyCommunityApplicantDto,
    PartyPostCardDto,
    SearchFilter
} from "@/types/community.ts";

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

    getPartyPost: (communityCode: string, filter: SearchFilter) =>
        apiClient.get<PartyPostCardDto[]>('/api/v1/community/' + communityCode +'/list', {
            params: {
                gameMode: filter?.gameMode,
                tier: filter?.tier,
                positionSelf: filter?.positionSelf,
                positionFind: filter?.positionFind,
                limit: filter?.limit,
                offset: filter?.offset
            }
        }),

    applyParty: (postId: number, position: string) =>
        apiClient.post<boolean>('/api/v1/community/party/apply', {
            postId: postId,
            position: position
        }),

    getApplyList: (postIds: number[]) =>
        apiClient.post<PartyCommunityApplicantDto[]>('/api/v1/community/party/apply/list', postIds),
}