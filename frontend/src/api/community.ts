import apiClient from "@/api/axios.ts";
import type {
    CommunityPostDto,
    DuoPostCardDto,
    MyPartyPostDto,
    PartyCommunityApplicantDetailDto, PartyCommunityApplicantDto,
    PartyCommunityAppliedHistoryResponse,
    PartyCommunityHistoryResponse,
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
        apiClient.post<void>('/api/v1/community/party/apply', {
            postId: postId,
            position: position
        }),

    getApplyList: (postIds: number[]) =>
        apiClient.post<PartyCommunityApplicantDetailDto[]>('/api/v1/community/party/applicant/list', postIds),

    getMyApplyList: () =>
        apiClient.post<PartyCommunityApplicantDto[]>('/api/v1/community/party/my/applicant/list'),

    getMyPartyPost: () =>
        apiClient.get<MyPartyPostDto>('/api/v1/community/party/my'),

    approvePartyApplicant: (postId: number, applicantPuuid: string, applicantSeq: number, position: string) =>
        apiClient.post<boolean>('/api/v1/community/party/approve', {
            postId: postId,
            applicantPuuid: applicantPuuid,
            applicantSeq: applicantSeq,
            position: position
        }),

    rejectPartyApplicant: (postId: number, applicantPuuid: string, applicantSeq: number, position: string) =>
        apiClient.post<boolean>('/api/v1/community/party/reject', {
            postId: postId,
            applicantPuuid: applicantPuuid,
            applicantSeq: applicantSeq,
            position: position
        }),

    leaveMyParty: (postId: number) =>
        apiClient.post<boolean>('/api/v1/community/party/leave', {
            postId: postId
        }),

    closeParty: (postId: number) =>
        apiClient.post<void>(`/api/v1/community/party/close/${postId}`),

    cancelParty: (postId: number) =>
        apiClient.post<void>(`/api/v1/community/party/cancel/${postId}`),

    getPartyHostedHistory: (page: number, limit: number) =>
        apiClient.get<PartyCommunityHistoryResponse>('/api/v1/community/party/history/hosted', {
            params: {
                page,
                limit
            }
        }),

    getPartyParticipatedHistory: (page: number, limit: number) =>
        apiClient.get<PartyCommunityHistoryResponse>('/api/v1/community/party/history/participated', {
            params: {
                page,
                limit
            }
        }),

    getPartyAppliedHistory: (page: number, limit: number) =>
        apiClient.get<PartyCommunityAppliedHistoryResponse>('/api/v1/community/party/history/applied', {
            params: {
                page,
                limit
            }
        }),
}