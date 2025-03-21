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

    allInPings: number;
    assistMePings: number;
    baronKills: number;
    bountyLevel: number;
    champExperience: number;
    commandPings: number;
    championTransform: number;
    consumablesPurchased: number;
    damageDealtToBuildings: number;
    damageDealtToObjectives: number;
    damageDealtToTurrets: number;
    damageSelfMitigated: number;
    detectorWardsPlaced: number;
    doubleKills: number;
    dragonKills: number;
    eligibleForProgression: boolean;
    enemyMissingPings: number;
    enemyVisionPings: number;
    firstBloodAssist: boolean;
    firstBloodKill: boolean;
    firstTowerAssist: boolean;
    firstTowerKill: boolean;
    gameEndedInEarlySurrender: boolean;
    gameEndedInSurrender: boolean;
    holdPings: number;
    getBackPings: number;
    goldEarned: number;
    goldSpent: number;
    individualPosition: string;
    inhibitorKills: number;
    inhibitorTakedowns: number;
    inhibitorsLost: number;
    item0: number;
    item1: number;
    item2: number;
    item3: number;
    item4: number;
    item5: number;
    item6: number;
    itemsPurchased: number;
    killingSprees: number;
    lane: string;
    largestCriticalStrike: number;
    largestKillingSpree: number;
    largestMultiKill: number;
    longestTimeSpentLiving: number;
    magicDamageDealt: number;
    magicDamageDealtToChampions: number;
    magicDamageTaken: number;
    neutralMinionsKilled: number;
    needVisionPings: number;
    nexusKills: number;
    nexusTakedowns: number;
    nexusLost: number;
    objectivesStolen: number;
    objectivesStolenAssists: number;
    onMyWayPings: number;
    pentaKills: number;
    physicalDamageDealt: number;
    physicalDamageDealtToChampions: number;
    physicalDamageTaken: number;
    placement: number;
    playerAugment1: number;
    playerAugment2: number;
    playerAugment3: number;
    playerAugment4: number;
    playerAugmentEntity1: ArenaAugmentEntity;
    playerAugmentEntity2: ArenaAugmentEntity;
    playerAugmentEntity3: ArenaAugmentEntity;
    playerAugmentEntity4: ArenaAugmentEntity;
    playerSubteamId: number;
    pushPings: number;
    profileIcon: number;
    quadraKills: number;
    role: string;
    sightWardsBoughtInGame: number;
    spell1Casts: number;
    spell2Casts: number;
    spell3Casts: number;
    spell4Casts: number;
    subteamPlacement: number;
    summoner1Casts: number;
    summoner1Id: number;
    summoner2Casts: number;
    summoner2Id: number;
    teamEarlySurrendered: boolean;
    teamPosition: string;
    timeCCingOthers: number;
    totalAllyJungleMinionsKilled: number;
    totalDamageDealt: number;
    totalDamageShieldedOnTeammates: number;
    totalEnemyJungleMinionsKilled: number;
    totalHeal: number;
    totalHealsOnTeammates: number;
    totalMinionsKilled: number;
    totalTimeCCDealt: number;
    totalTimeSpentDead: number;
    totalUnitsHealed: number;
    tripleKills: number;
    trueDamageDealt: number;
    trueDamageDealtToChampions: number;
    trueDamageTaken: number;
    turretKills: number;
    turretTakedowns: number;
    turretsLost: number;
    unrealKills: number;
    visionScore: number;
    visionClearedPings: number;
    visionWardsBoughtInGame: number;
    wardsKilled: number;
    wardsPlaced: number;
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
    FLEX_RANK = 'FLEX_RANK',
    CHERRY = 'CHERRY'
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

interface ArenaAugmentEntity {
    id: number;
    apiName: string;
    name: string;
    description: string;
    tooltip: string;
    rarityId: number;
    iconLarge: string;
    iconSmall: string;
    iconLargeUrl: string;
    iconSmallUrl: string;
    createdAt: string;
    updatedAt: string;
}

export const MatchGameModeDetails: Record<MatchGameMode, GameModeInfo> = {
    [MatchGameMode.ALL]: {queueId: 0, queueName: '전체'},
    [MatchGameMode.ARAM]: { queueId: 450, queueName: '칼바람나락' },
    [MatchGameMode.NORMAL]: { queueId: 430, queueName: '일반게임' },
    [MatchGameMode.SOLO_RANK]: { queueId: 420, queueName: '개인랭크' },
    [MatchGameMode.FLEX_RANK]: { queueId: 440, queueName: '자유랭크' },
    [MatchGameMode.CHERRY]: { queueId: 1700, queueName: '아레나' }
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