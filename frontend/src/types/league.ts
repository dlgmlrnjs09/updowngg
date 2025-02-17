export enum Tier {
    CHALLENGER = 'CHALLENGER',
    GRANDMASTER = 'GRANDMASTER',
    MASTER = 'MASTER',
    DIAMOND = 'DIAMOND',
    EMERALD = 'EMERALD',
    PLATINUM = 'PLATINUM',
    GOLD = 'GOLD',
    SILVER = 'SILVER',
    BRONZE = 'BRONZE',
    IRON = 'IRON'
}

export interface TierInfo {
    nameEn: string;
    nameKr: string;
}

export const TierDetails: Record<Tier, TierInfo> = {
    [Tier.CHALLENGER]: { nameEn: 'CHALLENGER', nameKr: '챌린저' },
    [Tier.GRANDMASTER]: { nameEn: 'GRANDMASTER', nameKr: '그랜드마스터' },
    [Tier.MASTER]: { nameEn: 'MASTER', nameKr: '마스터' },
    [Tier.DIAMOND]: { nameEn: 'DIAMOND', nameKr: '다이아몬드' },
    [Tier.EMERALD]: { nameEn: 'EMERALD', nameKr: '에메랄드' },
    [Tier.PLATINUM]: { nameEn: 'PLATINUM', nameKr: '플래티넘' },
    [Tier.GOLD]: { nameEn: 'GOLD', nameKr: '골드' },
    [Tier.SILVER]: { nameEn: 'SILVER', nameKr: '실버' },
    [Tier.BRONZE]: { nameEn: 'BRONZE', nameKr: '브론즈' },
    [Tier.IRON]: { nameEn: 'IRON', nameKr: '아이언' }
};

export interface LolSummonerLeagueEntity {
    /** LOL 리그 고유 ID */
    leagueId: string;

    /** Riot 소환사 고유 ID */
    summonerId: string;

    /** 큐 타입 */
    queueType: string;

    /** 랭크 티어 */
    tier: string;

    /** 랭크 단계 */
    rank: string;

    /** 랭크 점수 */
    leaguePoints: number;

    /** 승리 수 */
    wins: number;

    /** 패배 수 */
    losses: number;

    /** 연승 여부 */
    hotStreak: boolean;

    /** 베테랑 여부(해당 티어에서 100게임 이상 플레이) */
    veteran: boolean;

    /** 신규 진입 여부 - 해당 티어 신규 진입 */
    freshBlood: boolean;

    /** 휴면 계정 여부 */
    inactive: boolean;

    /** 등록일 */
    regDt: string;

    /** 수정일 */
    updDt: string;
}

export interface LolSummonerLeagueDto {
    leagueId: string;
    summonerId: string;
    queueType: string;
    tier: string;
    rank: string;
    leaguePoints: number;
    wins: number;
    losses: number;
    hotStreak: boolean;
    veteran: boolean;
    freshBlood: boolean;
    inactive: boolean;
    reviewDto: LolSummonerLeagueReviewDto;
    regDt: string;
    updDt: string;
}

interface LolSummonerLeagueReviewDto {
    upCount: number;
    downCount: number;
}