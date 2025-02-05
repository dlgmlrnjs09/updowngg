<!-- src/components/match/MatchSummary.vue -->
<template>
  <div
      class="match-container"
      :class="
        [
            isWin && !showDetails ? 'bg-blue-950/30' : '',
            !isWin && !showDetails ?  'bg-red-950/30' : '',
            showDetails ? 'bg-[#141414]' : ''
        ]">
    <div class="summary-section" @click="toggleDetails">
      <div class="match-summary flex items-center">
        <!-- 왼쪽: 게임 정보 -->
        <div class="game-info-section w-48 px-4">
          <div class="text-sm font-medium">{{ match.matchInfo.gameModeName }}</div>
          <div class="text-xs text-gray-400">{{ formatDate(match.matchInfo.gameStartDt) }}</div>
          <div class="text-xs" :class="isWin ? 'text-blue-400' : 'text-red-400'">
            {{ isWin ? '승리' : '패배' }}
          </div>
          <div class="text-xs text-gray-400">{{ formatDuration(match.matchInfo.gameDuration) }}</div>
        </div>

        <!-- 중앙: 챔피언/KDA 정보 -->
        <div class="champion-kda-section flex items-center gap-4 w-64">
          <div class="champion-info">
            <div class="relative">
              <img
                  :src="currentPlayer.champProfileIconUrl"
                  :alt="currentPlayer.champName"
                  class="w-12 h-12 rounded-lg"
              >
              <span class="absolute bottom-0 right-0 text-xs bg-black/80 px-1 rounded">
                {{ currentPlayer.champLevel }}
              </span>
            </div>
          </div>

          <div class="kda-info text-center">
            <div class="text-sm">
              <span class="text-gray-200">{{ currentPlayer.kills }}</span> /
              <span class="text-red-400">{{ currentPlayer.deaths }}</span> /
              <span class="text-gray-200">{{ currentPlayer.assists }}</span>
            </div>
            <div class="text-xs text-gray-400">
              {{ calculateKDA(currentPlayer) }} 평점
            </div>
          </div>
        </div>

        <!-- 오른쪽: 참가자 목록 -->
        <div class="participants-section">
          <div class="flex gap-4">
            <!-- 블루팀 -->
            <div class="team-column">
              <div v-for="player in team1"
                   :key="player.puuid"
                   class="flex items-center gap-1 mb-1">
                <img
                    :src="player.champProfileIconUrl"
                    :alt="player.champName"
                    class="w-4 h-4 rounded-sm"
                >
                <span class="text-xs text-gray-300 hover:text-blue-400 cursor-pointer truncate"
                      @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
                  {{ player.riotIdGameName }}
                </span>
              </div>
            </div>
            <!-- 레드팀 -->
            <div class="team-column">
              <div v-for="player in team2"
                   :key="player.puuid"
                   class="flex items-center gap-1 mb-1">
                <img
                    :src="player.champProfileIconUrl"
                    :alt="player.champName"
                    class="w-4 h-4 rounded-sm"
                >
                <span class="text-xs text-gray-300 hover:text-blue-400 cursor-pointer truncate"
                      @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
                  {{ player.riotIdGameName }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 토글 아이콘 -->
        <div class="toggle-section flex items-center px-4">
          <ChevronDown v-if="!showDetails" class="w-5 h-5 text-gray-400" />
          <ChevronUp v-else class="w-5 h-5 text-gray-400" />
        </div>
      </div>
    </div>

    <!-- 매치 상세 정보 -->
    <div v-show="showDetails" class="game-item">
<!--      <div class="game-header">
        <div class="game-info">
          <div class="game-type">{{ match.matchInfo.gameModeName }}</div>
          <div class="game-meta">
            <span class="game-date">{{ formatDate(match.matchInfo.gameStartDt) }}</span>
            <span class="separator">•</span>
            <span class="game-duration">{{ formatDuration(match.matchInfo.gameDuration) }}</span>
          </div>
        </div>
      </div>-->
      <div class="teams-container">
        <match-team
            :participants="team1"
            :all-participants="match.participantList"
            :team-type="'blue'"
            :profile-data="profileData"
            @review-player="$emit('reviewPlayer', $event)"
        />
        <div class="teams-divider"></div>
        <match-team
            :participants="team2"
            :all-participants="match.participantList"
            :team-type="'red'"
            :profile-data="profileData"
            @review-player="$emit('reviewPlayer', $event)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import type { LolMatchInfoRes, LolMatchParticipant } from '@/types/match';
import type { LolSummonerProfileResDto } from '@/types/summoner';
import { ThumbsUp, ThumbsDown, ChevronDown, ChevronUp } from 'lucide-vue-next';
import MatchTeam from './MatchTeam.vue';
import {goSelectedSummonerProfile} from "@/utils/common.ts";

const props = defineProps<{
  match: LolMatchInfoRes;
  profileData: LolSummonerProfileResDto;
}>();

defineEmits<{
  (e: 'reviewPlayer', player: LolMatchParticipant): void;
}>();

const showDetails = ref(false);

const currentPlayer = computed(() => {
  return props.match.participantList.find(
      p => p.puuid === props.profileData.riotAccountInfoEntity.puuid
  ) as LolMatchParticipant;
});

const isWin = computed(() => {
  return currentPlayer.value?.isWin;
});

const team1 = computed(() => {
  return props.match.participantList.filter(p => p.teamId === 100);
});

const team2 = computed(() => {
  return props.match.participantList.filter(p => p.teamId === 200);
});

const toggleDetails = () => {
  showDetails.value = !showDetails.value;
};

const calculateKDA = (player: LolMatchParticipant) => {
  return player.deaths === 0
      ? ((player.kills + player.assists)).toFixed(2)
      : ((player.kills + player.assists) / player.deaths).toFixed(2);
};

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('ko-KR');
};

const formatDuration = (seconds: number) => {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
};
</script>

<style scoped>
.match-container {
  @apply rounded-lg border border-gray-700 overflow-hidden transition-all duration-200;
  border: 1px solid rgba(255,255,255,.05);
}

.summary-section {
  @apply p-4 cursor-pointer /*hover:bg-gray-800/30;*/
}

.participants-section {
  @apply flex-1 pl-4 ml-auto;
  max-width: min-content;
}

.participants-section .flex {
  @apply justify-start;
}

.team-column {
  @apply flex flex-col;
  width: 100px;
}

.game-item {
  background: #141414;
  /*border-radius: 12px;*/
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  transition: transform 0.2s ease, border-color 0.2s ease;
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

.player-icon img {
  @apply transition-transform hover:scale-110;
}

@media (max-width: 768px) {
  .match-summary {
    @apply flex-col;
  }

  .game-info-section,
  .champion-kda-section,
  .participants-section {
    @apply w-full mb-2;
  }

  .participants-section {
    @apply flex-col gap-4;
  }

  .team-column {
    @apply gap-2;
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