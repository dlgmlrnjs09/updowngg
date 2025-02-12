<template>
  <div class="container">
    <div class="settings-card">
      <h1 class="title">계정 설정</h1>

      <div class="section">
        <h2 class="section-title">연동된 계정</h2>

        <!-- 라이엇 계정 (이미 연동됨) -->
        <div class="account-item">
          <div class="account-info">
            <img src="@/assets/icon/riot_logo.svg" alt="Riot" class="account-icon" />
            <div class="account-details">
              <div class="account-name">라이엇 계정</div>
              <div class="account-status connected">
                {{ authStore.riotUser?.riotAccountInfoEntity.gameName }}#{{ authStore.riotUser?.riotAccountInfoEntity.tagLine }}
              </div>
            </div>
          </div>
        </div>

        <!-- 디스코드 계정 -->
        <div class="account-item">
          <div class="account-info">
            <img src="@/assets/icon/discord_logo.svg" alt="Discord" class="account-icon" />
            <div class="account-details">
              <div class="account-name">디스코드</div>
              <div class="account-status" :class="{ connected: isDiscordLinked }">
                {{ discordUsername || '연동되지 않음' }}
              </div>
            </div>
          </div>
          <button
              class="connect-btn"
              :class="{ connected: isDiscordLinked }"
              @click="handleDiscordLink"
          >
            {{ isDiscordLinked ? '연동 해제' : '계정 연동' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useToast } from 'vue-toastification';
import {authApi} from "@/api/auth.ts";
import type {DiscordAccount} from "@/types/auth.ts";

const authStore = useAuthStore();
const toast = useToast();

const isDiscordLinked = ref(false);
const discordUsername = ref('');

// 디스코드 연동 상태 확인
const checkDiscordLink = async () => {
  try {
    const response = (await authApi.checkDiscord()).data as DiscordAccount;
    isDiscordLinked.value = response.discordCode !== undefined;
    discordUsername.value = response.globalName || '';
  } catch (error) {
    console.error('Failed to check Discord link status:', error);
    toast.error('디스코드 연동 상태 확인에 실패했습니다.');
  }
};

// 디스코드 연동 처리
const handleDiscordLink = async () => {
  if (isDiscordLinked.value) {
    // 연동 해제 처리
    try {
        await authApi.disconnectDiscord()
        isDiscordLinked.value = false;
        discordUsername.value = '';
        toast.success('디스코드 연동이 해제되었습니다.');
    } catch (error) {
      console.error('Failed to unlink Discord:', error);
      toast.error('디스코드 연동 해제에 실패했습니다.');
    }
  } else {
    try {
      await authApi.connectDiscord();
      const redirectUri = `${window.location.origin}/oauth2/discord/callback`;
      window.location.href = `${import.meta.env.VITE_API_URL}/oauth2/authorization/discord?redirect_uri=${encodeURIComponent(redirectUri)}`;
    } catch (error) {
      console.error('Failed to redirect to Discord auth:', error);
      toast.error('디스코드 연동 시도 중 오류가 발생했습니다.');
    }
  }
};

// OAuth 리다이렉트 후 상태 확인
const checkOAuthStatus = () => {
  const urlParams = new URLSearchParams(window.location.search);
  const error = urlParams.get('error');
  const success = urlParams.get('success');

  if (error) {
    toast.error('디스코드 연동에 실패했습니다.');
  } else if (success) {
    toast.success('디스코드 계정이 성공적으로 연동되었습니다.');
    checkDiscordLink();
  }
};

onMounted(() => {
  checkDiscordLink();
  checkOAuthStatus();
});
</script>

<style scoped>
.container {
  max-width: 800px;
  min-height: 100vh; /* 전체 뷰포트 높이 */
  margin: 0 auto; /* 좌우 중앙 정렬 */
  padding: 20px;
  display: flex; /* Flexbox 사용 */
  align-items: center; /* 수직 중앙 정렬 */
  justify-content: center; /* 수평 중앙 정렬 */
}

.settings-card {
  width: 100%;
  background: #141414;
  border-radius: 16px;
  padding: 32px;
  border: 1px solid #333;
}

.title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 32px;
}

.section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #888;
  margin-bottom: 16px;
}

.account-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #1a1a1a;
  border-radius: 12px;
  margin-bottom: 16px;
}

.account-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.account-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
}

.account-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.account-name {
  font-weight: 600;
}

.account-status {
  font-size: 14px;
  color: #888;
}

.account-status.connected {
  color: #2979FF;
}

.connect-btn {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  background: #2979FF;
  border: none;
  color: white;
}

.connect-btn.connected {
  background: #1a1a1a;
  border: 1.5px solid #444;
  color: #888;
}

.connect-btn:hover {
  opacity: 0.9;
}

.connect-btn.connected:hover {
  border-color: #666;
  color: white;
}
</style>