<!-- src/App.vue -->
<template>
  <component :is="currentHeader" />
  <RouterView />
  <Footer />

  <!-- 스크롤 TOP 버튼 -->
  <button
      v-show="showTopBtn"
      @click="scrollToTop"
      class="top-btn"
      aria-label="맨 위로 스크롤"
  >
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <polyline points="18 15 12 9 6 15"></polyline>
    </svg>
  </button>
</template>

<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router';
import { onMounted, computed, ref, onBeforeUnmount } from 'vue'
import Header from '@/components/common/Header.vue';
import { useAuthStore } from "@/stores/auth.ts";
import HeaderMain from "@/components/common/HeaderMain.vue";
import { useNotificationStore } from "@/stores/notification.ts";
import Footer from "@/components/common/Footer.vue";

const auth = useAuthStore()
const route = useRoute()
const notification = useNotificationStore()

// TOP 버튼 표시 여부를 위한 ref
const showTopBtn = ref(false)

// 스크롤 이벤트 핸들러
const handleScroll = () => {
  // 스크롤이 200px 이상 내려갔을 때 버튼 표시
  showTopBtn.value = window.scrollY > 200
}

// 맨 위로 스크롤하는 함수
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

// 현재 라우트에 따라 헤더 컴포넌트 결정
const currentHeader = computed(() => {
  // mainPageRoutes에 HeaderMain을 사용할 라우트들의 이름을 추가
  const mainPageRoutes = ['home', 'login', 'signup', 'socialLogin']
  return mainPageRoutes.includes(route.name as string) ? HeaderMain : Header
})

onMounted(async () => {
  await auth.initializeAuth();
  if (auth.isAuthenticated && auth.user) {
    notification.initSSE();
  }

  // 스크롤 이벤트 리스너 등록
  window.addEventListener('scroll', handleScroll)
});

onBeforeUnmount(() => {
  // 컴포넌트 언마운트 시 이벤트 리스너 제거
  window.removeEventListener('scroll', handleScroll)
})
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

/* TOP 버튼 스타일 */
.top-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 50px;
  height: 50px;
  background-color: rgba(50, 50, 50, 0.7);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
  transition: all 0.3s ease;
}

.top-btn:hover {
  background-color: rgba(70, 70, 70, 0.9);
  transform: translateY(-3px);
}

.top-btn svg {
  width: 24px;
  height: 24px;
}
</style>