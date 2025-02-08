<template>
  <div class="team">
    <!-- PC/태블릿 레이아웃 -->
    <div class="hidden md:block">
      <div class="team-title" :class="[teamType, participants[0]?.isWin ? 'win' : 'lose']">
        {{ teamType === 'blue' ? '블루팀' : '레드팀' }}
        <span class="result-text">{{ participants[0]?.isWin ? '승리' : '패배' }}</span>
      </div>
      <div class="team-members">
        <div v-for="player in participants"
             :key="player.puuid"
             class="player"
             :class="[
               teamType,
               auth.user?.puuid != profileData.riotAccountInfoEntity.puuid
               ? 'someone-else'
               : {
                 'reviewed': !player.reviewDto?.reviewable,
                 'not-reviewed': player.reviewDto?.reviewable && player.puuid !== auth.user?.puuid,
                 'self-profile': player.puuid === auth.user?.puuid,
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
              <div class="player-name" @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
                {{ player.riotIdGameName }}
              </div>
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

    <!-- 모바일 레이아웃 -->
    <div class="md:hidden">
      <template v-if="teamType === 'all'">
        <!-- 블루팀 -->
        <div class="mobile-team-title blue">
          블루팀
          <span class="mobile-result-text">{{ participants.find(p => p.teamId === 100)?.isWin ? '승리' : '패배' }}</span>
        </div>
        <div class="mobile-team-members">
          <div v-for="player in participants.filter(p => p.teamId === 100)"
               :key="player.puuid"
               class="mobile-player"
               :class="[
                 auth.user?.puuid != profileData.riotAccountInfoEntity.puuid
                 ? 'mobile-someone-else'
                 : {
                   'mobile-reviewed': !player.reviewDto?.reviewable,
                   'mobile-not-reviewed': player.reviewDto?.reviewable && player.puuid !== auth.user?.puuid,
                   'mobile-self-profile': player.puuid === auth.user?.puuid,
                 }
               ]"
               @click="handlePlayerClick(player)"
          >
            <!-- Top row: Player info and review scores -->
            <div class="mobile-top-row">
              <div class="mobile-main-info">
                <div class="mobile-champion-wrapper">
                  <div class="mobile-champion-icon">
                    <img :src="player.champProfileIconUrl" :alt="player.champName">
                  </div>
                  <div class="mobile-champion-level">{{ player.champLevel }}</div>
                </div>
                <div class="mobile-player-info">
                  <div class="mobile-name-tag">
                    <span class="mobile-name" @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
                      {{ player.riotIdGameName }}
                    </span>
                    <span class="mobile-tag">#{{ player.riotIdTagline }}</span>
                  </div>
                  <div class="mobile-kda">
                    <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                    <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                    <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
                  </div>
                </div>
              </div>
              <div class="mobile-review-scores" v-if="player.reviewStatsDto">
                <div class="mobile-review-up">
                  <ThumbsUp class="mobile-thumb-icon up" />
                  <span class="mobile-count">{{ player.reviewStatsDto.upCount }}</span>
                </div>
                <div class="mobile-review-down">
                  <ThumbsDown class="mobile-thumb-icon down" />
                  <span class="mobile-count">{{ player.reviewStatsDto.downCount }}</span>
                </div>
              </div>
            </div>

            <!-- Bottom row: Damage bar -->
            <div class="mobile-bottom-row">
              <div class="mobile-damage-bar">
                <damage-bar
                    :value="player.totalDamageToChampion"
                    :max-value="maxDamage"
                    type="damage"
                    label="챔피언에게 가한 피해량"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- 레드팀 -->
        <div class="mobile-team-title red">
          레드팀
          <span class="mobile-result-text">{{ participants.find(p => p.teamId === 200)?.isWin ? '승리' : '패배' }}</span>
        </div>
        <div class="mobile-team-members">
          <div v-for="player in participants.filter(p => p.teamId === 200)"
               :key="player.puuid"
               class="mobile-player"
               :class="[
                 auth.user?.puuid != profileData.riotAccountInfoEntity.puuid
                 ? 'mobile-someone-else'
                 : {
                   'mobile-reviewed': !player.reviewDto?.reviewable,
                   'mobile-not-reviewed': player.reviewDto?.reviewable && player.puuid !== auth.user?.puuid,
                   'mobile-self-profile': player.puuid === auth.user?.puuid,
                 }
               ]"
               @click="handlePlayerClick(player)"
          >
            <!-- Top row -->
            <div class="mobile-top-row">
              <div class="mobile-main-info">
                <div class="mobile-champion-wrapper">
                  <div class="mobile-champion-icon">
                    <img :src="player.champProfileIconUrl" :alt="player.champName">
                  </div>
                  <div class="mobile-champion-level">{{ player.champLevel }}</div>
                </div>
                <div class="mobile-player-info">
                  <div class="mobile-name-tag">
                    <span class="mobile-name" @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
                      {{ player.riotIdGameName }}
                    </span>
                    <span class="mobile-tag">#{{ player.riotIdTagline }}</span>
                  </div>
                  <div class="mobile-kda">
                    <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                    <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                    <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
                  </div>
                </div>
              </div>
              <div class="mobile-review-scores" v-if="player.reviewStatsDto">
                <div class="mobile-review-up">
                  <ThumbsUp class="mobile-thumb-icon up" />
                  <span class="mobile-count">{{ player.reviewStatsDto.upCount }}</span>
                </div>
                <div class="mobile-review-down">
                  <ThumbsDown class="mobile-thumb-icon down" />
                  <span class="mobile-count">{{ player.reviewStatsDto.downCount }}</span>
                </div>
              </div>
            </div>

            <!-- Bottom row -->
            <div class="mobile-bottom-row">
              <div class="mobile-damage-bar">
                <damage-bar
                    :value="player.totalDamageToChampion"
                    :max-value="maxDamage"
                    type="damage"
                    label="챔피언에게 가한 피해량"
                />
              </div>
            </div>
          </div>
        </div>
      </template>
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
import {goSelectedSummonerProfile} from "@/utils/common.ts";


const props = defineProps<{
  profileData: LolSummonerProfileResDto
  participants: LolMatchParticipant[]
  allParticipants: LolMatchParticipant[]
  teamType: 'blue' | 'red' | 'all'
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

const calculateRatio = (upCount: number, total: number) => {
  if (total === 0) return 0
  return Math.round((upCount / total) * 100)
}

</script>

<style scoped>
.team {
  width: 100%;
}

/* PC/태블릿 스타일 */
.team-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 12px;
  padding-left: 8px;
  letter-spacing: 0.5px;
}

.team-title.win {
  color: #4CAF50;
}

.team-title.lose {
  color: #FF5252;
}

.team-title.blue {
  color: #2979FF;
}

.team-title.red {
  color: #FF5252;
}

.result-text {
  font-size: 12px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.1);
}

.win .result-text {
  background: rgba(76, 175, 80, 0.2);
}

.lose .result-text {
  background: rgba(255, 82, 82, 0.2);
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

.player.someone-else {
  background: rgba(255, 255, 255, 0.02);
  opacity: 0.8;
  cursor: default;
}

.player.not-reviewed {
  background: rgba(41, 121, 255, 0.08);
  border: 2px solid rgba(41, 121, 255, 0.2);
  box-shadow: 0 0 8px rgba(41, 121, 255, 0.1);
  cursor: pointer;
}

.player.not-reviewed:hover,
.player.reviewed:hover:not(.self-profile):hover {
  transform: translateX(4px);
  border-color: rgba(41, 121, 255, 0.4);
  background: rgba(41, 121, 255, 0.12);
}

.player.reviewed:not(.self-profile):hover {
  background: rgba(76, 175, 80, 0.05);
  border: 2px solid rgba(76, 175, 80, 0.1);
  opacity: 0.9;
  cursor: pointer;
}

.player.self-profile {
  background: rgba(255, 255, 255, 0.02);
  border: 2px solid rgba(255, 255, 255, 0.05);
  opacity: 0.8;
  cursor: default;
}

/* 기존 PC/태블릿 스타일 유지 */
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

.review-up,
.review-down {
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

/* 모바일 스타일 */
@media (max-width: 768px) {
  .mobile-team-title {
    font-size: 12px;
    font-weight: 600;
    padding: 8px 6px;
    display: flex;
    align-items: center;
    gap: 4px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }

  .mobile-player {
    display: flex;
    flex-direction: column;
    padding: 8px 6px;
    background: rgba(255, 255, 255, 0.04);
    border-bottom: 1px solid rgba(255, 255, 255, 0.03);
    gap: 6px;
  }

  .mobile-top-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }

  .mobile-bottom-row {
    width: 100%;
    padding: 0 4px;
  }

  .mobile-main-info {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;
    min-width: 0;
  }

  .mobile-champion-wrapper {
    position: relative;
    flex-shrink: 0;
  }

  .mobile-champion-icon {
    width: 28px;
    height: 28px;
    border-radius: 4px;
    overflow: hidden;
  }

  .mobile-champion-icon img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .mobile-champion-level {
    position: absolute;
    bottom: -2px;
    right: -2px;
    background: #000;
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 3px;
    padding: 0px 2px;
    font-size: 9px;
    font-weight: 600;
  }

  .mobile-player-info {
    min-width: 0;
    flex: 1;
  }

  .mobile-name-tag {
    display: flex;
    align-items: center;
    gap: 4px;
    min-width: 0;
  }

  .mobile-name {
    font-size: 12px;
    font-weight: 500;
    color: #fff;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .mobile-tag {
    font-size: 10px;
    color: #666;
    flex-shrink: 0;
  }

  .mobile-kda {
    font-size: 11px;
    color: #888;
  }

  .mobile-kda .highlight {
    color: #4CAF50;
  }

  .mobile-kda .highlight-death {
    color: #FF5252;
  }

  .mobile-damage-bar {
    width: 100%;
    height: 4px;
  }

  .mobile-review-scores {
    display: flex;
    gap: 8px;
    padding-left: 8px;
    flex-shrink: 0;
  }

  .mobile-review-up,
  .mobile-review-down {
    display: flex;
    align-items: center;
    gap: 2px;
    min-width: 32px;
    justify-content: flex-end;
  }

  .mobile-thumb-icon {
    width: 12px;
    height: 12px;
  }

  .mobile-thumb-icon.up {
    color: #4CAF50;
  }

  .mobile-thumb-icon.down {
    color: #FF5252;
  }

  .mobile-count {
    font-size: 11px;
    font-weight: 600;
  }

  .mobile-review-up .mobile-count {
    color: #4CAF50;
  }

  .mobile-review-down .mobile-count {
    color: #FF5252;
  }

  /* 팀 구분 스타일 */
  .mobile-team-title.blue {
    color: #2979FF;
  }

  .mobile-team-title.red {
    color: #FF5252;
    margin-top: 8px;
  }

  .win .mobile-result-text {
    background: rgba(76, 175, 80, 0.2);
    padding: 1px 4px;
    border-radius: 2px;
    font-size: 10px;
  }

  .lose .mobile-result-text {
    background: rgba(255, 82, 82, 0.2);
    padding: 1px 4px;
    border-radius: 2px;
    font-size: 10px;
  }

  .mobile-player.mobile-someone-else {
    background: rgba(255, 255, 255, 0.02);
    opacity: 0.8;
    cursor: default;
  }

  .mobile-player.mobile-not-reviewed {
    background: rgba(41, 121, 255, 0.08);
    border: 1px solid rgba(41, 121, 255, 0.2);
    cursor: pointer;
  }

  .mobile-player.mobile-reviewed:not(.mobile-self-profile) {
    background: rgba(76, 175, 80, 0.05);
    border: 1px solid rgba(76, 175, 80, 0.1);
    cursor: pointer;
  }

  .mobile-player.mobile-self-profile {
    background: rgba(255, 255, 255, 0.02);
    border: 1px solid rgba(255, 255, 255, 0.05);
    opacity: 0.8;
    cursor: default;
  }
}
</style>