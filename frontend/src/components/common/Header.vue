<!-- Header.vue -->
<template>
  <header class="header">
    <div class="header-content">
      <div class="left-section">
        <RouterLink to="/" class="logo">
          <div class="logo-icon">up</div>
          updownGG
        </RouterLink>
        <div class="search-container">
          <Search />
        </div>
      </div>

      <div class="right-section">
        <nav class="nav">
          <RouterLink to="/">홈</RouterLink>
          <RouterLink to="/ranking">랭킹</RouterLink>
          <RouterLink to="/stats">통계</RouterLink>
          <RouterLink to="/community/duo">커뮤니티</RouterLink>
        </nav>

        <div class="user-actions" v-if="authStore.isAuthenticated">
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
              <div class="dropdown-divider"></div>
              <button class="dropdown-item logout" @click="handleLogout">로그아웃</button>
            </div>
          </div>
        </div>
        <div class="user-actions" v-else>
          <router-link to="/login" class="login-btn">로그인</router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { useAuthStore } from "@/stores/auth.ts";
import Search from "@/components/common/Search.vue";

const authStore = useAuthStore();
const router = useRouter();
const isDropdownOpen = ref(false);
const profileDropdown = ref<HTMLElement | null>(null);

const handleProfile = async () => {
  isDropdownOpen.value = false;
  await router.push({
    name: 'summoner',
    params: {
      name: authStore.riotUser?.riotAccountInfoEntity.gameName,
      tag: authStore.riotUser?.riotAccountInfoEntity.tagLine
    },
  });
};

const navigateToAccountSettings = () => {
  isDropdownOpen.value = false;
  router.push('/setting/account');
};

const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
};

const handleClickOutside = (event: MouseEvent) => {
  if (profileDropdown.value && !profileDropdown.value.contains(event.target as Node)) {
    isDropdownOpen.value = false;
  }
};

const handleLogout = async () => {
  isDropdownOpen.value = false;
  await authStore.logout();
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
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
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 24px;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-section {
  display: flex;
  align-items: center;
  gap: 24px;
  flex: 1;
  max-width: 500px;
}

.right-section {
  display: flex;
  align-items: center;
  gap: 35px;
}

.logo {
  font-weight: 700;
  font-size: 24px;
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  white-space: nowrap;
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

.search-container {
  flex: 1;
  max-width: 400px;
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
  white-space: nowrap;
}

.nav a:hover,
.nav a.router-link-active {
  color: white;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
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
  white-space: nowrap;
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

@media (max-width: 1024px) {
  .header-content {
    padding: 0 16px;
  }

  .left-section {
    gap: 24px;
  }

  .right-section {
    gap: 24px;
  }

  .nav {
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .left-section {
    gap: 16px;
  }

  .nav {
    display: none;
  }

  .user-actions {
    gap: 8px;
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