<!-- src/components/summoner/MatchList.vue -->
<template>
  <div class="games-section">
    <!--    <div class="tab-container">-->
    <!--      <button-->
    <!--          v-for="tab in tabs"-->
    <!--          :key="tab.value"-->
    <!--          class="tab-button"-->
    <!--          :class="{ active: selectedTab === tab.value }"-->
    <!--          @click="selectedTab = tab.value"-->
    <!--      >-->
    <!--        {{ tab.label }}-->
    <!--      </button>-->
    <!--    </div>-->

    <div v-for="match in filteredMatches" :key="match.matchInfo.matchId" class="game-item">
      <div class="game-header">
        <div class="game-info">
          <div class="game-type">{{ match.matchInfo.gameModeName }}</div>
          <div class="game-meta">
            <span class="game-date">{{ formatDate(match.matchInfo.gameStartDt) }}</span>
            <span class="separator">•</span>
            <span class="game-duration">{{ formatDuration(match.matchInfo.gameDuration) }}</span>
          </div>
        </div>
      </div>
      <div class="teams-container">
        <match-team
            :participants="match.participantList.filter(p => p.teamId === 100)"
            :all-participants="match.participantList"
            :team-type="'blue'"
            :profile-data="profileData"
            @review-player="openReview"
        />
        <div class="teams-divider"></div>
        <match-team
            :participants="match.participantList.filter(p => p.teamId === 200)"
            :all-participants="match.participantList"
            :team-type="'red'"
            :profile-data="profileData"
            @review-player="openReview"
        />
      </div>
    </div>

    <!-- 더보기 버튼 -->
    <div v-if="!noMoreMatches" class="load-more-container">
      <button
          class="load-more-button"
          @click="$emit('loadMore')"
          :disabled="isLoading"
      >
        <div class="button-content">
          <span v-if="!isLoading">더보기</span>
          <div v-else class="spinner"></div>
        </div>
      </button>
    </div>

    <!-- 더 이상 데이터가 없을 때 표시 -->
    <div v-if="noMoreMatches" class="no-more-matches">
      더 이상 매치 기록이 없습니다
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { LolMatchInfoRes } from '@/types/match'
import MatchTeam from './MatchTeam.vue'
import type { LolSummonerProfileResDto } from "@/types/summoner.ts"
import { useAuthStore } from "@/stores/auth"

const auth = useAuthStore()
const selectedTab = ref('all')

const props = defineProps<{
  matches: LolMatchInfoRes[]
  profileData: LolSummonerProfileResDto
  isLoading: boolean
  noMoreMatches: boolean
}>()

const emit = defineEmits<{
  (e: 'reviewPlayer', player: any): void
  (e: 'loadMore'): void
}>()

const filteredMatches = computed(() => {
  switch (selectedTab.value) {
    case 'reviewed':
      return props.matches.filter(match =>
          match.participantList.some(player =>
              !player.reviewDto?.reviewable &&
              player.puuid !== auth.user?.puuid
          )
      )
    case 'pending':
      return props.matches.filter(match =>
          match.participantList.some(player =>
              player.reviewDto?.reviewable &&
              player.puuid !== auth.user?.puuid
          )
      )
    default:
      return props.matches
  }
})

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString()
}

const formatDuration = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

const openReview = (player: any) => {
  emit('reviewPlayer', player)
}
</script>

<style scoped>
.games-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px 0;
}

.tab-container {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.tab-button {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #999;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
}

.tab-button:hover {
  background: rgba(41, 121, 255, 0.1);
  border-color: rgba(41, 121, 255, 0.2);
  color: #fff;
}

.tab-button.active {
  background: rgba(41, 121, 255, 0.2);
  border-color: #2979FF;
  color: #fff;
}

.game-item {
  background: #141414;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: transform 0.2s ease, border-color 0.2s ease;
}

.game-item:hover {
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.1);
}

.game-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.game-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.game-type {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.game-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.separator {
  color: #444;
}

.teams-container {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 24px;
  align-items: start;
}

.teams-divider {
  width: 1px;
  height: 100%;
  background: linear-gradient(
      to bottom,
      transparent,
      rgba(255, 255, 255, 0.08) 20%,
      rgba(255, 255, 255, 0.08) 80%,
      transparent
  );
  margin: 0 8px;
}

/*.load-more-container {
  margin-top: 12px;
}*/

.load-more-button {
  background: #141414;
  border: 1px solid #2979FF;
  color: #2979FF;
  padding: 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
  height: 48px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.load-more-button:hover:not(:disabled) {
  background: rgba(41, 121, 255, 0.1);
}

.load-more-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  border-color: #4a4a4a;
  color: #4a4a4a;
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
  border: 2px solid #2979FF;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.no-more-matches {
  text-align: center;
  padding: 20px;
  color: #666;
  font-size: 14px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 768px) {
  .tab-container {
    overflow-x: auto;
    padding-bottom: 8px;
  }

  .tab-button {
    white-space: nowrap;
  }

  .game-item {
    padding: 16px;
  }

  .teams-container {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .teams-divider {
    width: 100%;
    height: 1px;
    margin: 8px 0;
  }
}
</style>