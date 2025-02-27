// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/MainPage.vue';
import {useAuthStore} from "@/stores/auth.ts";
import {authApi} from "@/api/auth.ts";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomePage
        },
        {
            path: '/summoner/:name/:tag',
            name: 'summoner',
            component: () => import('@/views/SummonerMainPage.vue')
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/LoginPage.vue')
        },
        {
            path: '/login/social',
            name: 'socialLogin',
            component: () => import('@/views/SocialLoginPage.vue')
        },
        {
            path: '/signup',
            name: 'signup',
            component: () => import('@/views/SignupPage.vue')
        },
        {
            path: '/ranking',
            name: 'ranking',
            component: () => import('@/views/SummonerRakingPage.vue')
        },
        {
            path: '/stats',
            name: 'stats',
            component: () => import('@/views/StatsPage.vue')
        },
        {
            path: '/setting/account',
            name: 'settingAccount',
            component: () => import('@/views/AccountSettings.vue')
        },
        {
            path: '/review/history',
            name: 'reviewHistory',
            component: () => import('@/views/ReviewHistoryPage.vue')
        },
        {
            path: '/community/duo',
            name: 'duoCommunity',
            component: () => import('@/views/Community.vue')
        },
        {
            path: '/community/party',
            name: 'partyCommunity',
            component: () => import('@/views/PartyCommunity.vue')
        },
        {
            path: '/community/history',
            name: 'communityHistory',
            component: () => import('@/views/CommunityHistory.vue')
        },
        {
            path: '/privacy',
            name: 'privacy',
            component: () => import('@/views/PrivacyPolicy.vue')
        },
        {
            path: '/terms',
            name: 'terms',
            component: () => import('@/views/TermsOfService.vue')
        },
        {
            path: '/oauth2/discord/callback',
            name: 'discordCallback',
            component: () => import('@/views/AccountSettings.vue'),
            beforeEnter: async (to, from, next) => {
                try {
                    const response = (await authApi.connectDiscord()).data;
                    next('/setting/account');
                } catch (error) {
                    console.error('Discord callback error:', error);
                    next('/setting/account');
                }
            }
        },
        {
            path: '/auth/success',
            name: 'riotAuthSuccess',
            component: () => import('@/views/AuthSuccess.vue')
        }
    ],
    scrollBehavior(to, from, savedPosition) {
        // 항상 페이지 상단으로 스크롤
        return { top: 0 }
    }
});

// 라우터 에러 핸들러
router.onError((error, to, from) => {
    console.error('Navigation error:', error)
    // 개발/운영 환경 모두 강제 이동
    window.location.href = to.fullPath
})

/**
 * 네비게이션 가드 설정
 */
router.beforeEach(async (to, from, next) => {
    try {
        const authStore = useAuthStore();
        const auth = authStore.isAuthenticated;
        const requiresAuth = to.matched.some(record => record.meta.requiresAuth ?? false);

        if (requiresAuth && !auth) {
            next('/login');
        } else if (auth && to.path === '/login') {
            next('/');
        } else {
            next();
        }
    } catch (error) {
        console.error('Navigation guard error:', error)
        next(true) // 개발/운영 모두 네비게이션 계속 진행
    }
});

// 페이지 뷰 추적을 위한 네비게이션 가드
router.afterEach((to) => {
    try {
        const title = to.meta.title
        if (title) {
            document.title = String(title)
        }
    } catch (error) {
        console.error('After navigation error:', error)
        // afterEach는 네비게이션에 영향을 주지 않으므로 에러만 로깅
    }
})

export default router;