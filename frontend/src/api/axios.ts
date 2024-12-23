// src/api/axios.ts
import axios from 'axios'
import router from "@/router";
import {useAuthStore} from "@/stores/auth.ts";
import Toast, {useToast} from "vue-toastification";
import type {CommonErrorResponse} from "@/types/error.ts";

const apiClient = axios.create({
    baseURL: (window as any).__API_URL__ || import.meta.env.VITE_API_URL,
    timeout: 100000
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
const publicPaths = ['/auth/login', '/auth/signup', '/auth/refresh'];
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

                    const response = await apiClient.post('/auth/refresh', {
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
        const originalRequest = error.config;
        const authStore = useAuthStore();

        // 401 에러이고 아직 재시도하지 않은 경우에만 처리
        if (error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const refreshToken = localStorage.getItem('refreshToken');
                if (!refreshToken) {
                    throw new Error('No refresh token available');
                }

                // 리프레시 토큰으로 새로운 액세스 토큰 요청
                const response = await apiClient.post('/auth/refresh', {
                    refreshToken: refreshToken
                });

                const newAccessToken = response.data.accessToken;
                localStorage.setItem('accessToken', newAccessToken);
                authStore.initializeAuth();

                // 새로운 토큰으로 원래 요청 재시도
                originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                return apiClient(originalRequest);
            } catch (refreshError) {
                // 리프레시 토큰도 만료된 경우 로그아웃 처리
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                router.push('/login')
                return Promise.reject(refreshError);
            }
        }

        const errorResponse = error.response?.data as CommonErrorResponse;

        if (errorResponse.userMessage) {
            toast.error(errorResponse.userMessage);
        }

        // 개발 환경에서는 개발자 메시지도 콘솔에 출력
        if (process.env.NODE_ENV === 'development') {
            console.error('API Error:', {
                status: error.response?.status,
                devMessage: errorResponse?.devMessage,
                userMessage: errorResponse?.userMessage,
                timestamp: errorResponse?.timestamp
            });
        }

        return Promise.reject(error);
    }
);

export default apiClient;