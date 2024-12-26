<template>
  <div class="result-section">
    <div class="ranking-list">
      <div
          v-for="(player, index) in rankerCards"
          :key="player.puuid"
          class="ranking-item"
      >
        <div class="rank-number" :class="{ top3: index < 3 }">
          {{ index + 1 }}
        </div>

        <div class="player-info">
          <div class="player-icon">
            <img :src="player.profileIconUrl" :alt="player.gameName">
          </div>

          <div class="player-basic-info">
            <div class="player-name">
              <span class="name">{{ player.gameName }}</span>
              <span class="tag">#{{ player.tagLine }}</span>
            </div>
            <div class="review-counts">
              <span>평가 {{ player.totalReviewCount }}회</span>
              <span class="dot">•</span>
              <span>최근 30일 {{ player.last30DayReviewCnt }}회</span>
            </div>
          </div>

          <div class="ratings">
            <div class="rating-item">
              <div class="rating-row">
                <ThumbsUp class="thumb-icon up" />
                <span class="rating-value up-count">{{ player.upCount }}</span>
              </div>
              <div class="rating-row">
                <ThumbsDown class="thumb-icon down" />
                <span class="rating-value down-count">{{ player.downCount }}</span>
              </div>
              <div class="rating-row" v-if="player.totalReviewCount > 0">
                <span class="rating-value ratio-value">{{ calculateRatio(player.upCount, player.totalReviewCount) }}%</span>
              </div>
            </div>
          </div>

          <div class="player-tags">
            <span
                v-for="tag in player.recentTags"
                :key="tag.tagCode"
                class="tag"
            >
              {{ tag.tagValue }}
              <span class="tag-count">{{ tag.frequentCount }}</span>
              <span class="tooltip">{{ tag.tagDescription }}</span>
            </span>
          </div>
        </div>
      </div>
    </div>

    <button
        v-if="!isLastPage"
        class="load-more-button"
        :class="{ loading: isLoading }"
        @click="$emit('load-more')"
        :disabled="isLoading"
    >
      <div class="button-content">
        <span v-if="!isLoading">더보기</span>
        <div v-else class="spinner"></div>
      </div>
    </button>
  </div>
</template>

<script setup lang="ts">
import { getRatingColor } from '@/utils/ratingUtil'
import type { RankingCard } from '@/types/ranking'
import { ThumbsUp, ThumbsDown } from 'lucide-vue-next'


defineProps<{
  rankerCards: RankingCard[]
  isLoading: boolean
  isLastPage: boolean
}>()

defineEmits<{
  'load-more': []
}>()

const calculateRatio = (upCount: number, total: number) => {
  if (total === 0) return 0
  return Math.round((upCount / total) * 100)
}
</script>

<style scoped>
.result-section {
  display: flex;
  flex-direction: column;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  background: #141414;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform 0.2s;
}

.ranking-item:hover {
  transform: translateX(4px);
}

.rank-number {
  font-size: 24px;
  font-weight: 700;
  color: #666;
  width: 40px;
  text-align: center;
}

.rank-number.top3 {
  color: #2979FF;
}

.player-info {
  flex: 1;
  display: grid;
  grid-template-columns: 48px minmax(200px, 1fr) auto auto;
  gap: 24px;
  align-items: center;
  min-height: 110px;
}

.player-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
}

.player-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.player-basic-info {
  flex: 1;
  min-width: 200px;
}

.player-name {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.name {
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.tag {
  color: #666;
  font-size: 14px;
}

.review-counts {
  color: #666;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dot {
  color: #444;
}

.ratings {
  display: flex;
  justify-content: flex-end;
  width: 120px;  /* 너비 감소 */
  align-self: center;
}

.rating-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-row {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-end; /* 오른쪽 정렬 */
}


.rating-item.ratio {
  flex-direction: column;
  align-items: center;
}

.rating-value {
  font-size: 18px;
  font-weight: 600;
}

.rating-label {
  font-size: 12px;
  color: #666;
}

.player-tags {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 120px;
  align-self: center;
  min-height: 88px;
}

.thumb-icon {
  width: 16px;  /* 아이콘 크기 약간 감소 */
  height: 16px;
}

.thumb-icon.up {
  color: #4CAF50;
}

.thumb-icon.down {
  color: #FF5252;
}

.up-count {
  color: #4CAF50;
}

.down-count {
  color: #FF5252;
}

.ratio-value {
  color: #2979FF;
}

@media (min-width: 769px) {
  /* 태그 1개일 때 */
  .player-tags:has(:only-child) {
    justify-content: center;
  }

  /* 태그 2개일 때 */
  .player-tags:has(:first-child:nth-last-child(2)),
  .player-tags:has(:first-child:nth-last-child(2) ~ *) {
    justify-content: center;
  }

  /* 태그 3개일 때 */
  .player-tags:has(:first-child:nth-last-child(3)),
  .player-tags:has(:first-child:nth-last-child(3) ~ *) {
    justify-content: center;
  }
}

.tag {
  background: rgba(41, 121, 255, 0.1);
  color: #2979FF;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tag-count {
  color: #9e9e9e;
  font-size: 12px;
}

.load-more-button {
  width: 100%;
  background: transparent;
  border: 1px solid #2979FF;
  color: #2979FF;
  padding: 12px;
  border-radius: 8px;
  margin-top: 24px;
  cursor: pointer;
  transition: all 0.2s;
}

.load-more-button:hover:not(:disabled) {
  background: rgba(41, 121, 255, 0.1);
}

.load-more-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.button-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 20px;
}

.tooltip-container {
  position: relative;
}

.tooltip {
  visibility: hidden;
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 10;
  margin-bottom: 8px;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.2s;
}

.tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border-width: 5px;
  border-style: solid;
  border-color: rgba(0, 0, 0, 0.8) transparent transparent transparent;
}

.tooltip-container:hover .tooltip {
  visibility: visible;
  opacity: 1;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #2979FF;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1024px) {
  .player-info {
    grid-template-columns: 48px 1fr auto 100px;  /* 마지막 컬럼 너비 감소 */
    gap: 16px;
  }

  .player-tags {
    width: 100px;
  }

  .ratings {
    width: 100px;  /* 모바일에서 ratings 영역 너비 감소 */
  }
}

@media (max-width: 768px) {
  .ranking-item {
    padding: 16px;
    gap: 12px;
  }

  .player-info {
    grid-template-columns: 48px 1fr auto;
    gap: 12px;
  }

  .player-basic-info {
    min-width: 150px;
  }

  .ratings {
    grid-template-columns: repeat(3, 1fr);
    width: auto;
    min-width: 180px;
  }

  .rating-item {
    justify-content: center;
  }

  .player-tags {
    grid-column: 1 / -1;
    width: 100%;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    overflow-x: auto;
    padding-bottom: 4px;
    min-height: auto;
    margin-top: 8px;
    gap: 8px;
  }

  .tag {
    white-space: nowrap;
    flex-shrink: 0;
  }

  /* 스크롤바 스타일링 */
  .player-tags::-webkit-scrollbar {
    height: 4px;
  }

  .player-tags::-webkit-scrollbar-track {
    background: #1a1a1a;
    border-radius: 2px;
  }

  .player-tags::-webkit-scrollbar-thumb {
    background: #333;
    border-radius: 2px;
  }
}
</style>