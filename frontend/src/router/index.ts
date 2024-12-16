// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/MainPage.vue';

const router = createRouter({
    history: createWebHistory(/*import.meta.env.BASE_URL*/),
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
        }
    ]
});

/**
 * 네비게이션 가드 설정
 */
router.beforeEach((to, from, next) => {
    const auth = localStorage.getItem('accessToken');
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth ?? false);

    if (requiresAuth && !auth) {
        next('/login');
    } else if (auth && to.path === '/login') {
        next('/');
    } else {
        next();
    }
});

export default router;