// notice.vue
<template>
  <div v-if="notices.length > 0" class="site-notice-wrapper">
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
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import axios from 'axios';

interface Notice {
  seq: number;
  content: string;
  backgroundColor: string;
  isDisplay: boolean;
  regDt: string;
  delDt: string | null;
}

// 개발용 목업 데이터
const mockNotices: Notice[] = [
  {
    seq: 1,
    content: "🎮 신규 기능 업데이트! 전적 검색이 더욱 빨라졌습니다.",
    backgroundColor: "rgb(103 114 152 / 30%)",
    isDisplay: true,
    regDt: "2024-02-17T10:00:00",
    delDt: null
  },
  {
    seq: 2,
    content: "⚡ 서버 점검 안내: 2월 18일 오전 4시 ~ 6시",
    backgroundColor: "#FF5722",
    isDisplay: true,
    regDt: "2024-02-17T11:00:00",
    delDt: null
  },
  {
    seq: 3,
    content: "🎁 설날 이벤트 진행중! 친구 초대하고 특별 보상 받으세요",
    backgroundColor: "#4CAF50",
    isDisplay: true,
    regDt: "2024-02-17T12:00:00",
    delDt: null
  }
];

const notices = ref<Notice[]>(mockNotices);
const currentIndex = ref(0);
let autoPlayInterval: number | null = null;

const currentNotice = computed(() => notices.value[currentIndex.value]);

const fetchNotices = async () => {
  try {
    const response = await axios.get('/api/notices/current');
    notices.value = response.data.filter((notice: Notice) => notice.isDisplay);
    if (notices.value.length > 0) {
      startAutoPlay();
    }
  } catch (error) {
    console.error('Failed to fetch notices:', error);
  }
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
    }, 3000); // 3초마다 다음 공지로 전환
  }
};

const resetAutoPlay = () => {
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval);
    startAutoPlay();
  }
};

onMounted(() => {
  // 목업 데이터를 사용할 때는 바로 startAutoPlay 실행
  if (notices.value.length > 0) {
    startAutoPlay();
  } else {
    // API 연동 시에는 fetchNotices 사용
    fetchNotices();
  }
});

onUnmounted(() => {
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval);
  }
});
</script>

<style scoped>
.site-notice-wrapper {
  justify-items: center
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

.carousel-container {
  flex: 1;
  position: relative;
  overflow: hidden;
  min-height: 22px; /* 공지사항의 최소 높이 설정 */
}

.carousel-content {
  position: relative;
  height: 22px; /* carousel-container와 동일한 높이 */
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
}
</style>