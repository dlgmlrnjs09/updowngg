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
        :slides="onboardingSlides"
        :storage-key="onboardingStorageKey"
        @close="hideOnboarding"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import Search from '@/components/common/Search.vue';
import ReviewRolling from '@/components/review/ReviewRolling.vue';
import OnboardingModal from '@/components/common/OnboardingModal.vue';
import type { ReviewRequestDto } from "@/types/review.ts";
import { reviewApi } from "@/api/review.ts";
import type { Slide } from '@/components/common/OnboardingModal.vue';

// 메인페이지 이미지
import pcCommunity from '@/assets/guide/main/pc_main_guide_community.png';
import pcProfile from '@/assets/guide/main/pc_main_guide_profile.png';
import pcReview from '@/assets/guide/main/pc_main_guide_review.png';
import pcRanking from '@/assets/guide/main/pc_main_guide_ranking.png';
import pcStats from '@/assets/guide/main/pc_main_guide_stats.png';

import moCommunity from '@/assets/guide/main/mo_main_guide_community.png';
import moProfile from '@/assets/guide/main/mo_main_guide_profile.png';
import moReview from '@/assets/guide/main/mo_main_guide_review.png';
import moRanking from '@/assets/guide/main/mo_main_guide_ranking.png';
import moStats from '@/assets/guide/main/mo_main_guide_stats.png';

const recentReviews = ref<ReviewRequestDto[]>([]);
const showOnboarding = ref(false);
const onboardingStorageKey = 'mainPageOnboardingShown';

// 온보딩 슬라이드 데이터
const onboardingSlides = ref<Slide[]>([
  {
    title: '커뮤니티',
    description: '함께할 소환사들을 찾아보세요.',
    image: {
      desktop: pcCommunity,
      mobile: moCommunity
    }
  },
  {
    title: '프로필',
    description: '인게임 정보와 받은 평가를 확인할 수 있어요.',
    image: {
      desktop: pcProfile,
      mobile: moProfile
    }
  },
  {
    title: '소환사 평가',
    description: '함께 플레이한 소환사를 평가할 수 있어요.',
    image: {
      desktop: pcReview,
      mobile: moReview
    }
  },
  {
    title: '통계',
    description: '좋은 평가를 받고있는 챔피언을 확인해보세요.',
    image: {
      desktop: pcStats,
      mobile: moStats
    }
  },
  {
    title: '랭킹',
    description: '좋은 평가를 받고있는 소환사들을 확인해보세요.',
    image: {
      desktop: pcRanking,
      mobile: moRanking
    }
  }
]);

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
  const onboardingShown = localStorage.getItem(onboardingStorageKey);
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