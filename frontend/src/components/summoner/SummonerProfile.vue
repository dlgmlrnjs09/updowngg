<template>
  <div class="profile-section">
    <div class="main-profile">
      <div class="profile-icon">
        <img :src="profileData.lolSummonerDto.profileIconUrl" alt="">
      </div>
      <div class="profile-info">
        <div class="summoner-name">{{ profileData.riotAccountInfoEntity.gameName }}</div>
        <div class="profile-stats">평가 {{ reviewStats?.totalReviewCnt ?? 0 }}회 · 최근 30일 {{ reviewStats?.last30DayReviewCnt ?? 0 }}회</div>
        <div class="rating-stats" >
          <div class="rating-item">
            <span class="rating-value">{{ reviewStats?.skillRatingAvg ?? '0.0' }}</span>
            <span class="rating-label">실력</span>
          </div>
          <div class="rating-item">
            <span class="rating-value">{{ reviewStats?.teamworkRatingAvg ?? '0.0' }}</span>
            <span class="rating-label">팀워크</span>
          </div>
          <div class="rating-item">
            <span class="rating-value">{{ reviewStats?.mannerRatingAvg ?? '0.0'}}</span>
            <span class="rating-label">매너</span>
          </div>
        </div>
      </div>
      <div class="button-group">
        <button class="secondary-button" @click="$emit('updateMatches')">전적 갱신</button>
<!--        <button class="expand-button" @click="isExpanded = !isExpanded">-->
<!--          {{ isExpanded ? '접기' : '더보기' }}-->
<!--        </button>-->
      </div>
    </div>

    <!-- 확장 영역 -->
    <div v-if="isExpanded" class="expanded-section">
      <div class="stats-grid">
        <!-- 평가 통계 -->
        <div class="stats-column">
          <div class="stats-title">평가 통계</div>
          <div class="stats-content">
            <div class="stat-item">
              <div class="stat-label">평가자 신뢰도</div>
              <div class="stat-value rating-badge">4.7</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">긍정적 평가</div>
              <div class="stat-value">75%</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">평가 참여율</div>
              <div class="stat-value">상위 10%</div>
            </div>
          </div>
        </div>

        <!-- 최근 태그 -->
        <div class="stats-column">
          <div class="stats-title">자주 받은 태그</div>
          <div class="tags-container">
            <div class="tag">
              <span class="tag-text">포지티브</span>
              <span class="tag-count">38</span>
            </div>
            <div class="tag">
              <span class="tag-text">침착함</span>
              <span class="tag-count">27</span>
            </div>
            <div class="tag">
              <span class="tag-text">리더십</span>
              <span class="tag-count">25</span>
            </div>
            <div class="tag">
              <span class="tag-text">좋은 소통</span>
              <span class="tag-count">21</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 최근 평가 섹션 수정 -->
      <div class="recent-reviews">
        <div class="stats-title">최근 받은 평가</div>
        <div class="reviews-container">
          <transition-group
              name="slide"
              tag="div"
              class="review-wrapper"
          >
            <div
                v-for="review in displayReview"
                :key="review.id"
                class="review-item"
            >
              <div class="review-header">
                <div class="review-rating">{{ review.rating }}</div>
                <div class="review-date">{{ review.date }}</div>
              </div>
              <div class="review-content">
                "{{ review.content }}"
              </div>
            </div>
          </transition-group>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, onUnmounted, computed} from 'vue'
import type { LolSummonerProfileResDto } from '@/types/summoner.ts'
import type {ReviewStatsDto} from "@/types/review.ts";

const props = defineProps<{
  profileData: LolSummonerProfileResDto
  reviewStats: ReviewStatsDto
}>()

defineEmits<{
  (e: 'showDetail'): void
  (e: 'updateMatches'): void
}>()

// 리뷰 데이터
const reviews = [
  {
    id: 1,
    rating: 4.8,
    date: "2024.12.14",
    content: "팀워크가 좋고 피드백을 잘 수용하는 플레이어입니다. 게임 내내 긍정적인 분위기를 만들어주셨어요."
  },
  {
    id: 2,
    rating: 4.5,
    date: "2024.12.13",
    content: "어려운 상황에서도 침착하게 플레이하시는 모습이 인상적이었습니다."
  },
  {
    id: 3,
    rating: 4.7,
    date: "2024.12.12",
    content: "적절한 타이밍에 로밍해주시고 팀 밸런스를 잘 맞춰주셨습니다."
  }
]

const currentIndex = ref(0)
const displayReview = computed(() => {
  return [reviews[currentIndex.value % reviews.length]]
})

let timer: number | null = null

const startCarousel = () => {
  timer = window.setInterval(() => {
    currentIndex.value++
  }, 3000)
}

onMounted(() => {
  startCarousel()
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

const isExpanded = ref(true)
</script>

<style scoped>
.profile-section {
  background: #141414;
  border-radius: 8px;
  padding: 20px;
}

.main-profile {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 20px;
  align-items: center;
}

.profile-icon {
  width: 84px;
  height: 84px;
  border-radius: 50%;
  overflow: hidden;
}

.profile-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summoner-name {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
}

.profile-stats {
  color: #9e9e9e;
  font-size: 13px;
}

.rating-stats {
  display: flex;
  gap: 24px;
  margin-top: 4px;
}

.rating-item {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.rating-value {
  font-weight: 600;
  color: #2979FF;
  font-size: 15px;
}

.rating-label {
  color: #9e9e9e;
  font-size: 13px;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.primary-button {
  background: #2979FF;
  color: white;
  padding: 8px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: background 0.2s;
}

.primary-button:hover {
  background: #2264D1;
}

.secondary-button {
  background: transparent;
  border: 1px solid #2979FF;
  color: #2979FF;
  padding: 8px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.secondary-button:hover {
  background: rgba(41, 121, 255, 0.1);
}

.expand-button {
  background: transparent;
  border: none;
  color: #9e9e9e;
  font-size: 13px;
  cursor: pointer;
  padding: 4px;
}

.expand-button:hover {
  color: #fff;
}

.expanded-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
  margin-bottom: 24px;
}

.stats-title {
  font-size: 15px;
  font-weight: 500;
  color: #fff;
  margin-bottom: 16px;
}

.stats-column {
  display: flex;
  flex-direction: column;
}

.stats-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  color: #9e9e9e;
  font-size: 14px;
}

.stat-value {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.rating-badge {
  background: rgba(41, 121, 255, 0.1);
  color: #2979FF;
  padding: 2px 8px;
  border-radius: 4px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  background: rgba(41, 121, 255, 0.1);
  border-radius: 4px;
  padding: 6px 10px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tag-text {
  color: #2979FF;
  font-size: 13px;
}

.tag-count {
  color: #9e9e9e;
  font-size: 12px;
}

.reviews-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  padding: 16px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.review-rating {
  color: #2979FF;
  font-weight: 500;
}

.review-date {
  color: #9e9e9e;
  font-size: 13px;
}

.review-content {
  color: #e0e0e0;
  font-size: 14px;
  line-height: 1.5;
}

/* 리뷰 섹션 관련 스타일 수정 */
.reviews-container {
  position: relative;
  margin-bottom: 2px; /* stats-grid와 동일한 마진 유지 */
}

.review-wrapper {
  position: relative;
  min-height: 80px;
}

.review-item {
  position: absolute;
  width: 100%;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  padding: 16px;
}

/* 슬라이드 애니메이션 */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.5s ease;
}

.slide-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.slide-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}
</style>