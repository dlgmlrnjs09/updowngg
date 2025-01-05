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
      </nav>

      <div class="user-actions" v-if="authStore.isAuthenticated" @click="handleProfile">
        <div class="profile-btn">
          <div class="profile-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
          </div>
<!--          프로필-->
        </div>
        <button class="logout-btn" @click="authStore.logout">로그아웃</button>
      </div>
      <div class="user-actions" v-else>
        <div class="login-wrapper">
          <router-link to="/login" class="login-btn">로그인</router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import {RouterLink, useRouter} from 'vue-router';
import {useAuthStore} from "@/stores/auth.ts";
const authStore = useAuthStore();
const router = useRouter();

const handleProfile = async () => {
  await router.push({
    name: 'summoner',
    params: {
      name: authStore.riotUser?.riotAccountInfoEntity.gameName,
      tag: authStore.riotUser?.riotAccountInfoEntity.tagLine
    },
  })
}
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
  display: grid;
  grid-template-columns: 180px minmax(300px, auto) 200px;
  align-items: center;
  gap: 48px;
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
  justify-content: center;
}

.nav a {
  color: #888;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s;
}

.nav a:hover,
.nav a.router-link-active {
  color: white;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-self: end;
  min-width: 160px;
  justify-content: flex-end;
}

.login-wrapper {
  min-width: 160px;
  display: flex;
  justify-content: center;
}

.logout-btn,
.login-btn,
.profile-btn {
  height: 36px;
  color: #888;
  background: none;
  border: 1.5px solid #444;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  padding: 0 16px;
  border-radius: 8px;
  font-weight: 500;
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logout-btn:hover,
.login-btn:hover,
.profile-btn:hover {
  color: white;
  border-color: #666;
}

.profile-btn {
  gap: 8px;
}

.profile-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 0;
}

@media (max-width: 768px) {
  .header-content {
    gap: 24px;
  }

  .nav {
    display: none;
  }

  .user-actions {
    gap: 8px;
    min-width: auto;
  }

  .logout-btn,
  .login-btn,
  .profile-btn {
    padding: 0 12px;
    font-size: 13px;
  }

  .profile-icon svg {
    width: 16px;
    height: 16px;
  }
}
</style>