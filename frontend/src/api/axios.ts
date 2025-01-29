// src/api/axios.ts
import axios from 'axios'
import router from "@/router";
import {useAuthStore} from "@/stores/auth.ts";
import Toast, {useToast} from "vue-toastification";
import type {CommonErrorResponse} from "@/types/error.ts";

const apiClient = axios.create({
    baseURL: (window as any).__API_URL__ || import.meta.env.VITE_API_URL,
    timeout: 100000,
    withCredentials: true
})

const toast = useToast();

// JWT 디코딩 및 만료 체크 함수들
function parseJwt(token: string) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (error) {
        return null;
    }
}

function isTokenExpired(token: string): boolean {
    const decodedToken = parseJwt(token);
    if (!decodedToken) return true;

    // exp는 초 단위로 저장되어 있으므로 1000을 곱해 밀리초로 변환
    const expirationTime = decodedToken.exp * 1000;
    return Date.now() >= expirationTime;
}

// 인증 불필요
const publicPaths = ['/api/v1/auth/login', '/api/v1/auth/signup', '/api/v1/auth/refresh'];
apiClient.interceptors.request.use(
    async (config) => {
        const isPublicPath = publicPaths.some(path => config.url?.includes(path));
        const token = localStorage.getItem('accessToken');

        if (!isPublicPath && token) {
            // 토큰이 만료되었는지 확인
            if (isTokenExpired(token)) {
                // 토큰이 만료된 경우, 리프레시 시도
                try {
                    const refreshToken = localStorage.getItem('refreshToken');
                    if (!refreshToken) throw new Error('No refresh token available');

                    const response = await apiClient.post('/api/v1/auth/refresh', {
                        refreshToken: refreshToken
                    });

                    const newAccessToken = response.data.accessToken;
                    const authStore = useAuthStore();
                    authStore.initializeAuth();
                    localStorage.setItem('accessToken', newAccessToken);
                    config.headers.Authorization = `Bearer ${newAccessToken}`;

                    console.log('Token Refresh! new Token : ', newAccessToken);

                } catch (error) {
                    // 리프레시 실패 시 로그아웃
                    localStorage.removeItem('accessToken');
                    localStorage.removeItem('refreshToken');
                    router.push('/login');
                    return Promise.reject(error);
                }
            } else {
                // 토큰이 유효한 경우 그대로 사용
                config.headers.Authorization = `Bearer ${token}`;
            }
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

/**
 * 응답 인터셉터를 설정
 * 모든 HTTP 응답을 처리하기 전에 실행
 * - 401 에러(인증 실패) 발생 시 토큰 갱신 시도
 * - 토큰 갱신 성공 시 원래 요청 재시도
 * - 토큰 갱신 실패 시 로그인 페이지로 리다이렉트
 */
apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        if (error.response?.status === 401) {
            const tokenStatus = error.response.headers['token_status'];
            const authStore = useAuthStore();

            switch(tokenStatus) {
                case 'EXPIRED':
                    // 토큰 갱신 시도
                    try {
                        const refreshToken = localStorage.getItem('refreshToken');
                        if (!refreshToken) {
                            throw new Error('No refresh token');
                        }

                        const response = await apiClient.post('/api/v1/auth/refresh', {refreshToken});

                        localStorage.setItem('accessToken', response.data.accessToken);

                        // 원래 요청 재시도
                        const originalRequest = error.config;
                        originalRequest.headers.Authorization = `Bearer ${response.data.accessToken}`;
                        return apiClient(originalRequest);
                    } catch (refreshError) {
                        // 갱신 실패 시
                        localStorage.removeItem('accessToken');
                        localStorage.removeItem('refreshToken');
                        authStore.isAuthenticated = false;
                        router.push('/login');
                        toast.error('인증이 만료되었습니다. 다시 로그인해주세요.');
                    }
                    break;

                case 'BLACKLISTED':
                    // 로그아웃 처리
                    await authStore.logout();
                    router.push('/login');
                    toast.error('유효하지 않은 인증입니다. 다시 로그인해주세요.');
                    break;

                default:
                    // 일반적인 인증 필요 상황
                    if (!error.config.url?.includes('/auth/')) {
                        // auth 관련 요청이 아닐 경우에만 알림
                        toast.error('로그인이 필요한 서비스입니다.');
                    }
            }
        }
        return Promise.reject(error);
    }
);

export default apiClient;