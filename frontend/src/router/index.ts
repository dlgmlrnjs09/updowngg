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
        // {
        //     path: '/ranking',
        //     name: 'ranking',
        //     component: () => import('@/views/RankingPage.vue')
        // },
        // {
        //     path: '/stats',
        //     name: 'stats',
        //     component: () => import('@/views/StatsPage.vue')
        // },
        // {
        //     path: '/faq',
        //     name: 'faq',
        //     component: () => import('@/views/FaqPage.vue')
        // }
    ]
});

export default router;