<template>
  <header class="header">
    <div class="header-content">
      <RouterLink to="/" class="logo">
        <div class="logo-icon">up</div>
        updownGG
      </RouterLink>
      <nav class="nav">
        <RouterLink to="/">홈</RouterLink>
        <RouterLink to="/ranking">랭킹</RouterLink>
        <RouterLink to="/stats">통계</RouterLink>
        <RouterLink to="/faq">FAQ</RouterLink>
      </nav>

      <div v-if="authStore.isAuthenticated">
        <button @click="authStore.logout">로그아웃</button>
      </div>
      <div v-else>
        <router-link to="/login">로그인</router-link>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router';
import {useAuthStore} from "@/stores/auth.ts";
const authStore = useAuthStore();

const handleLogin = () => {
  // 로그인 처리 로직
  console.log('로그인 클릭');
};
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #0A0A0A;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-weight: 700;
  font-size: 24px;
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.logo-icon {
  width: 28px;
  height: 28px;
  background: #2979FF;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  font-weight: 700;
}

.nav {
  display: flex;
  gap: 32px;
}

.nav a {
  color: #888;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s;
}

.nav a:hover, .nav a.router-link-active {
  color: white;
}

.login-btn {
  color: #888;
  background: none;
  border: 1.5px solid #444;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 500;
}

.login-btn:hover {
  color: white;
  border-color: #666;
}

@media (max-width: 768px) {
  .nav {
    display: none;
  }
}
</style>