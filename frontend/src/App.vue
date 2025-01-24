<!-- src/App.vue -->
<template>
  <component :is="currentHeader" />
  <RouterView />
</template>

<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router';
import {onMounted, computed} from 'vue'
import Header from '@/components/common/Header.vue';
import {useAuthStore} from "@/stores/auth.ts";
import HeaderMain from "@/components/common/HeaderMain.vue";
import {useNotificationStore} from "@/stores/notification.ts";

const auth = useAuthStore()
const route = useRoute()
const notification = useNotificationStore()

// 현재 라우트에 따라 헤더 컴포넌트 결정
const currentHeader = computed(() => {
  // mainPageRoutes에 HeaderMain을 사용할 라우트들의 이름을 추가
  const mainPageRoutes = ['home', 'login', 'signup']
  return mainPageRoutes.includes(route.name as string) ? HeaderMain : Header
})

onMounted(async () => {
  await auth.initializeAuth();
  if (auth.isAuthenticated && auth.user) {
    notification.initSSE();
  }
});
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Pretendard', sans-serif;
}

body {
  background: #0A0A0A;
  color: white;
  line-height: 1.5;
}

.app {
  min-height: 100vh;
  background: #0A0A0A;
  display: flex;
  flex-direction: column; /* dp 수정10 */
}
</style>