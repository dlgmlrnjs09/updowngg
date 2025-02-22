import type {LolSummonerMostChampionDto} from "@/types/summoner.ts";
import type {ReviewStatsDto, ReviewTagDto} from "@/types/review.ts";
import type {SummonerBasicInfoDto} from "@/types/ranking.ts";

export interface CommunityPostDto {
    postId?: number;
    communityCode: string;
    title?: string;
    content: string;
    regDt?: string;
    updDt?: string;
    delDt?: string;
    duoCommunityEntity?: DuoCommunityDto; // 자식 엔티티는 선택 사항으로 정의
    partyCommunityDto?: PartyCommunityDto;
}

export interface DuoCommunityDto {
    gameMode: string;
    positionSelf: string;
    positionFind: string;
    isUseMic: boolean;
}

export interface DuoPostCardDto {
    postDto: CommunityPostDto;
    duoSummonerInfoDto: DuoSummonerInfoDto
}

export interface DuoSummonerInfoDto {
    summonerBasicInfoDto: SummonerBasicInfoDto
    frequentTagDtoList: ReviewTagDto[]
    reviewStatsDto: ReviewStatsDto
    mostChampionDto: LolSummonerMostChampionDto[]
}

export interface SearchFilter {
    gameMode?: string;
    tier?: string;
    positionSelf?: string;
    positionFind?: string;
    offset?: number;
    limit?: number;
}

export interface PartyCommunityDto {
    gameMode: string
    isUseMic: boolean
    positionSelf: string
    isOpenTop: boolean
    isOpenJungle: boolean
    isOpenMid: boolean
    isOpenAd: boolean
    isOpenSup: boolean
}

export interface PartyParticipantDto {
    summonerInfoDto: DuoSummonerInfoDto
    isOpenPosition: boolean
    position: string
}

export interface PartyPostCardDto {
    postId: number;
    writerSiteCode: number;
    writerPuuid: string;
    communityCode: string;
    title: string;
    content: string;
    gameMode: string;
    positionSelf: string;
    isUseMic: boolean;
    isOpenTop: boolean;
    isOpenJungle: boolean;
    isOpenMid: boolean;
    isOpenAd: boolean;
    isOpenSup: boolean;
    topPuuid: string | null;
    junglePuuid: string | null;
    midPuuid: string | null;
    adPuuid: string | null;
    supPuuid: string | null;
    participantDtoList: PartyParticipantDto[];
}

export interface PartyCommunityApplicantDto {
    postId: number;
    puuid: string;
    position: string;
    isApprove: string;
}

export interface PartyCommunityApplicantDetailDto {
    postId: number;
    applicantSeq: number;
    applicantPuuid: string;
    position: string;
    isApprove: boolean

    summonerInfoDto: DuoSummonerInfoDto
}

export interface MyPartyPostDto {
    postCardDto: PartyPostCardDto
    applicantByPositionMap: Record<string, PartyCommunityApplicantDetailDto[]>;
}

interface PartyCommunityHistoryBaseDto {
    postId: number;
    gameMode: string;
    content: string;
    isOpenTop: boolean;
    isOpenJungle: boolean;
    isOpenMid: boolean;
    isOpenAd: boolean;
    isOpenSup: boolean;
    topPuuid: string;
    junglePuuid: string;
    midPuuid: string;
    adPuuid: string;
    supPuuid: string;
    postStatus: string;
    regDt: string;

    recruitCount: number;
    participantCount: number;
    participantList: PartyParticipantDto[];
}

export interface PartyCommunityHistoryDto extends PartyCommunityHistoryBaseDto{
}

export interface PartyCommunityAppliedHistoryDto extends PartyCommunityHistoryBaseDto{
    applicantDto: PartyCommunityApplicantDto
}