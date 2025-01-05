<template>
  <div class="main">
    <div class="search-container">
      <div class="main-logo">
        <div class="main-logo-icon">up</div>
        updownGG
      </div>
      <Search />

      <!-- 최근 받은 평가 컴포넌트 -->
      <ReviewRolling
          :reviews="recentReviews"
          :rolling-interval="3000"
          wrapper-height="0"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import Search from '@/components/common/Search.vue';
import ReviewRolling from '@/components/review/ReviewRolling.vue';

// 샘플 리뷰 데이터 (실제로는 API에서 가져올 예정)
const recentReviews = ref([
  {
    summonerReviewSeq: 1,
    isUp: true,
    regDt: '2024-01-05',
    comment: "적극적으로 소통하며 팀워크가 좋았습니다."
  },
  {
    summonerReviewSeq: 2,
    isUp: true,
    regDt: '2024-01-04',
    comment: "라인전 압도적이었고 로밍도 완벽했습니다."
  },
  {
    summonerReviewSeq: 3,
    isUp: false,
    regDt: '2024-01-03',
    comment: "정글러와 협동이 아쉬웠습니다."
  }
]);
</script>

<style scoped>
.main {
  min-height: 100vh;
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
  width: 72px;
  height: 72px;
  background: #2979FF;
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
    width: 56px;
    height: 56px;
    font-size: 24px;
  }
}
</style>