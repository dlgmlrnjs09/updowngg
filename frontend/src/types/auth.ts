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