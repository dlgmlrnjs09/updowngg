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
import type {ReviewRequestDto} from "@/types/review.ts";
import {reviewApi} from "@/api/review.ts";

const recentReviews = ref<ReviewRequestDto[]>([])

onMounted(async () => {
  await fetchRecentReviews();
});

const fetchRecentReviews = async () => {
  const response = await reviewApi.getRecentReviews(20);
  recentReviews.value = response.data;
}

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