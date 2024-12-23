import type {ReviewRequestDto, ReviewStatsDto} from "@/types/review.ts";

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
}

export interface LolMatchInfoRes {
    matchInfo: LolMatchInfo;
    participantList: LolMatchParticipant[];
}