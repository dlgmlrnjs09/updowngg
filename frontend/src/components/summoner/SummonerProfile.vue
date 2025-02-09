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

    <!-- 작성한 리뷰 알림 섹션 -->
    <div v-if="writtenReview && authStore.user?.puuid != profileData.riotAccountInfoEntity.puuid" class="written-review-section">
      <div class="written-review-content">
        <div class="written-review-text">
          <InfoIcon/>
          <span class="highlight-name">{{ profileData.riotAccountInfoEntity.gameName }}</span>님에게 작성한 리뷰가 있어요.
        </div>
        <button @click="$emit('openPreviousModal')" class="edit-button">
          확인
        </button>
      </div>
    </div>
    <!-- 리뷰작성가능 알림 섹션 -->
    <div v-if="!writtenReview && playTogetherMatch && authStore.user?.puuid != profileData.riotAccountInfoEntity.puuid" class="written-review-section">
      <div class="written-review-content">
        <div class="written-review-text">
          <InfoIcon/>
          <span class="highlight-name">{{ profileData.riotAccountInfoEntity.gameName }}</span>님과 함께한 게임, 어떠셨나요?
        </div>
        <button @click="$emit('openReviewModal', playTogetherMatch.participantList.filter(p => p.puuid === profileData.riotAccountInfoEntity.puuid)[0])" class="edit-button">
          리뷰작성
        </button>
      </div>
    </div>

    <button
        v-if="recentReviews.length > 0"
        @click="toggleExpanded"
        class="toggle-button"
    >
      <div class="toggle-content">
        <span>{{ isExpanded ? '접기' : '통계 보기' }}</span>
        <ChevronDown
            :class="['toggle-icon', { 'rotate-180': isExpanded }]"
            size="20"
        />
      </div>
    </button>

    <!-- 확장 영역 -->
    <Transition name="expand">
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
                <img :src="getPositionImage(position)" class="position-icon" alt="Position" />
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
    </Transition>

    <div v-if="!recentReviews.length" class="no-review-message">
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
import { ThumbsUp, ThumbsDown, ChevronDown, InfoIcon } from 'lucide-vue-next'
import TagList from "@/components/common/TagList.vue";
import ReviewRolling from "@/components/review/ReviewRolling.vue";
import ReviewStatic from "@/components/review/ReviewStatic.vue";
import type {LolMatchInfoRes} from "@/types/match.ts";
import {useAuthStore} from "@/stores/auth.ts";
import {useImageUrl} from "@/utils/imageUtil.ts";
const { getPositionImage } = useImageUrl();

const authStore = useAuthStore();

const props = defineProps<{
  profileData: LolSummonerProfileResDto
  reviewStats: ReviewStatsDto | null
  frequentTags: ReviewTagDto[] | null
  recentReviews: ReviewRequestDto[]
  isUpdatedMatchList: boolean
  ratingByChamp: ReviewRatingByChampDto[] | null
  ratingByPosition: ReviewRatingByPositionDto[] | null
  writtenReview: ReviewRequestDto | null
  playTogetherMatch: LolMatchInfoRes;
}>()

defineEmits<{
  (e: 'showDetail'): void
  (e: 'updateMatches'): void
  (e: 'openPreviousModal'): void
  (e: 'openReviewModal', player: any): void
}>()

const isExpanded = ref(true)

const toggleExpanded = () => {
  isExpanded.value = !isExpanded.value
}
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
  /*margin-top: 20px;*/
  padding-top: 20px;
  /*border-top: 1px solid rgba(255, 255, 255, 0.1);*/
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
  position: relative;
}

.stats-grid::after {
  content: '';
  position: absolute;
  top: 16px; /* Match padding */
  bottom: 16px;
  left: 50%;
  width: 1px;
  background: rgba(255, 255, 255, 0.1);
}

.stats-column {
  padding: 0 20px;
}

.stats-column:first-child {
  border-right: 1px solid rgba(255, 255, 255, 0.1);
}

.champion-stats, .position-stats {
  display: flex;
  justify-content: space-between;
  width: 100%;
  padding: 0 24px; /* Add horizontal padding for spacing */
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

/* 토글 버튼 스타일 */
.toggle-button {
  width: 100%;
  background: transparent;
  border: none;
  color: #9e9e9e;
  padding: 12px;
  margin-top: 16px;
  cursor: pointer;
  transition: all 0.2s;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.toggle-button:hover {
  color: #ffffff;
}

.toggle-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 14px;
}

.toggle-icon {
  transition: transform 0.3s ease;
}

/* 확장 영역 트랜지션 */
.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease-out;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  opacity: 0;
  transform: translateY(-20px);
  max-height: 0;
}

.expand-enter-to,
.expand-leave-from {
  opacity: 1;
  transform: translateY(0);
  max-height: 1000px;
}

.written-review-text {
  display: flex;
  align-items: center;
  gap: 8px; /* InfoIcon과 텍스트 사이의 간격 */
  /*font-size: 14px;  !* 기존 사이즈와 맞춤 *!*/
}

.highlight-name {
  color: #2979FF;  /* 블루 계열 색상 */
  font-weight: 600;  /* 볼드체 */
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .stats-grid::after {
    display: none;
  }

  .stats-column:first-child {
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding-bottom: 24px;
  }

  .champion-stats, .position-stats {
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

.written-review-section {
  margin-top: 30px;
  margin-bottom: 10px;
  padding: 16px;
  background: rgba(41, 121, 255, 0.1);
  border-radius: 8px;
}

.written-review-content {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.written-review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.written-review-title {
  font-size: 15px;
  font-weight: 500;
  color: #2979FF;
}

.written-review-date {
  font-size: 12px;
  color: #9e9e9e;
}

.written-review-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.written-review-rating {
  display: flex;
  align-items: center;
}

.written-review-tags {
  margin: 4px 0;
}

.written-review-comment {
  font-size: 14px;
  color: #e0e0e0;
  line-height: 1.5;
}

.edit-button {
  top: 0;
  right: 0;
  background: transparent;
  border: 1px solid #2979FF;
  color: #2979FF;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-button:hover {
  background: rgba(41, 121, 255, 0.1);
}
</style>
