<template>
  <div class="games-section">
    <div v-for="match in matches" :key="match.matchInfo.matchId" class="game-item">
      <div class="game-header">
        <div class="game-type">{{ match.matchInfo.gameType }}</div>
        <div class="game-meta">
          <span class="game-duration">{{ formatDuration(match.matchInfo.gameDuration) }}</span>
          <span class="separator">•</span>
          <span class="game-date">{{ formatDate(match.matchInfo.gameStartDt) }}</span>
        </div>
      </div>
      <div class="teams-container">
        <match-team
            :participants="match.participantList.filter(p => p.teamId === 100)"
            :team-type="'blue'"
            @review-player="openReview"
        />
        <div class="teams-divider"></div>
        <match-team
            :participants="match.participantList.filter(p => p.teamId === 200)"
            :team-type="'red'"
            @review-player="openReview"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Script 부분은 동일하게 유지
import { ref } from 'vue'
import type { LolMatchInfoRes } from '@/types/match'
import MatchTeam from './MatchTeam.vue'

const props = defineProps<{
  matches: LolMatchInfoRes[]
}>()

const emit = defineEmits<{
  (e: 'reviewPlayer', player: any): void
}>()

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
  gap: 8px;
}

.game-item {
  background: #141414;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.game-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.game-type {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.game-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 12px;
}

.separator {
  color: #444;
}

.teams-container {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 16px;
  align-items: start;
}

.teams-divider {
  width: 1px;
  height: auto;
  background: rgba(255, 255, 255, 0.08);
  margin: 0 4px;
}

@media (max-width: 768px) {
  .game-item {
    padding: 8px;
  }

  .teams-container {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .teams-divider {
    display: none;
  }
}
</style>