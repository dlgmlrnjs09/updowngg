<template>
  <div class="main">
    <div class="search-container">
      <div class="main-logo">
        <div class="main-logo-icon"><img src="/favicon.ico" alt="favicon"></div>
        updownGG
      </div>
      <Search />

      <!-- 최근 받은 평가 컴포넌트 -->
      <ReviewRolling
          :reviews="recentReviews"
          :rolling-interval="3000"
          wrapper-height="0"
          :show-profile="true"
      />
    </div>
    
    <!-- 가이드 모달 -->
    <GuideModal
      v-if="showGuide"
      :pages="guidePages"
      :show="showGuide"
      :close-on-overlay-click="true"
      @close="closeGuide"
      @page-changed="handleGuidePageChange"
      @complete="completeGuide"
    />
    
    <!-- 가이드 시작 버튼 -->
    <button
      v-if="!showGuide && !guideCompleted"
      @click="startGuide"
      class="fixed bottom-8 right-8 bg-[#2979FF] text-white p-3 rounded-full shadow-lg hover:bg-[#1A67E0] transition-all z-50 flex items-center group"
      aria-label="updownGG 사용법 보기"
    >
      <HelpCircle class="w-6 h-6" />
      <span class="max-w-0 overflow-hidden whitespace-nowrap group-hover:max-w-xs transition-all duration-500 ease-linear bg-[#2979FF] rounded-l-full -ml-3 pl-5 pr-2">
        사용법 보기
      </span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import Search from '@/components/common/Search.vue';
import ReviewRolling from '@/components/review/ReviewRolling.vue';
import GuideModal from '@/components/common/GuideModal.vue';
import { HelpCircle } from 'lucide-vue-next';
import type {ReviewRequestDto} from "@/types/review.ts";
import {reviewApi} from "@/api/review.ts";
import { useToast } from "vue-toastification";

const toast = useToast();
const recentReviews = ref<ReviewRequestDto[]>([]);

// 가이드 모달 관련 상태
const showGuide = ref(false);
const guideCompleted = ref(false);
const GUIDE_COMPLETED_KEY = 'main_guide_completed';
const currentGuidePageIndex = ref(0);

// 가이드 페이지 정의
const guidePages = [
  {
    title: 'updownGG에 오신 것을 환영합니다',
    content: 'updownGG는 리그 오브 레전드 플레이어들의 실력과 매너를 평가하고 함께 플레이할 팀원을 찾을 수 있는 서비스입니다. 이 가이드를 통해 주요 기능을 알아보세요.'
    // image: '/path/to/your/image.jpg' - 이미지는 직접 추가 예정
  },
  {
    title: '소환사 검색',
    content: '상단 검색창에 소환사 이름과 태그를 입력하여 소환사를 검색할 수 있습니다. 검색 결과에서 소환사 프로필, 경기 기록, 평가 정보 등을 확인할 수 있습니다.'
    // image: '/path/to/your/image.jpg' - 이미지는 직접 추가 예정
  },
  {
    title: '소환사 평가',
    content: '소환사와 함께 게임을 한 후 해당 소환사의 실력과 매너를 평가할 수 있습니다. 평가는 점수와 태그로 남길 수 있으며, 이 평가는 다른 사용자들에게 소환사에 대한 참고 정보를 제공합니다.'
    // image: '/path/to/your/image.jpg' - 이미지는 직접 추가 예정
  },
  {
    title: '파티 커뮤니티',
    content: '함께 게임할 팀원을 찾고 싶다면 파티 커뮤니티를 이용하세요. 원하는 게임 모드, 포지션, 티어에 맞게 파티를 생성하거나 참가할 수 있습니다. 리그 오브 레전드를 더 즐겁게 즐기는 방법입니다.'
    // image: '/path/to/your/image.jpg' - 이미지는 직접 추가 예정
  }
];

// 가이드 관련 메서드
const startGuide = () => {
  currentGuidePageIndex.value = 0;
  showGuide.value = true;
};

const closeGuide = () => {
  showGuide.value = false;
};

const handleGuidePageChange = (pageIndex: number) => {
  currentGuidePageIndex.value = pageIndex;
};

const completeGuide = () => {
  showGuide.value = false;
  guideCompleted.value = true;
  
  // 로컬 스토리지에 가이드 완료 상태 저장
  localStorage.setItem(GUIDE_COMPLETED_KEY, 'true');
  
  // 완료 메시지
  toast.success('updownGG 서비스 가이드를 완료했습니다!');
  
  // 5분 후에 도움 버튼 다시 표시
  setTimeout(() => {
    guideCompleted.value = false;
  }, 300000); // 5분
};

onMounted(async () => {
  await fetchRecentReviews();
  
  // 로컬 스토리지에서 가이드 완료 상태 확인
  const completedStatus = localStorage.getItem(GUIDE_COMPLETED_KEY);
  guideCompleted.value = completedStatus === 'true';
  
  // 첫 방문자에게 2초 후 가이드 표시
  if (!guideCompleted.value) {
    setTimeout(() => {
      startGuide();
    }, 2000);
  }
});

const fetchRecentReviews = async () => {
  const response = await reviewApi.getRecentReviews(20);
  recentReviews.value = response.data;
}

</script>

<style scoped>
.main {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.search-container {
  max-width: 600px;
  width: 100%;
  text-align: center;
}

.main-logo {
  font-size: 64px;
  font-weight: 800;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 40px;
}

.main-logo-icon {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
  font-weight: 800;
}

.search-example {
  margin-top: 16px;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

@media (max-width: 768px) {
  .main-logo {
    font-size: 48px;
  }

  .main-logo-icon {
    width: 70px;
    height: 70px;
    font-size: 24px;
  }
}
</style>