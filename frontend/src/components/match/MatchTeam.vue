<template>
  <div class="team">
    <div class="team-title" :class="teamType">
      {{ teamType === 'blue' ? '블루팀' : '레드팀' }}
    </div>
    <div class="team-members">
      <div v-for="player in participants"
           :key="player.puuid"
           class="player"
           :class="[
           teamType,
           auth.user?.puuid != profileData.riotAccountInfoEntity.puuid
           ? 'someone-else' // 다른사람의 프로필은 선택 불가능
             : {
               'reviewed': !player.reviewDto?.reviewable,  // 리뷰 작성한 경우
               'not-reviewed': player.reviewDto?.reviewable && player.puuid !== auth.user?.puuid,  // 리뷰 작성하지 않은 경우
               'self-profile': player.puuid === auth.user?.puuid,  // 본인인 경우
             }
           ]"
           @click="handlePlayerClick(player)"
      >
        <div class="review-scores" v-if="player.reviewStatsDto">
          <div class="review-counts">
            <div class="review-up">
              <ThumbsUp class="thumb-icon up" />
              <span class="count">{{ player.reviewStatsDto.upCount }}</span>
            </div>
            <div class="review-down">
              <ThumbsDown class="thumb-icon down" />
              <span class="count">{{ player.reviewStatsDto.downCount }}</span>
            </div>
          </div>
          <div class="review-ratio" v-if="player.reviewStatsDto.totalReviewCnt > 0">
            {{ calculateRatio(player.reviewStatsDto.upCount, player.reviewStatsDto.totalReviewCnt) }}%
          </div>
        </div>
        <div class="champion-wrapper">
          <div class="champion-icon">
            <img :src="player.champProfileIconUrl" :alt="player.champName">
          </div>
          <div class="champion-level">{{ player.champLevel }}</div>
        </div>
        <div class="player-info">
          <div class="player-name-wrapper">
            <div class="player-name" @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">{{ player.riotIdGameName }}</div>
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
                KDA {{ calculateKDA(player).toFixed(1) }}
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
import {computed, onMounted} from 'vue'
import {useAuthStore} from "@/stores/auth.ts";
import type {LolSummonerProfileResDto} from "@/types/summoner.ts";
import {useRouter} from "vue-router";
import {getRatingColor} from "@/utils/ratingUtil.ts";
import {ThumbsDown, ThumbsUp} from "lucide-vue-next";
import {goSelectedSummonerProfile} from "@/common.ts";


const props = defineProps<{
  profileData: LolSummonerProfileResDto
  participants: LolMatchParticipant[]
  allParticipants: LolMatchParticipant[]
  teamType: 'blue' | 'red'
}>()

const emit = defineEmits<{
  (e: 'reviewPlayer', player: LolMatchParticipant): void
}>()

const auth = useAuthStore();
const router = useRouter();

const maxDamage = computed(() =>
    Math.max(...props.allParticipants.map(p => p.totalDamageToChampion))
)

const maxDamageTaken = computed(() =>
    Math.max(...props.allParticipants.map(p => p.totalDamageTaken))
)

const calculateKDA = (player: LolMatchParticipant) => {
  return player.deaths === 0
      ? player.kills + player.assists
      : (player.kills + player.assists) / player.deaths
}

const handlePlayerClick = (player:any) => {
  // 자기 자신인 경우
  if (auth.isAuthenticated && auth.user?.puuid == player.puuid) {
    return
  }

  // 다른 사람의 프로필인 경우
  if (auth.user?.puuid !== props.profileData.riotAccountInfoEntity.puuid) {
    return
  }

  emit('reviewPlayer', player)
}

// const goSelectedSummonerProfile = (player:any) => {
//   router.push({
//     name: 'summoner',
//     params: {
//       name: player.riotIdGameName,
//       tag: player.riotIdTagline,
//     }
//   })
// }

const calculateRatio = (upCount: number, total: number) => {
  if (total === 0) return 0
  return Math.round((upCount / total) * 100)
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
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.2s ease;
  background: rgba(255, 255, 255, 0.04);
  border: 2px solid transparent;
}

/* 다른 사람의 프로필 조회 시 */
.player.someone-else {
  background: rgba(255, 255, 255, 0.02);
  opacity: 0.8;
  cursor: default;
}

/* 리뷰 가능한 경우 (리뷰 작성하지 않은 소환사) */
.player.not-reviewed {
  background: rgba(41, 121, 255, 0.08);
  border: 2px solid rgba(41, 121, 255, 0.2);
  box-shadow: 0 0 8px rgba(41, 121, 255, 0.1);
  cursor: pointer;
}

.player.not-reviewed:hover, .player.reviewed:hover:not(.self-profile):hover {
  transform: translateX(4px);
  border-color: rgba(41, 121, 255, 0.4);
  background: rgba(41, 121, 255, 0.12);
}

/* 리뷰 작성한 경우 */
.player.reviewed:not(.self-profile):hover {
  background: rgba(76, 175, 80, 0.05);
  border: 2px solid rgba(76, 175, 80, 0.1);
  opacity: 0.9;
  cursor: pointer;
}

/* 본인인 경우 */
.player.self-profile {
  background: rgba(255, 255, 255, 0.02);
  border: 2px solid rgba(255, 255, 255, 0.05);
  opacity: 0.8;
  cursor: default;
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
  transition: all 0.2s ease;
  cursor: pointer;
}

.player-name:hover {
  color: #2979FF;
  text-decoration: underline;
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

.review-scores {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.review-counts {
  display: flex;
  gap: 12px;
}

.review-up, .review-down {
  display: flex;
  align-items: center;
  gap: 4px;
}

.thumb-icon {
  width: 14px;
  height: 14px;
}

.thumb-icon.up {
  color: #4CAF50;
}

.thumb-icon.down {
  color: #FF5252;
}

.count {
  font-size: 14px;
  font-weight: 600;
}

.review-up .count {
  color: #4CAF50;
}

.review-down .count {
  color: #FF5252;
}

.review-ratio {
  font-size: 12px;
  font-weight: 600;
  color: #2979FF;
}

.score-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  color: #666;
}

.score-value {
  font-size: 14px;
  font-weight: 600;
  color: #2979FF;
  margin-bottom: 2px;
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

  .review-scores {
    top: 8px;
    right: 8px;
  }

  .thumb-icon {
    width: 12px;
    height: 12px;
  }

  .count {
    font-size: 12px;
  }

  .review-ratio {
    font-size: 11px;
  }

  .score-item {
    font-size: 11px;
  }

  .score-value {
    font-size: 13px;
  }
}
</style>