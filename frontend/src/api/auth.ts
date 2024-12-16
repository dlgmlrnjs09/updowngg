import apiClient from './axios';
import type {AuthTokens, LoginCredentials, SignupCredentials, SignupResponse, SiteAccount} from '@/types/auth';

export const authApi = {
    signup: (credentials: SignupCredentials) =>
        apiClient.put<SignupResponse>('/auth/signup', credentials),
    login: (credentials: LoginCredentials) =>
        apiClient.post<AuthTokens>('/auth/login', credentials),
    logout: () =>
        apiClient.post<boolean>('/auth/logout'),
    getMemberInfo: () =>
        apiClient.get<SiteAccount>('/auth/member-info', {
        }),
};
