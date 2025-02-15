import type {ReviewByMatchSummaryDto, ReviewRequestDto, ReviewStatsDto, ReviewTagDto} from "@/types/review.ts";
import type {SummonerBasicInfoDto} from "@/types/ranking.ts";

export interface LolMatchInfo {
    matchId: string;
    gameDuration: number;
    gameMode: string;
    gameModeName: string;
    gameStartDt: string;
    gameType: string;
}

export interface LolMatchParticipant {
    matchId: string;
    puuid: string;
    riotIdGameName: string;
    riotIdTagline: string;
    position: string;
    teamId: number;
    teamName: string;
    kills: number;
    assists: number;
    deaths: number;
    champLevel: number;
    champId: number;
    champName: string;
    champProfileIconUrl: string;
    totalDamageToChampion: number;
    totalDamageTaken: number;
    // reviewable: boolean;
    isReviewable: boolean;
    reviewDto: ReviewRequestDto
    reviewStatsDto: ReviewStatsDto
    isWin: boolean
}

export interface LolMatchInfoRes {
    matchInfo: LolMatchInfo;
    participantList: LolMatchParticipant[];
    reviewByMatchSummaryDto: ReviewByMatchSummaryDto
}


export enum MatchGameMode {
    ALL = "ALL",
    ARAM = 'ARAM',
    NORMAL = 'NORMAL',
    SOLO_RANK = 'SOLO_RANK',
    FLEX_RANK = 'FLEX_RANK'
}

export interface GameModeInfo {
    queueId: number;
    queueName: string;
}

interface CurrentMatchPlayerDto {
    puuid: string;
    championId: number;
    championIconUrl: string;
    teamId: number;
    leagueDto: LolMatchPlayerLeagueDto
}

interface LolMatchPlayerLeagueDto {
    tier: string;
    acronymTier: string;
    tierColor: string;
    rank: string;
}

export interface CurrentMatchParticipantDto {
    playerDto: CurrentMatchPlayerDto;
    summonerInfoDto: SummonerBasicInfoDto;
    frequentTagDtoList?: ReviewTagDto[];
    reviewStatsDto?: ReviewStatsDto;
}

export interface CurrentMatchInfoDto {
    matchInfoDto: LolMatchInfo;
    participantDtoList: CurrentMatchParticipantDto[];
}

export const MatchGameModeDetails: Record<MatchGameMode, GameModeInfo> = {
    [MatchGameMode.ALL]: {queueId: 0, queueName: '전체'},
    [MatchGameMode.ARAM]: { queueId: 450, queueName: '칼바람나락' },
    [MatchGameMode.NORMAL]: { queueId: 430, queueName: '일반게임' },
    [MatchGameMode.SOLO_RANK]: { queueId: 420, queueName: '개인랭크' },
    [MatchGameMode.FLEX_RANK]: { queueId: 440, queueName: '자유랭크' }
};

export const MatchGameModeUtils = {
    getQueueName(queueId: number): string | null {
        const mode = Object.values(MatchGameModeDetails).find(
            mode => mode.queueId === queueId
        );
        return mode?.queueName ?? null;
    }
};

export enum MatchPosition {
    TOP = 'TOP',
    JUNGLE = 'JUNGLE',
    MIDDLE = 'MIDDLE',
    BOTTOM = 'BOTTOM',
    UTILITY = 'UTILITY'
}

export const MatchPositionDetails: Record<MatchPosition, { nameKr: string }> = {
    [MatchPosition.TOP]: { nameKr: '탑' },
    [MatchPosition.JUNGLE]: { nameKr: '정글' },
    [MatchPosition.MIDDLE]: { nameKr: '미드' },
    [MatchPosition.BOTTOM]: { nameKr: '원딜' },
    [MatchPosition.UTILITY]: { nameKr: '서폿' }
};

export enum Period {
    ALL = 'ALL',
    MONTH = 'MONTH',
    WEEK = 'WEEK',
    TODAY = 'TODAY'
}