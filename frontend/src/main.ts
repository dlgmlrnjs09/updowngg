// src/main.ts
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";

import './assets/main.css'
import {createPinia} from "pinia";

const app = createApp(App)
const pinia = createPinia()
import { throttleClick } from '@/plugin/throttleClick'

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