// src/stores/auth.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Ref } from 'vue';
import { authApi } from '@/api/auth';
import type { SiteAccount, LoginCredentials } from '@/types/auth';
import { useToast } from "vue-toastification";

export const useAuthStore = defineStore('auth', () => {
    const user: Ref<SiteAccount | null> = ref(null);
    const isAuthenticated: Ref<boolean> = ref(false);
    const isInitialized: Ref<boolean> = ref(false);  // 초기화 상태 추가
    const toast = useToast();

    async function login(credentials: LoginCredentials): Promise<boolean> {
        try {
            const response = await authApi.login(credentials);
            const { accessToken, refreshToken } = response.data;

            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);

            isAuthenticated.value = true;
            await fetchUserInfo();

            return true;
        } catch (error) {
            console.error('Login failed:', error);
            return false;
        }
    }

    async function logout(): Promise<void> {
        try {
            const response = await authApi.logout();
            if (response.data) {
                toast.success('안녕히가세요, ' + user.value?.memberEmail + '님!')
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                user.value = null;
                isAuthenticated.value = false;
            } else {
                toast.error('로그아웃에 실패했습니다.')
            }
        } catch (error) {
            console.error('Logout failed:', error);
            toast.error('로그아웃에 실패했습니다.')
        }
    }

    async function fetchUserInfo(): Promise<void> {
        try {
            const response = await authApi.getMemberInfo();
            user.value = response.data;
        } catch (error) {
            console.error('Failed to fetch user info:', error);
            // 토큰이 만료되었거나 유효하지 않은 경우
            isAuthenticated.value = false;
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
        }
    }

    async function initializeAuth(): Promise<void> {
        if (isInitialized.value) return;  // 이미 초기화된 경우 스킵

        const token = localStorage.getItem('accessToken');
        if (token) {
            isAuthenticated.value = true;
            await fetchUserInfo();
        }

        isInitialized.value = true;
    }

    return {
        user,
        isAuthenticated,
        isInitialized,
        login,
        logout,
        fetchUserInfo,
        initializeAuth
    };
});