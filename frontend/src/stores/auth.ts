// src/stores/auth.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Ref } from 'vue';
import { authApi } from '@/api/auth';
import type { SiteAccount, LoginCredentials } from '@/types/auth';
import Toast, {useToast} from "vue-toastification";

const toast = useToast()

export const useAuthStore = defineStore('auth', () => {
    const user: Ref<SiteAccount | null> = ref(null);
    const isAuthenticated: Ref<boolean> = ref(false);

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
    }

    async function fetchUserInfo(): Promise<void> {
        try {
            const response = await authApi.getMemberInfo();
            user.value = response.data;
        } catch (error) {
            console.error('Failed to fetch user info:', error);
        }
    }

    function initializeAuth(): void {
        const token = localStorage.getItem('accessToken');
        if (token) {
            isAuthenticated.value = true;
            fetchUserInfo();
        }
    }

    return {
        user,
        isAuthenticated,
        login,
        logout,
        fetchUserInfo,
        initializeAuth
    };
});