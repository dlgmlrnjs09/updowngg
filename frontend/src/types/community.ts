export interface CommunityPostDto {
    postId?: number;
    communityCode: string;
    title?: string;
    content: string;
    regDt?: string;
    updDt?: string;
    delDt?: string;
    duoCommunityEntity?: DuoCommunityDto; // 자식 엔티티는 선택 사항으로 정의
}

export interface DuoCommunityDto {
    gameMode: string;
    positionSelf: string;
    positionFind: string;
    isUseMic: boolean;
}