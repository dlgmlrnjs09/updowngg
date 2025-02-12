<template>
  <header class="header">
    <div class="header-content">
      <RouterLink to="/" class="logo">
        <div class="logo-icon"><img src="/favicon.ico" alt="favicon"></div>
        <span>updownGG</span>
      </RouterLink>
      <nav class="nav">
        <RouterLink to="/">홈</RouterLink>
        <RouterLink to="/ranking">랭킹</RouterLink>
        <RouterLink to="/stats">통계</RouterLink>
        <RouterLink to="/community/duo">커뮤니티</RouterLink>
      </nav>

      <div class="user-actions" v-if="authStore.isAuthenticated">
        <Notification/>
        <div class="profile-dropdown" ref="profileDropdown">
          <button class="profile-btn" @click="toggleDropdown">
            <div class="profile-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </div>
          </button>

          <div v-if="isDropdownOpen" class="dropdown-menu">
            <button class="dropdown-item" @click="handleProfile">프로필</button>
            <button class="dropdown-item" @click="navigateToAccountSettings">계정 설정</button>
            <button class="dropdown-item" @click="navigateToReviewHistory">평가 내역</button>
            <div class="dropdown-divider"></div>
            <button class="dropdown-item logout" @click="handleLogout">로그아웃</button>
          </div>
        </div>
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
import {onMounted, onUnmounted, ref, watch} from "vue";
import Notification from "@/components/common/Notification.vue";
const authStore = useAuthStore();
const router = useRouter();
const isDropdownOpen = ref(false);
const profileDropdown = ref<HTMLElement | null>(null);
import { useDropdownStore } from '@/stores/dropdown'

const dropdownStore = useDropdownStore()

watch(() => dropdownStore.openDropdown, (newValue) => {
  if (newValue !== 'profile') {
    isDropdownOpen.value = false
  }
})

const handleProfile = async () => {
  await router.push({
    name: 'summoner',
    params: {
      name: authStore.riotUser?.riotAccountInfoEntity.gameName,
      tag: authStore.riotUser?.riotAccountInfoEntity.tagLine
    },
  })
}

const handleLogout = async () => {
  isDropdownOpen.value = false;
  await authStore.logout();
}

const navigateToAccountSettings = () => {
  isDropdownOpen.value = false;
  router.push('/setting/account');
};

const navigateToReviewHistory = () => {
  isDropdownOpen.value = false;
  router.push('/review/history');
};

const toggleDropdown = () => {
  if (isDropdownOpen.value) {
    dropdownStore.setOpenDropdown(null)
  } else {
    dropdownStore.setOpenDropdown('profile')
  }
  isDropdownOpen.value = !isDropdownOpen.value
}

// 드롭다운 외부 클릭시 닫기
const handleClickOutside = (event: MouseEvent) => {
  if (profileDropdown.value && !profileDropdown.value.contains(event.target as Node)) {
    isDropdownOpen.value = false;
  }
};

onMounted(() => {
  isDropdownOpen.value = false;
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
.header {
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #0A0A0A;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 1000;
}

.header-content {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 24px;
  height: 100%;
  display: grid;
  grid-template-columns: 180px minmax(300px, auto) 200px;
  align-items: center;
  gap: 24px;
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
  width: 40px;
  height: 40px;
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
  gap: 8px;
  justify-self: end;
  min-width: 160px;
  justify-content: flex-end;
}

.login-wrapper {
  min-width: 160px;
  display: flex;
  justify-content: end;
}

.profile-dropdown {
  position: relative;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: #141414;
  border: 1.5px solid #333;
  border-radius: 8px;
  padding: 8px;
  min-width: 160px;
}

.dropdown-item {
  width: 100%;
  padding: 8px 16px;
  color: #888;
  background: none;
  border: none;
  font-size: 14px;
  text-align: left;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.dropdown-item:hover {
  background: #1a1a1a;
  color: white;
}

.dropdown-divider {
  height: 1px;
  background: #333;
  margin: 8px 0;
}

.logout {
  color: #ff4444;
}

.logout:hover {
  background: #1a1a1a;
  color: #ff6666;
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
    grid-template-columns: auto 1fr auto;
    gap: 16px;
    padding: 0 16px;
  }

  .logo span {
    display: none;
  }

  .logo-icon {
    width: 36px;
    height: 36px;
  }

  .nav {
    display: flex;
    gap: 16px;
    font-size: 14px;
  }

  .nav a {
    font-size: 14px;
  }

  .user-actions {
    gap: 8px;
    min-width: auto;
  }

  .login-wrapper {
    min-width: auto;
  }

  .logout-btn,
  .login-btn,
  .profile-btn {
    padding: 0 12px;
    font-size: 13px;
    height: 32px;
  }

  .profile-icon svg {
    width: 16px;
    height: 16px;
  }
}
</style>