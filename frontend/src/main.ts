import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import Toast, {useToast} from "vue-toastification"
import "vue-toastification/dist/index.css"
import VueGtag from 'vue-gtag-next'

import './assets/main.css'
import {createPinia} from "pinia"

const app = createApp(App)
const pinia = createPinia()

const isMobile = window.innerWidth < 768;
// Toast 먼저 설정
app.use(Toast, {
    position: isMobile ? "top-center" : "bottom-right",
    timeout: 3000,
    closeOnClick: true,
    pauseOnHover: true
})

// 전역 에러 핸들러
app.config.errorHandler = (err: any, vm, info) => {
    // 콘솔에는 항상 에러 출력
    console.error('Global error:', err)

    // 개발 환경에서만 toast 메시지 표시
    if (!import.meta.env.PROD) {
        const toast = useToast();
        // 에러 객체 대신 에러 메시지만 전달
        if (err.response) {
            // Axios 에러인 경우
            toast.error(err.response.data?.message || err.message || '에러가 발생했습니다.')
        } else {
            // 일반 에러인 경우
            toast.error(err.message || '에러가 발생했습니다.')
        }
    }

    return true
}

import { throttleClick } from '@/plugin/throttleClick'
if (import.meta.env.PROD) {
    app.use(VueGtag, {
        property: { id: 'G-DVK79DXH8T' }, router
    })
}

app.use(throttleClick)
app.use(pinia)
app.use(router)
app.mount('#app')