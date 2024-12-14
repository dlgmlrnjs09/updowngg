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
          </div>
          <div class="champion-level">{{ player.champLevel }}</div>
        </div>
        <div class="player-info">
          <div class="player-name-wrapper">
            <div class="player-name">{{ player.riotIdGameName }}</div>
            <div class="player-tag">#{{ player.riotIdTagline }}</div>
          </div>
          <div class="player-stats">
            <div class="kda-wrapper">
              <span class="kda">
                <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
              </span>
              <span class="kda-ratio" v-if="calculateKDA(player) > 0">
                {{ calculateKDA(player).toFixed(1) }} KDA
              </span>
            </div>
            <div class="damage-bars">
              <damage-bar
                  :value="player.totalDamageToChampion"
                  :max-value="maxDamage"
                  type="damage"
                  label="챔피언에게 가한 피해량"
              />
              <damage-bar
                  :value="player.totalDamageTaken"
                  :max-value="maxDamageTaken"
                  type="taken"
                  label="받은 피해량"
              />
            </div>
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
  font-size: 14px;
  margin-bottom: 12px;
  padding-left: 8px;
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
  gap: 8px;
}

.player {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid transparent;
}

.player:hover {
  background: rgba(255, 255, 255, 0.04);
  border-color: rgba(255, 255, 255, 0.1);
  transform: translateX(4px);
}

.player.blue:hover {
  border-color: rgba(41, 121, 255, 0.2);
}

.player.red:hover {
  border-color: rgba(255, 82, 82, 0.2);
}

.champion-wrapper {
  position: relative;
}

.champion-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  overflow: hidden;
  background: #1a1a1a;
  border: 2px solid rgba(255, 255, 255, 0.1);
}

.champion-icon img {
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
  color: #fff;
}

.player-info {
  flex: 1;
  min-width: 0;
}

.player-name-wrapper {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.player-name {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.player-tag {
  font-size: 12px;
  color: #666;
}

.player-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.kda-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.kda {
  font-size: 14px;
  color: #888;
  font-weight: 500;
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
  font-weight: 500;
}

.damage-bars {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-right: 8px;
}

@media (max-width: 768px) {
  .player {
    padding: 8px;
  }

  .champion-icon {
    width: 32px;
    height: 32px;
  }

  .player-name {
    font-size: 13px;
  }
}
</style>