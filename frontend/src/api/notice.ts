import apiClient from "@/api/axios.ts";
import type {LolMatchInfoRes} from "@/types/match.ts";
import type {NoticeResDto} from "@/types/notice.ts";

export const noticeApi = {
    getDisplayNoticeList: () =>
        apiClient.get<NoticeResDto[]>('/api/v1/notice/list'),
}