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