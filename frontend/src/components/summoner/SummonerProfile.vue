<template>
  <div class="profile-section">
    <!-- 기존 프로필 섹션 -->
    <div class="main-profile">
      <div class="profile-icon">
        <img :src="profileData.lolSummonerDto.profileIconUrl" alt="프로필 아이콘">
      </div>
      <div class="profile-info">
        <div class="summoner-name">{{ profileData.riotAccountInfoEntity.gameName }}</div>
        <div class="profile-stats">평가 {{ reviewStats?.totalReviewCnt ?? 0 }}회 · 최근 30일 {{ reviewStats?.last30DayReviewCnt ?? 0 }}회</div>
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

    <!-- 랭크 티어 섹션 -->
    <div class="rank-section">
      <div class="rank-card solo">
        <div class="rank-header">
          <img :src="soloRankInfo?.tier ? getTierImage(soloRankInfo?.tier) : getTierImage(`unranked`)" alt="개인 랭크" class="rank-emblem"/>
          <div class="rank-info">
            <div class="rank-type">개인 랭크</div>
            <div class="rank-tier">{{ soloRankInfo?.tier || '배치 전' }}</div>
            <div v-if="soloRankInfo?.tier" class="rank-lp">
              {{ soloRankInfo?.leaguePoints }} LP
            </div>
          </div>
        </div>
        <div v-if="soloRankInfo?.tier" class="rank-record">
          {{ soloRankInfo?.wins }}승 {{ soloRankInfo?.losses }}패
          <span class="win-rate">({{ calculateWinRate(soloRankInfo?.wins, soloRankInfo?.losses) }}%)</span>
        </div>
      </div>
      <div class="rank-card flex">
        <div class="rank-header">
          <img :src="flexRankInfo?.tier ? getTierImage(flexRankInfo?.tier) : getTierImage(`unranked`)" alt="자유 랭크" class="rank-emblem"/>
          <div class="rank-info">
            <div class="rank-type">자유 랭크</div>
            <div class="rank-tier">{{ flexRankInfo?.tier || '배치 전' }}</div>
            <div v-if="flexRankInfo?.tier" class="rank-lp">
              {{ flexRankInfo?.leaguePoints }} LP
            </div>
          </div>
        </div>
        <div v-if="flexRankInfo?.tier" class="rank-record">
          {{ flexRankInfo?.wins }}승 {{ flexRankInfo?.losses }}패
          <span class="win-rate">({{ calculateWinRate(flexRankInfo?.wins, flexRankInfo?.losses) }}%)</span>
        </div>
      </div>
    </div>

    <!-- 통계 토글 버튼 -->
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
        <div class="tags-section">
          <div class="stats-title">자주 받은 태그</div>
          <div v-if="frequentTags && frequentTags.length > 0" class="tags-slider">
            <TagList :tags="frequentTags" size="medium" is-show-count/>
          </div>
          <div v-else class="no-review-message">
            받은 태그가 없습니다.
          </div>
        </div>
        <ReviewRolling :reviews="recentReviews" title="최근 받은 평가" wrapper-height="90px"/>
      </div>
    </Transition>

    <div v-if="!recentReviews.length" class="no-review-message">
      아직 작성된 리뷰가 없습니다.
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, computed, onMounted} from 'vue'
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
import type { LolMatchInfoRes } from "@/types/match.ts";
import { useAuthStore } from "@/stores/auth.ts";
import type {LolSummonerLeagueEntity} from "@/types/league.ts";
import {useImageUrl} from "@/utils/imageUtil.ts";

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
  playTogetherMatch: LolMatchInfoRes | null
}>()

defineEmits<{
  (e: 'showDetail'): void
  (e: 'updateMatches'): void
  (e: 'openPreviousModal'): void
  (e: 'openReviewModal', player: any): void
}>()

const { getTierImage } = useImageUrl();

const isExpanded = ref(true)
const soloRankInfo = ref<LolSummonerLeagueEntity>();
const flexRankInfo = ref<LolSummonerLeagueEntity>();

const toggleExpanded = () => {
  isExpanded.value = !isExpanded.value
}

const calculateWinRate = (wins: number, losses: number) => {
  const total = wins + losses;
  if (total === 0) return 0;
  return Math.round((wins / total) * 100);
}

onMounted(() => {
  if (props.profileData.leagueEntityList.length > 0) {
    props.profileData.leagueEntityList.forEach(league => {
      if (league.queueType === 'RANKED_SOLO_5x5') soloRankInfo.value = league;
      if (league.queueType === 'RANKED_FLEX_SR') flexRankInfo.value = league;
    })
  }
});

</script>

<style scoped>
.profile-section {
  background: #141414;
  border-radius: 8px;
  padding: 20px;
}

.rank-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
  margin-top: 24px
}

.rank-card {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: all 0.2s ease;
}

.rank-card:hover {
  background: rgba(255, 255, 255, 0.05);
}

.rank-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.rank-emblem {
  width: 72px;
  height: 72px;
  object-fit: contain;
}

.rank-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.rank-type {
  color: #9e9e9e;
  font-size: 13px;
}

.rank-tier {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
}

.rank-lp {
  color: #2979FF;
  font-size: 14px;
  font-weight: 500;
}

.rank-record {
  color: #e0e0e0;
  font-size: 14px;
  padding-left: 88px;
}

.win-rate {
  color: #2979FF;
  margin-left: 4px;
}

/* 기존 스타일 유지 */
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
  padding-top: 20px;
}

.stats-section, .tags-section {
  margin-bottom: 24px;
}

.stats-title {
  font-size: 15px;
  font-weight: 500;
  color: #fff;
  margin-bottom: 16px;
}

.tags-slider {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  padding: 16px;
}

.no-review-message {
  color: #9e9e9e;
  text-align: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  margin-top: 20px;
}

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
  gap: 8px;
}

.highlight-name {
  color: #2979FF;
  font-weight: 600;
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

@media (max-width: 768px) {
  .rank-section {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .rank-emblem {
    width: 56px;
    height: 56px;
  }

  .rank-record {
    padding-left: 72px;
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