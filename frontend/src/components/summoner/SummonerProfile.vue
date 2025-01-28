<template>
  <div class="profile-section">
    <div class="main-profile">
      <div class="profile-icon">
        <img :src="profileData.lolSummonerDto.profileIconUrl" alt="">
      </div>
      <div class="profile-info">
        <div class="summoner-name">{{ profileData.riotAccountInfoEntity.gameName }}</div>
        <div class="profile-stats">평가 {{ reviewStats?.totalReviewCnt ?? 0 }}회 · 최근 30일 {{ reviewStats?.last30DayReviewCnt ?? 0 }}회</div>
        <!-- 단순화된 좋아요/싫어요 통계 -->
        <div class="rating-stats">
          <div class="rating-item">
            <div class="up-down-stats">
              <div class="stat-group">
                <ThumbsUp class="thumb-icon up" />
                <span class="up-count">{{ reviewStats?.upCount ?? 0 }}</span>
              </div>
              <div class="stat-group">
                <ThumbsDown class="thumb-icon down" />
                <span class="down-count">{{ reviewStats?.downCount ?? 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="button-group">
        <button
            class="secondary-button"
            @click="$emit('updateMatches')"
            :disabled="isUpdatedMatchList"
            :class="{ 'loading': isUpdatedMatchList }"
        >
          <div class="button-content">
            <span v-if="!isUpdatedMatchList">전적 갱신</span>
            <div v-else class="spinner"></div>
          </div>
        </button>
      </div>
    </div>

    <!-- 확장 영역 -->
    <div v-if="isExpanded && recentReviews.length > 0" class="expanded-section">
      <!-- 자주 받은 태그 -->
      <div class="tags-section">
        <div class="stats-title">자주 받은 태그</div>
        <div v-if="frequentTags && frequentTags.length > 0" class="tags-slider">
          <TagList :tags="frequentTags" size="medium" is-show-count/>
        </div>
        <div v-else class="no-review-message">
          받은 태그가 없습니다.
        </div>
      </div>

      <!-- 평가 통계 -->
      <div class="stats-section">
        <div class="stats-title">평가 통계</div>
        <div class="stats-grid">
          <!-- 챔피언별 평가 -->
          <div class="champion-stats">
            <div class="champion-item" v-for="rating in ratingByChamp" :key="rating.champId">
              <img :src="rating.champIconUrl" class="position-icon" alt="Champion" />
              <div class="champion-rating">
                <span class="up-count">{{ rating.upCount || 0 }}</span>
                <span class="stats-divider">/</span>
                <span class="down-count">{{ rating.downCount || 0 }}</span>
              </div>
            </div>
          </div>
          <!-- 포지션별 평가 -->
          <div class="position-stats">
            <div class="position-item" v-for="(position, index) in ['top', 'jungle', 'mid', 'support', 'ad']" :key="position">
              <img :src="`/src/assets/icon/position/position_${position}.svg`" class="position-icon" alt="Position" />
              <div class="position-rating">
                <span class="up-count">{{ ratingByPosition?.[index]?.upCount ?? 0 }}</span>
                <span class="stats-divider">/</span>
                <span class="down-count">{{ ratingByPosition?.[index]?.downCount ?? 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 최근 평가 섹션 -->
      <ReviewRolling :reviews="recentReviews" title="최근 받은 평가" wrapper-height="90px"/>
    </div>
    <div v-else class="no-review-message">
      아직 작성된 리뷰가 없습니다.
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, onUnmounted, computed} from 'vue'
import type { LolSummonerProfileResDto } from '@/types/summoner.ts'
import type {
  ReviewRatingByChampDto,
  ReviewRatingByPositionDto,
  ReviewRequestDto,
  ReviewStatsDto,
  ReviewTagDto
} from "@/types/review.ts";
import { ThumbsUp, ThumbsDown } from 'lucide-vue-next'
import TagList from "@/components/common/TagList.vue";
import ReviewRolling from "@/components/review/ReviewRolling.vue";

const props = defineProps<{
  profileData: LolSummonerProfileResDto
  reviewStats: ReviewStatsDto | null
  frequentTags: ReviewTagDto[] | null
  recentReviews: ReviewRequestDto[]
  isUpdatedMatchList: boolean
  ratingByChamp: ReviewRatingByChampDto[] | null
  ratingByPosition: ReviewRatingByPositionDto[] | null
}>()

defineEmits<{
  (e: 'showDetail'): void
  (e: 'updateMatches'): void
}>()

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
  margin-top: 4px;
}

.up-down-stats {
  display: flex;
  align-items: center;
  gap: 24px;
}

.stat-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.thumb-icon {
  width: 20px;
  height: 20px;
}

.thumb-icon.up {
  color: #4CAF50;
}

.thumb-icon.down {
  color: #FF5252;
}

.up-count {
  color: #4CAF50;
  font-weight: 600;
  font-size: 16px;
}

.down-count {
  color: #FF5252;
  font-weight: 600;
  font-size: 16px;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
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
  min-width: 100px;
}

.secondary-button:hover {
  background: rgba(41, 121, 255, 0.1);
}

.secondary-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.secondary-button.loading {
  background: #4a4a4a;
  border-color: #4a4a4a;
  color: #ffffff;
}

.button-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 20px;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #ffffff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.expanded-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

/* 평가 통계 & 태그 섹션 */
.stats-section, .tags-section {
  margin-bottom: 24px;
}

.stats-title {
  font-size: 15px;
  font-weight: 500;
  color: #fff;
  margin-bottom: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  background: rgba(255, 255, 255, 0.03);
  padding: 16px;
  border-radius: 6px;
}

.stats-column {
  padding: 0 20px;
}

.stats-column:first-child {
  border-right: 1px solid rgba(255, 255, 255, 0.1);
}

.champion-stats {
  display: flex;
  gap: 16px;
  justify-content: space-between;
}

.position-stats {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.champion-item, .position-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-width: 48px;
}

.champion-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #2979FF;
}

.position-icon {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
}

.champion-rating, .position-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  margin-top: 4px;
}

.tags-slider {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  padding: 16px;
}

.tags-wrapper {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 4px;
  scrollbar-width: none;
}

.tags-wrapper::-webkit-scrollbar {
  display: none;
}

.tag {
  border-radius: 4px;
  padding: 6px 10px;
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.tag-up {
  background: rgba(41, 121, 255, 0.1);
}

.tag-down {
  background: rgba(235, 87, 87, 0.1);
}

.tag-text {
  font-size: 13px;
}

.tag-text-up {
  color: #2979FF;
}

.tag-text-down {
  color: #EB5757;
}

.tag-count {
  color: #9e9e9e;
  font-size: 12px;
}

.reviews-container {
  position: relative;
  margin-bottom: 2px;
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

.review-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.review-content {
  color: #e0e0e0;
  font-size: 14px;
  line-height: 1.5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

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

.no-review-message {
  color: #9e9e9e;
  text-align: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .stats-column:first-child {
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding-bottom: 24px;
  }

  .position-stats {
    padding: 0 12px;
  }

  .champion-stats {
    justify-content: space-between;
  }

  .main-profile {
    grid-template-columns: auto 1fr;
  }

  .button-group {
    grid-column: span 2;
    margin-top: 16px;
  }
}
</style>