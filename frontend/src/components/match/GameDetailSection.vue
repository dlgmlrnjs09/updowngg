<!-- src/components/match/GameDetailSection.vue -->
<template>
  <div class="game-details" :class="{ 'open': isOpen }">
    <div class="game-info">
      <div class="game-meta">
        <div class="game-type">{{ match.matchInfo.gameModeName }}</div>
        <div class="game-date">
          {{ formatDate(match.matchInfo.gameStartDt) }}
          <span class="bullet">•</span>
          {{ formatDuration(match.matchInfo.gameDuration) }}
        </div>
      </div>
    </div>

    <div class="teams-container">
      <!-- Blue Team -->
      <div class="team blue-team">
        <div class="team-header">블루팀</div>
        <div class="team-players">
          <div v-for="player in blueTeam"
               :key="player.puuid"
               class="player-card"
               :class="{
                  'reviewed': player.puuid === reviewedPuuid,
                  'me': player.puuid === currentUserPuuid
               }"
          >
            <div class="player-champion">
              <div class="champion-portrait">
                <img :src="player.champProfileIconUrl" :alt="player.champName">
                <div class="champion-level">{{ player.champLevel }}</div>
              </div>
            </div>
            <div class="player-info">
              <div class="player-identity">
                <span class="name">{{ player.riotIdGameName }}</span>
                <span class="tagline">#{{ player.riotIdTagline }}</span>
              </div>
              <div class="player-stats">
                <div class="kda">
                  <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                  <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                  <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
                </div>
                <div v-if="calculateKDA(player) > 0" class="kda-ratio">
                  {{ calculateKDA(player).toFixed(2) }} KDA
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Red Team -->
      <div class="team red-team">
        <div class="team-header">레드팀</div>
        <div class="team-players">
          <div v-for="player in redTeam"
               :key="player.puuid"
               class="player-card"
               :class="{
                  'reviewed': player.puuid === reviewedPuuid,
                  'me': player.puuid === currentUserPuuid
               }"
          >
            <div class="player-champion">
              <div class="champion-portrait">
                <img :src="player.champProfileIconUrl" :alt="player.champName">
                <div class="champion-level">{{ player.champLevel }}</div>
              </div>
            </div>
            <div class="player-info">
              <div class="player-identity">
                <span class="name">{{ player.riotIdGameName }}</span>
                <span class="tagline">#{{ player.riotIdTagline }}</span>
              </div>
              <div class="player-stats">
                <div class="kda">
                  <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                  <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                  <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
                </div>
                <div v-if="calculateKDA(player) > 0" class="kda-ratio">
                  {{ calculateKDA(player).toFixed(2) }} KDA
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { LolMatchInfoRes, LolMatchParticipant } from '@/types/match.ts'

const props = defineProps<{
  match: LolMatchInfoRes
  isOpen: boolean
  reviewedPuuid: string
  currentUserPuuid: string
}>()

const blueTeam = computed(() =>
    props.match?.participantList.filter(p => p.teamId === 100)
)

const redTeam = computed(() =>
    props.match?.participantList.filter(p => p.teamId === 200)
)

const calculateKDA = (player: LolMatchParticipant) => {
  return player.deaths === 0
      ? player.kills + player.assists
      : (player.kills + player.assists) / player.deaths
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString()
}

const formatDuration = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}
</script>

<style scoped>
.game-details {
  max-height: none;
  height: auto;
  overflow: visible;
  background: rgba(255, 255, 255, 0.02);
  display: none;
}

.game-details.open {
  display: block;
}

.game-info {
  padding: 24px 24px 0;
}

.game-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.game-type {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  padding: 6px 12px;
  border-radius: 6px;
}

.game-date {
  color: #888;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.bullet {
  color: #444;
}

.teams-container {
  padding: 24px;
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  height: auto;
}

.team {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  padding: 20px;
}

.team-header {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-left: 8px;
}

.blue-team .team-header {
  color: #2979FF;
}

.red-team .team-header {
  color: #FF5252;
}

.team-players {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.player-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background: rgba(30, 30, 30, 0.95);
  transition: all 0.2s ease;
}

.player-card.reviewed {
  background: rgba(76, 175, 80, 0.1);
  border: 1px solid rgba(76, 175, 80, 0.2);
}

.player-card.me {
  background: rgba(41, 121, 255, 0.1);
  border: 1px solid rgba(41, 121, 255, 0.2);
}

.player-champion {
  position: relative;
}

.champion-portrait {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  overflow: hidden;
  background: #000;
  border: 2px solid rgba(255, 255, 255, 0.1);
}

.champion-portrait img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.champion-level {
  position: absolute;
  bottom: -4px;
  right: -4px;
  background: #000;
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  padding: 1px 4px;
  font-size: 11px;
  font-weight: 600;
}

.player-info {
  flex: 1;
  min-width: 0;
}

.player-identity {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.player-identity .name {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
}

.player-identity .tagline {
  font-size: 12px;
  color: #666;
}

.player-stats {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  justify-content: space-between;
}

.kda {
  font-size: 14px;
  color: #888;
  white-space: nowrap;
}

.kda .highlight {
  color: #4CAF50;
}

.kda .highlight-death {
  color: #FF5252;
}

.kda-ratio {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
}

@media (min-width: 768px) {
  .teams-container {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 640px) {
  .game-info {
    padding: 16px 16px 0;
  }

  .teams-container {
    padding: 16px;
  }

  .player-card {
    padding: 8px;
  }
}
</style>