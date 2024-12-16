// src/api/axios.ts
import axios from 'axios'
import router from "@/router";

const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 100000
})

// 인증 불필요
const publicPaths = ['/auth/login', '/auth/signup', '/auth/refresh'];

// Request Interceptor
apiClient.interceptors.request.use(
    (config) => {
        // URL이 publicPaths에 포함되어 있지 않은 경우에만 토큰 추가
        const isPublicPath = publicPaths.some(path => config.url?.includes(path));
        console.log('Request URL:', config.url);  // 추가
        console.log('Is public path:', isPublicPath);  // 추가

        if (!isPublicPath) {
            const token = localStorage.getItem('accessToken');
            console.log('Token exists:', JSON.stringify(token));  // 추가
            if (token) {
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

        return Promise.reject(error);
    }
);

export default apiClient;