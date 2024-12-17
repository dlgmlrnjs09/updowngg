<!-- src/components/summoner/MatchList.vue -->
<template>
  <div class="games-section">
    <div v-for="match in matches" :key="match.matchInfo.matchId" class="game-item">
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
  </div>
</template>

<script setup lang="ts">
// Script 부분은 동일하게 유지
import { ref } from 'vue'
import type { LolMatchInfoRes } from '@/types/match'
import MatchTeam from './MatchTeam.vue'
import type {LolSummonerProfileResDto} from "@/types/summoner.ts";

const props = defineProps<{
  matches: LolMatchInfoRes[]
  profileData: LolSummonerProfileResDto
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
  gap: 12px;
  padding: 16px 0;
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

@media (max-width: 768px) {
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