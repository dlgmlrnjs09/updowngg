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
    
    <!-- 온보딩 모달 -->
    <OnboardingModal 
      :is-open="showOnboarding" 
      @close="hideOnboarding" 
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import Search from '@/components/common/Search.vue';
import ReviewRolling from '@/components/review/ReviewRolling.vue';
import OnboardingModal from '@/components/onboarding/OnboardingModal.vue';
import type {ReviewRequestDto} from "@/types/review.ts";
import {reviewApi} from "@/api/review.ts";

const recentReviews = ref<ReviewRequestDto[]>([]);
const showOnboarding = ref(false);

onMounted(async () => {
  await fetchRecentReviews();
  checkOnboarding();
});

const fetchRecentReviews = async () => {
  const response = await reviewApi.getRecentReviews(20);
  recentReviews.value = response.data;
};

const checkOnboarding = () => {
  // 이미 온보딩을 본 사용자인지 확인
  const onboardingShown = localStorage.getItem('onboardingShown');
  if (!onboardingShown) {
    showOnboarding.value = true;
  }
};

const hideOnboarding = () => {
  showOnboarding.value = false;
};

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