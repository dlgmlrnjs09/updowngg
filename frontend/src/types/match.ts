export interface LolMatchInfo {
    matchId: string;
    gameDuration: number;
    gameMode: string;
    gameStartDt: string;
    gameType: string;
}

export interface LolMatchParticipant {
    matchId: string;
    puuid: string;
    riotIdGameName: string;
    riotIdTagline: string;
    role: string;
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
    reviewable: boolean;
}

export interface LolMatchInfoRes {
    matchInfo: LolMatchInfo;
    participantList: LolMatchParticipant[];
}