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
        <div class="score-section">
          <span class="score-label">평가점수</span>
          <HelpTooltip
            message="신뢰도 95%의 윌슨 스코어로 계산된 평가 점수입니다.
            긍정적인 평가와 부정적인 평가의 비율, 그리고 전체 평가 수를 고려하여
            더 신뢰할 수 있는 점수를 계산합니다."
          />
          <span class="score-value">{{ reviewStats?.score ?? 0 }}</span><span class="score-label">점</span>
        </div>
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
      <!-- 솔로 랭크 카드 -->
      <div class="rank-card">
        <div class="rank-header">
          <img :src="soloRankInfo?.tier ? getTierImage(soloRankInfo?.tier) : getTierImage(`unranked`)"
               alt="개인 랭크"
               class="rank-emblem"/>
          <div class="rank-info">
            <div class="rank-type">개인 랭크</div>
            <div class="rank-tier">{{ soloRankInfo?.tier || '배치 전' }}</div>
            <div v-if="soloRankInfo?.tier" class="rank-lp">
              {{ soloRankInfo?.leaguePoints }} LP
            </div>
            <div v-if="soloRankInfo?.tier" class="rank-record">
              {{ soloRankInfo?.wins }}승 {{ soloRankInfo?.losses }}패
              <span class="win-rate">({{ calculateWinRate(soloRankInfo?.wins, soloRankInfo?.losses) }}%)</span>
            </div>
          </div>

          <div class="rank-review-stats">
            <div class="thumbs-section">
              <div class="stat-group">
                <ThumbsUp class="thumb-icon up" />
                <span class="up-count">{{ soloRankInfo?.reviewDto?.upCount ?? 0 }}</span>
              </div>
              <div class="stat-group">
                <ThumbsDown class="thumb-icon down" />
                <span class="down-count">{{ soloRankInfo?.reviewDto?.downCount ?? 0}}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 자유 랭크 카드 -->
      <div class="rank-card">
        <div class="rank-header">
          <img :src="flexRankInfo?.tier ? getTierImage(flexRankInfo?.tier) : getTierImage(`unranked`)"
               alt="자유 랭크"
               class="rank-emblem"/>
          <div class="rank-info">
            <div class="rank-type">자유 랭크</div>
            <div class="rank-tier">{{ flexRankInfo?.tier || '배치 전' }}</div>
            <div v-if="flexRankInfo?.tier" class="rank-lp">
              {{ flexRankInfo?.leaguePoints }} LP
            </div>
            <div v-if="flexRankInfo?.tier" class="rank-record">
              {{ flexRankInfo?.wins }}승 {{ flexRankInfo?.losses }}패
              <span class="win-rate">({{ calculateWinRate(flexRankInfo?.wins, flexRankInfo?.losses) }}%)</span>
            </div>
          </div>

          <div class="rank-review-stats">
            <div class="thumbs-section">
              <div class="stat-group">
                <ThumbsUp class="thumb-icon up" />
                <span class="up-count">{{ flexRankInfo?.reviewDto?.upCount ?? 0 }}</span>
              </div>
              <div class="stat-group">
                <ThumbsDown class="thumb-icon down" />
                <span class="down-count">{{ flexRankInfo?.reviewDto?.downCount ?? 0 }}</span>
              </div>
            </div>
          </div>
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
import { ref, computed, onMounted } from 'vue'
import type { LolSummonerProfileResDto } from '@/types/summoner.ts'
import type {
  ReviewRatingByChampDto,
  ReviewRatingByPositionDto,
  ReviewRequestDto,
  ReviewStatsDto,
  ReviewTagDto
} from "@/types/review.ts"
import { ThumbsUp, ThumbsDown, ChevronDown, InfoIcon, HelpCircle } from 'lucide-vue-next'
import TagList from "@/components/common/TagList.vue"
import ReviewRolling from "@/components/review/ReviewRolling.vue"
import type { LolMatchInfoRes } from "@/types/match.ts"
import { useAuthStore } from "@/stores/auth.ts"
import type {LolSummonerLeagueDto, LolSummonerLeagueEntity} from "@/types/league.ts"
import { useImageUrl } from "@/utils/imageUtil.ts"
import HelpTooltip from "@/components/common/HelpTooltip.vue";

const authStore = useAuthStore()
const { getTierImage } = useImageUrl()

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

const isExpanded = ref(true)
const soloRankInfo = ref<LolSummonerLeagueDto>()
const flexRankInfo = ref<LolSummonerLeagueDto>()

// 툴팁 관련 상태 추가
const tooltipInfo = ref({
  show: false,
  type: '' // 'solo' 또는 'flex'
})
const tooltipStyle = ref({})

const calculateWinRate = (wins: number, losses: number) => {
  const total = wins + losses
  if (total === 0) return 0
  return Math.round((wins / total) * 100)
}

const showTooltip = (event: MouseEvent, type: string) => {
  const target = event.currentTarget as HTMLElement
  const rect = target.getBoundingClientRect()

  tooltipStyle.value = {
    top: `${rect.top - 10}px`,
    left: `${rect.left + (rect.width / 2)}px`
  }

  tooltipInfo.value = {
    show: true,
    type
  }
}

const hideTooltip = () => {
  tooltipInfo.value.show = false
}

const toggleExpanded = () => {
  isExpanded.value = !isExpanded.value
}

onMounted(() => {
  if (props.profileData.leagueEntityList.length > 0) {
    props.profileData.leagueEntityList.forEach(league => {
      if (league.queueType === 'RANKED_SOLO_5x5') soloRankInfo.value = league
      if (league.queueType === 'RANKED_FLEX_SR') flexRankInfo.value = league
    })
  }
})
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
  width: 16px;
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
  font-weight: 600;
  font-size: 14px;
}

.down-count {
  color: #FF5252;
  font-weight: 600;
  font-size: 14px;
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
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 90px;
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
  width: 16px;
  height: 16px;
  border: 2px solid #ffffff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.rank-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin: 20px 0;
}

.rank-card {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  padding: 12px;
  transition: all 0.2s ease;
}

.rank-card:hover {
  background: rgba(255, 255, 255, 0.05);
}

.rank-header {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
  min-height: 90px;
}

.rank-emblem {
  width: 48px;
  height: 48px;
  object-fit: contain;
  flex-shrink: 0;
}

.rank-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
  justify-content: center;
}

.rank-type {
  color: #9e9e9e;
  font-size: 11px;
}

.rank-tier {
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
}

.rank-lp {
  color: #2979FF;
  font-size: 12px;
  font-weight: 500;
}

.rank-record {
  color: #e0e0e0;
  font-size: 12px;
}

.win-rate {
  color: #2979FF;
  margin-left: 4px;
}

.rank-review-stats {
  position: absolute;
  right: 0;
  /*top: 0;*/
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-end;
  justify-content: center;
}

.score-section {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 2px;
}

.score-label {
  font-size: 12px;
  color: #9e9e9e;
}

.help-icon {
  color: #9e9e9e;
  cursor: help;
}

.score-value {
  font-size: 14px;
  font-weight: 600;
  color: #2979FF;
}

.thumbs-section {
  display: flex;
  gap: 8px;
}

.tooltip-wrapper {
  position: relative;
  display: inline-block;
}

.tooltip {
  position: fixed;
  transform: translate(-50%, -120%);
  background: #323232;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
  padding: 6px 10px;
  color: white;
  font-size: 11px;
  border-radius: 4px;
  white-space: pre-line;
  min-width: 140px;
  max-width: 200px;
  z-index: 9999;
  pointer-events: none;
  line-height: 1.4;
}

.tooltip-arrow {
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%) rotate(45deg);
  width: 8px;
  height: 8px;
  background: #323232;
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

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .profile-section {
    padding: 16px;
  }

  .main-profile {
    grid-template-columns: auto 1fr;
    gap: 16px;
  }

  .button-group {
    grid-column: span 2;
    margin-top: 16px;
  }

  .rank-section {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .rank-review-stats {
    position: relative;
    margin-top: 6px;
    padding-left: 60px;
    align-items: flex-start;
  }
}

@media (max-width: 360px) {
  .profile-section {
    padding: 12px;
  }

  .rank-card {
    padding: 10px;
  }

  .rank-header {
    min-height: 40px; /* 모바일에서 티어 아이콘 크기에 맞춤 */
  }

  .score-section {
    font-size: 11px;
  }

  .score-value {
    font-size: 13px;
  }

  .rank-emblem {
    width: 40px;
    height: 40px;
  }

  .profile-icon {
    width: 60px;
    height: 60px;
  }

  .summoner-name {
    font-size: 16px;
  }

  .profile-stats {
    font-size: 11px;
  }

  .tooltip {
    min-width: 120px;
    max-width: 180px;
    font-size: 10px;
    padding: 6px 8px;
  }
}
</style>