<!-- src/components/summoner/MatchTeam.vue -->
<template>
  <div class="team">
    <div class="team-title" :class="teamType">
      {{ teamType === 'blue' ? '아군 팀' : '상대 팀' }}
    </div>
    <div class="team-members">
      <div v-for="player in participants"
           :key="player.puuid"
           class="player"
           :class="teamType"
           @click="$emit('reviewPlayer', player)">
        <div class="champion-wrapper">
          <div class="champion-icon">
            <img :src="player.champProfileIconUrl" :alt="player.championName">
            <div class="champion-level">{{ player.champLevel }}</div>
          </div>
        </div>
        <div class="player-info">
          <div class="player-top-row">
            <div class="player-name">{{ player.riotIdGameName }}</div>
            <div class="kda-stats">
              <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span>
              <span class="separator">/</span>
              <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span>
              <span class="separator">/</span>
              <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
            </div>
          </div>
          <div class="damage-bars">
            <damage-bar
                :value="player.totalDamageToChampion"
                :max-value="maxDamage"
                type="damage"
            />
            <damage-bar
                :value="player.totalDamageTaken"
                :max-value="maxDamageTaken"
                type="taken"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { LolMatchParticipant } from '@/types/match'
import DamageBar from './DamageBar.vue'
import { computed } from 'vue'

const props = defineProps<{
  participants: LolMatchParticipant[]
  teamType: 'blue' | 'red'
}>()

const maxDamage = computed(() =>
    Math.max(...props.participants.map(p => p.totalDamageToChampion))
)

const maxDamageTaken = computed(() =>
    Math.max(...props.participants.map(p => p.totalDamageTaken))
)

const calculateKDA = (player: LolMatchParticipant) => {
  return player.deaths === 0
      ? player.kills + player.assists
      : (player.kills + player.assists) / player.deaths
}
</script>

<style scoped>
.team {
  width: 100%;
}

.team-title {
  font-weight: 600;
  font-size: 13px;
  margin-bottom: 8px;
  padding-left: 4px;
  letter-spacing: 0.5px;
}

.team-title.blue {
  color: #2979FF;
}

.team-title.red {
  color: #FF5252;
}

.team-members {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.player {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: rgba(255, 255, 255, 0.02);
}

.player:hover {
  background: rgba(255, 255, 255, 0.04);
}

.champion-wrapper {
  position: relative;
  flex-shrink: 0;
}

.champion-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  overflow: hidden;
  background: #1a1a1a;
  position: relative;
}

.champion-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.champion-level {
  position: absolute;
  bottom: -2px;
  right: -2px;
  background: #000;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 3px;
  padding: 0px 3px;
  font-size: 10px;
  color: #fff;
}

.player-info {
  flex: 1;
  min-width: 0;
}

.player-top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2px;
}

.player-name {
  font-size: 13px;
  font-weight: 500;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

.kda-stats {
  font-size: 13px;
  color: #888;
  white-space: nowrap;
}

.kda-stats .separator {
  color: #555;
  margin: 0 2px;
}

.kda-stats .highlight {
  color: #4CAF50;
}

.kda-stats .highlight-death {
  color: #FF5252;
}

.damage-bars {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

@media (max-width: 768px) {
  .player {
    padding: 4px 6px;
  }

  .champion-icon {
    width: 28px;
    height: 28px;
  }
}
</style>