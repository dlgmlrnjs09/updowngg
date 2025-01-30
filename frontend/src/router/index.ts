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
        }
    ],
    scrollBehavior(to, from, savedPosition) {
        // 항상 페이지 상단으로 스크롤
        return { top: 0 }
    }
});

/**
 * 네비게이션 가드 설정
 */
router.beforeEach((to, from, next) => {
    // const auth = localStorage.getItem('accessToken');
    const authStore = useAuthStore();
    const auth = authStore.isAuthenticated;
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth ?? false);

    if (requiresAuth && !auth) {
        console.log('requiresAuth && !auth')
        next('/login');
    } else if (auth && to.path === '/login') {
        console.log('auth && to.path')
        next('/');
    } else {
        next();
    }
});

// 페이지 뷰 추적을 위한 네비게이션 가드
router.afterEach((to) => {
    const title = to.meta.title
    if (title) {
        document.title = String(title)
    }
})

export default router;