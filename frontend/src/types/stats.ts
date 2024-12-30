import type {MatchGameMode} from "@/types/match.ts";
import type {Tier} from "@/types/league.ts";
import {MatchPosition} from "@/types/match.ts";
import type {ReviewTagDto} from "@/types/review.ts";

export interface ChampionResDto {
    nameUs: string;
    nameKr: string;
    iconUrl: string;
    playCount: number;
    reviewCount: number;
    upRatio: number;
    downRatio: number;
    topTagList: ReviewTagDto[];
}

export interface SearchFilter {
    queueType?: MatchGameMode;
    tier?: Tier;
    position?: MatchPosition;
    period?: string;
}