<template>
  <div v-if="notices.length > 0 && !isHidden" class="site-notice-wrapper">
    <div class="site-notice" :style="{ backgroundColor: currentNotice.backgroundColor }">
      <div class="notice-content">
        <div class="carousel-container">
          <TransitionGroup
              name="carousel"
              tag="div"
              class="carousel-content"
          >
            <div
                v-for="(notice, index) in notices"
                :key="notice.seq"
                v-show="currentIndex === index"
                class="carousel-item"
            >
              {{ notice.content }}
            </div>
          </TransitionGroup>
        </div>

        <div class="notice-controls">
          <!-- 여러 공지사항이 있을 경우에만 네비게이션 표시 -->
          <div v-if="notices.length > 1" class="carousel-nav">
            <button @click="prevNotice" class="nav-button">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="15 18 9 12 15 6"></polyline>
              </svg>
            </button>
            <div class="carousel-indicators">
              <button
                  v-for="(_, index) in notices"
                  :key="index"
                  @click="goToNotice(index)"
                  class="indicator"
                  :class="{ active: currentIndex === index }"
              ></button>
            </div>
            <button @click="nextNotice" class="nav-button">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="9 18 15 12 9 6"></polyline>
              </svg>
            </button>
          </div>

          <!-- X 버튼 (오늘 하루 보지 않기) -->
          <button @click="hideNoticeForToday" class="close-button" title="오늘 하루 보지 않기">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useNoticeStore } from '@/stores/notice'
import type { NoticeResDto } from "@/types/notice.ts";

// 쿠키 키
const NOTICE_HIDDEN_COOKIE = 'notice_hidden_until';
const REFRESH_INTERVAL = 10 * 60 * 1000; // 10분

const noticeStore = useNoticeStore();
const notices = ref<NoticeResDto[]>([]);
const currentIndex = ref(0);
const isHidden = ref(false);
let autoPlayInterval: number | null = null;
let refreshInterval: number | null = null;

const currentNotice = computed(() => notices.value[currentIndex.value]);

// 쿠키 설정 함수
const setCookie = (name: string, value: string, hours: number) => {
  const date = new Date();
  date.setTime(date.getTime() + (hours * 60 * 60 * 1000));
  const expires = `expires=${date.toUTCString()}`;
  document.cookie = `${name}=${value};${expires};path=/`;
};

// 쿠키 가져오기 함수
const getCookie = (name: string) => {
  const cookieName = `${name}=`;
  const cookies = document.cookie.split(';');
  for (let cookie of cookies) {
    cookie = cookie.trim();
    if (cookie.indexOf(cookieName) === 0) {
      return cookie.substring(cookieName.length, cookie.length);
    }
  }
  return null;
};

const hideNoticeForToday = () => {
  const tomorrow = new Date();
  tomorrow.setHours(24, 0, 0, 0); // 다음날 자정으로 설정
  const now = new Date();
  const hoursUntilTomorrow = (tomorrow.getTime() - now.getTime()) / (1000 * 60 * 60);

  setCookie(NOTICE_HIDDEN_COOKIE, 'true', hoursUntilTomorrow);
  isHidden.value = true;
};

const checkHiddenState = () => {
  const hiddenUntil = getCookie(NOTICE_HIDDEN_COOKIE);
  isHidden.value = hiddenUntil !== null;
};

const nextNotice = () => {
  currentIndex.value = (currentIndex.value + 1) % notices.value.length;
  resetAutoPlay();
};

const prevNotice = () => {
  currentIndex.value = currentIndex.value === 0
      ? notices.value.length - 1
      : currentIndex.value - 1;
  resetAutoPlay();
};

const goToNotice = (index: number) => {
  currentIndex.value = index;
  resetAutoPlay();
};

const startAutoPlay = () => {
  if (notices.value.length > 1) {
    autoPlayInterval = window.setInterval(() => {
      nextNotice();
    }, 3000);
  }
};

const resetAutoPlay = () => {
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval);
    startAutoPlay();
  }
};

const startPeriodicRefresh = () => {
  refreshInterval = window.setInterval(async () => {
    notices.value = await noticeStore.getNotices();
  }, REFRESH_INTERVAL);
};

// store의 notices가 변경될 때마다 실행
watch(() => noticeStore.notices, (newNotices) => {
  notices.value = newNotices;
  if (newNotices.length > 0) {
    startAutoPlay();
  }
});

onMounted(async () => {
  checkHiddenState();

  if (!isHidden.value) {
    notices.value = await noticeStore.getNotices();
    startPeriodicRefresh();
  }
});

onUnmounted(() => {
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval);
  }
  if (refreshInterval) {
    clearInterval(refreshInterval);
  }
});
</script>

<style scoped>
.site-notice-wrapper {
  justify-items: center;
}

.site-notice {
  width: 100%;
  padding: 8px 0;
  text-align: center;
  font-size: 14px;
  color: white;
  transition: background-color 0.8s ease-in-out;
  margin-bottom: 10px;
}

.notice-content {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.notice-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.carousel-container {
  flex: 1;
  position: relative;
  overflow: hidden;
  min-height: 22px;
}

.carousel-content {
  position: relative;
  height: 22px;
}

.carousel-item {
  text-align: center;
  position: absolute;
  width: 100%;
  left: 0;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

.carousel-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-button {
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  padding: 4px;
  transition: color 0.2s;
}

.nav-button:hover {
  color: white;
}

.carousel-indicators {
  display: flex;
  gap: 4px;
}

.indicator {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.3);
  border: none;
  cursor: pointer;
  padding: 0;
  transition: all 0.2s;
}

.indicator.active {
  background-color: white;
  transform: scale(1.2);
}

.close-button {
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  padding: 4px;
  transition: color 0.2s;
}

.close-button:hover {
  color: white;
}

/* Carousel Transitions */
.carousel-enter-active,
.carousel-leave-active {
  transition: opacity 0.3s ease;
}

.carousel-enter-from,
.carousel-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .site-notice {
    padding: 6px 0;
    font-size: 13px;
  }

  .notice-content {
    padding: 0 16px;
  }

  .nav-button svg {
    width: 14px;
    height: 14px;
  }

  .hide-today-button {
    font-size: 11px;
    padding: 3px 8px;
  }
}
</style>