import apiClient from './axios';
import type {
    AuthTokens,
    DiscordAccount,
    LoginCredentials,
    SignupCredentials,
    SignupResponse,
    SiteAccount
} from '@/types/auth';

export const authApi = {
    signup: (credentials: SignupCredentials) =>
        apiClient.put<SignupResponse>('/api/v1/auth/signup', credentials),
    login: (credentials: LoginCredentials) =>
        apiClient.post<AuthTokens>('/api/v1/auth/login', credentials),
    logout: () =>
        apiClient.post<boolean>('/api/v1/auth/logout'),
    getMemberInfo: () =>
        apiClient.get<SiteAccount>('/api/v1/auth/member-info', {
        }),
    connectDiscord: () =>
        apiClient.get<DiscordAccount>('/api/v1/auth/discord/connect', {
        }),
    disconnectDiscord: () =>
        apiClient.post<void>('/api/v1/auth/discord/disconnect', {
        }),
    checkDiscord: () =>
        apiClient.get<DiscordAccount>('/api/v1/auth/discord/account', {
        }),
    loginDiscord: () =>
        apiClient.get<AuthTokens>('/api/v1/auth/discord/login'),
};
