// src/main.ts
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
import VueGtag from 'vue-gtag-next'

import './assets/main.css'
import {createPinia} from "pinia";

const app = createApp(App)
const pinia = createPinia()

// 전역 에러 핸들러
app.config.errorHandler = (err, vm, info) => {
    console.error('Global error:', err)
    return true
}

import { throttleClick } from '@/plugin/throttleClick'
if (import.meta.env.PROD) {
    app.use(VueGtag, {
        property: { id: 'G-DVK79DXH8T' }, router
    });
}
app.use(pinia)
app.use(router)
app.mount('#app')
app.use(throttleClick)
app.use(Toast, {
    // 옵션 설정
    position: "bottom-right",
    timeout: 3000,
    closeOnClick: true,
    pauseOnHover: true
})