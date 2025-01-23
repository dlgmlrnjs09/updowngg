export interface SiteAccount {
    memberSiteCode: number;
    puuid: string;
    memberEmail: string;
}

export interface LoginCredentials {
    email: string;
    password: string;
}

export interface SignupCredentials {
    email: string;
    password: string;
    passwordConfirm: string;
}

export interface AuthTokens {
    accessToken: string;
    refreshToken: string;
}

export interface RefreshTokenRequest {
    refreshToken: string;
}

export interface SignupResponse {
    success: boolean;
    message: string;
}

export interface DiscordAccount {
    memberSiteCode: number;
    discordCode: string | null;
    username: string | null;
    globalName: string | null;
    avatar: string | null;
    banner: string | null;
    bannerColor: string | null;
    accentColor: string | null;
    avatarDecorationData: string | null;
    email: string | null;
    verified: boolean | null;
    mfaEnabled: boolean | null;
    publicFlags: boolean | null;
    flags: boolean | null;
    locale: string | null;
    clan: string | null;
    primaryGuild: string | null;
    regDt: string | null;
    updDt: string | null;
    delDt: string | null;
}