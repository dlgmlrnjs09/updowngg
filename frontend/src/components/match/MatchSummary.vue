<template>
  <div
      class="match-container"
      :class="[
        isWin && !showDetails ? 'bg-blue-950/30' : '',
        !isWin && !showDetails ? 'bg-red-950/30' : '',
        showDetails ? 'bg-[#141414]' : ''
      ]">
    <div class="summary-section" @click="toggleDetails">
      <div class="match-summary flex flex-col md:flex-row items-stretch">
        <!-- 모바일 레이아웃 -->
        <div class="block md:hidden w-full">
          <div class="flex justify-between items-center mb-2">
            <div class="flex items-center gap-2">
              <div class="relative">
                <img
                    :src="currentPlayer.champProfileIconUrl"
                    :alt="currentPlayer.champName"
                    class="w-10 h-10 rounded-lg"
                >
                <span class="absolute bottom-0 right-0 text-xs bg-black/80 px-1 rounded">
                  {{ currentPlayer.champLevel }}
                </span>
              </div>
              <div>
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
            <div class="text-right">
              <div class="text-xs">{{ match.matchInfo.gameModeName }}</div>
              <div class="text-xs text-gray-400">{{ formatTimeAgo(match.matchInfo.gameStartDt) }}</div>
              <div class="text-xs" :class="isWin ? 'text-blue-400' : 'text-red-400'">
                {{ isWin ? '승리' : '패배' }} · {{ formatDuration(match.matchInfo.gameDuration) }}
              </div>
            </div>
          </div>

          <!-- 리뷰 섹션 -->
          <template v-if="match.reviewByMatchSummaryDto">
            <div class="flex items-center justify-between border-t border-gray-700 pt-2">
              <div class="flex items-center gap-4">
                <div class="flex items-center gap-2">
                  <ThumbsUp class="w-[16px] h-[16px] text-[#4CAF50]" />
                  <span class="text-[#4CAF50] font-semibold text-xs">
                    {{ match.reviewByMatchSummaryDto?.upCount || 0 }}
                  </span>
                </div>
                <div class="flex items-center gap-2">
                  <ThumbsDown class="w-[16px] h-[16px] text-[#FF5252]" />
                  <span class="text-[#FF5252] font-semibold text-xs">
                    {{ match.reviewByMatchSummaryDto?.downCount || 0 }}
                  </span>
                </div>
              </div>
              <div class="reviewers-champions">
                <div class="flex gap-1 overflow-hidden">
                  <img
                      v-for="reviewer in match.reviewByMatchSummaryDto?.reviewerInfoList"
                      :key="reviewer.reviewerChampId"
                      :src="reviewer.isAnonymous ? '/src/assets/icon/anonymous_profile.png' : reviewer.reviewerChampIconUrl"
                      :alt="reviewer.reviewerChampId"
                      class="w-5 h-5 rounded-sm border border-gray-700/50"
                      :title="reviewer.isAnonymous ? '익명의 소환사' : reviewer.reviewerChampName"
                  >
                </div>
              </div>
            </div>
            <div class="mt-2">
              <TagList :tags="match.reviewByMatchSummaryDto?.tagDtoList?.slice(0, 3)" size="small" is-show-count/>
            </div>
          </template>
          <template v-else>
            <div class="text-center text-xs text-gray-400 border-t border-gray-700 pt-2">
              해당 게임에서 받은 평가가 없습니다.
            </div>
          </template>

          <div class="flex justify-center items-center mt-2 border-t border-gray-700 pt-2">
            <div class="toggle-section flex items-center">
              <ChevronDown v-if="!showDetails" class="w-5 h-5 text-gray-400" />
              <ChevronUp v-else class="w-5 h-5 text-gray-400" />
            </div>
          </div>
        </div>

        <!-- 태블릿/PC 레이아웃 -->
        <div class="hidden md:flex w-full">
          <!-- 기존 데스크탑 레이아웃 코드 유지 -->
          <div class="game-info-section w-36 px-4 flex flex-col">
            <div class="text-sm font-medium">{{ match.matchInfo.gameModeName }}</div>
            <div class="text-xs text-gray-400">{{ formatTimeAgo(match.matchInfo.gameStartDt) }}</div>
            <div class="divider my-2 border-t border-gray-700 w-[60%]"></div>
            <div class="text-xs" :class="isWin ? 'text-blue-400' : 'text-red-400'">
              {{ isWin ? '승리' : '패배' }}
            </div>
            <div class="text-xs text-gray-400">{{ formatDuration(match.matchInfo.gameDuration) }}</div>
          </div>

          <div class="champion-kda-section flex items-center gap-4 w-60">
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

          <div class="review-info-section px-4 border-l border-r border-gray-700 w-[28.5rem]">
            <template v-if="match.reviewByMatchSummaryDto">
              <div class="reviewers-champions mb-2">
                <div class="flex gap-1 overflow-hidden">
                  <img
                      v-for="reviewer in match.reviewByMatchSummaryDto?.reviewerInfoList"
                      :key="reviewer.reviewerChampId"
                      :src="reviewer.isAnonymous ? '/src/assets/icon/anonymous_profile.png' : reviewer.reviewerChampIconUrl"
                      :alt="reviewer.reviewerChampId"
                      class="w-7 h-7 rounded-sm border border-gray-700/50"
                      :title="reviewer.isAnonymous ? '익명의 소환사' : reviewer.reviewerChampName"
                  >
                </div>
              </div>

              <div class="flex items-center gap-4 mb-2">
                <div class="flex items-center gap-2">
                  <ThumbsUp class="w-[20px] h-[20px] text-[#4CAF50]" />
                  <span class="text-[#4CAF50] font-semibold text-sm">
                    {{ match.reviewByMatchSummaryDto?.upCount || 0 }}
                  </span>
                </div>
                <div class="flex items-center gap-2">
                  <ThumbsDown class="w-[20px] h-[20px] text-[#FF5252]" />
                  <span class="text-[#FF5252] font-semibold text-sm">
                    {{ match.reviewByMatchSummaryDto?.downCount || 0 }}
                  </span>
                </div>
              </div>

              <TagList :tags="match.reviewByMatchSummaryDto?.tagDtoList?.slice(0, 3)" size="small" is-show-count/>
            </template>
            <template v-else>
              <div class="flex items-center justify-center h-full text-sm text-gray-400">
                해당 게임에서 받은 평가가 없습니다.
              </div>
            </template>
          </div>

          <div class="participants-section">
            <div class="flex gap-4">
              <div class="team-column">
                <div v-for="player in team1"
                     :key="player.puuid"
                     class="participant-row">
                  <div class="flex items-center gap-1 min-w-0 flex-1">
                    <img
                        :src="player.champProfileIconUrl"
                        :alt="player.champName"
                        class="w-4 h-4 rounded-sm flex-shrink-0"
                    >
                    <span class="text-xs text-gray-300 hover:text-blue-400 cursor-pointer truncate"
                          @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
                      {{ player.riotIdGameName }}
                    </span>
                  </div>
                  <div class="flex items-center">
                    <FileEdit
                        v-if="auth.user?.puuid === profileData.riotAccountInfoEntity.puuid
                              && player.puuid !== auth.user?.puuid
                              && !player.reviewDto.reviewable"
                        class="w-4 h-4 text-blue-400 hover:text-blue-300 cursor-pointer flex-shrink-0 ml-2"
                        @click.stop="$emit('reviewPlayer', player)"
                    />
                    <PencilIcon
                        v-if="auth.user?.puuid === profileData.riotAccountInfoEntity.puuid
                              && player.puuid !== auth.user?.puuid
                              && player.reviewDto.reviewable"
                        class="w-[16px] h-[16px] text-gray-600 hover:text-gray-400 cursor-pointer flex-shrink-0 ml-2"
                        @click.stop="$emit('reviewPlayer', player)"
                    />
                  </div>
                </div>
              </div>

              <div class="team-column">
                <div v-for="player in team2"
                     :key="player.puuid"
                     class="participant-row">
                  <div class="flex items-center gap-1 min-w-0 flex-1">
                    <img
                        :src="player.champProfileIconUrl"
                        :alt="player.champName"
                        class="w-4 h-4 rounded-sm flex-shrink-0"
                    >
                    <span class="text-xs text-gray-300 hover:text-blue-400 cursor-pointer truncate"
                          @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
                      {{ player.riotIdGameName }}
                    </span>
                  </div>
                  <div class="flex items-center">
                    <FileEdit
                        v-if="auth.user?.puuid === profileData.riotAccountInfoEntity.puuid
                              && player.puuid !== auth.user?.puuid
                              && !player.reviewDto.reviewable"
                        class="w-4 h-4 text-blue-400 hover:text-blue-300 cursor-pointer flex-shrink-0 ml-2"
                        @click.stop="$emit('reviewPlayer', player)"
                    />
                    <PencilIcon
                        v-if="auth.user?.puuid === profileData.riotAccountInfoEntity.puuid
                              && player.puuid !== auth.user?.puuid
                              && player.reviewDto.reviewable"
                        class="w-[16px] h-[16px] text-gray-600 hover:text-gray-400 cursor-pointer flex-shrink-0 ml-2"
                        @click.stop="$emit('reviewPlayer', player)"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="toggle-section flex items-center px-4">
            <ChevronDown v-if="!showDetails" class="w-5 h-5 text-gray-400" />
            <ChevronUp v-else class="w-5 h-5 text-gray-400" />
          </div>
        </div>
      </div>
    </div>

    <!-- 매치 상세 정보 -->
    <div v-show="showDetails" class="game-item">
      <!-- PC/태블릿 레이아웃 -->
      <div class="hidden md:block">
        <div class="teams-container">
          <!-- 아레나 모드가 아닌 경우 -->
          <template v-if="!isArena">
            <match-team
                :game-mode="match.matchInfo.gameMode"
                :participants="team1"
                :all-participants="match.participantList"
                :team-type="'blue'"
                :profile-data="profileData"
                @review-player="$emit('reviewPlayer', $event)"
            />
            <div class="teams-divider"></div>
            <match-team
                :game-mode="match.matchInfo.gameMode"
                :participants="team2"
                :all-participants="match.participantList"
                :team-type="'red'"
                :profile-data="profileData"
                @review-player="$emit('reviewPlayer', $event)"
            />
          </template>

          <!-- 아레나 모드인 경우 -->
          <template v-else>
            <div class="arena-teams-full-width">
              <match-team
                  :game-mode="match.matchInfo.gameMode"
                  :participants="match.participantList"
                  :all-participants="match.participantList"
                  :team-type="'all'"
                  :profile-data="profileData"
                  @review-player="$emit('reviewPlayer', $event)"
              />
            </div>
          </template>
        </div>
      </div>

      <!-- 모바일 레이아웃 -->
      <div class="md:hidden">
        <match-team
            :game-mode="match.matchInfo.gameMode"
            :participants="match.participantList"
            :all-participants="match.participantList"
            :team-type="'all'"
            :profile-data="profileData"
            @review-player="$emit('reviewPlayer', $event)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import {type LolMatchInfoRes, type LolMatchParticipant, MatchGameMode} from '@/types/match';
import type { LolSummonerProfileResDto } from '@/types/summoner';
import { ThumbsUp, ThumbsDown, ChevronDown, ChevronUp, PenLine, FileEdit, PencilIcon } from 'lucide-vue-next';
import MatchTeam from './MatchTeam.vue';
import {formatTimeAgo, goSelectedSummonerProfile} from "@/utils/common.ts";
import TagList from "@/components/common/TagList.vue";
import type {ReviewByMatchSummaryDto} from "@/types/review.ts";
import {useAuthStore} from "@/stores/auth.ts";

const auth = useAuthStore();

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

/*const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('ko-KR');
};*/

const formatDuration = (seconds: number) => {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
};

const isArena = computed(() => {
  return props.match.matchInfo.gameMode === MatchGameMode.CHERRY;
});

const arenaTeams = computed(() => {
  if (!isArena.value) return [];

  // playerSubteamId로 그룹핑
  const teams: { [key: number]: LolMatchParticipant[] } = {};
  props.match.participantList.forEach(player => {
    const teamId = player.playerSubteamId || 0;
    if (!teams[teamId]) {
      teams[teamId] = [];
    }
    teams[teamId].push(player);
  });

  // 팀별로 subteamPlacement 값을 가져와서 오름차순 정렬
  const sortedTeams = Object.values(teams).sort((teamA, teamB) => {
    // 각 팀에서 첫 플레이어의 subteamPlacement를 기준으로 정렬
    // (팀 내 모든 플레이어의 subteamPlacement는 동일해야 함)
    const placementA = teamA[0]?.subteamPlacement ?? Number.MAX_SAFE_INTEGER;
    const placementB = teamB[0]?.subteamPlacement ?? Number.MAX_SAFE_INTEGER;
    return placementA - placementB;
  });

  return sortedTeams;
});

const getPlacementText = (placement) => {
  if (!placement && placement !== 0) return '';

  switch (placement) {
    case 1: return '1st';
    case 2: return '2nd';
    case 3: return '3rd';
    default: return `${placement}th`;
  }
}
</script>

<style scoped>
.match-container {
  @apply rounded-lg border border-gray-700 overflow-hidden transition-all duration-200;
  border: 1px solid rgba(255,255,255,.05);
}

.summary-section {
  @apply p-4 cursor-pointer;
}

.review-info-section {
  @apply flex flex-col justify-center min-w-[160px];
}

.participants-section {
  @apply flex-1 pl-4;
  max-width: min-content;
}

.participants-section .flex {
  @apply justify-start;
}

.team-column {
  @apply flex flex-col;
  width: 120px;
}

.participant-row {
  @apply flex items-center mb-1 pr-1 w-[120px];
}

.game-item {
  background: #141414;
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
  grid-template-columns: 1fr auto 1fr; /* 기본 레이아웃 */
  gap: 24px;
  align-items: start;
}

.arena-teams-full-width {
  width: 100%;
  grid-column: 1 / -1; /* 모든 열을 차지하도록 함 */
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

.arena-team-row {
  width: 100%;
  margin-bottom: 2px;
  border-radius: 4px;
  overflow: hidden;
  background: rgba(30, 30, 30, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.arena-team-header {
  padding: 4px 8px;
  background: rgba(0, 0, 0, 0.3);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.arena-team-players {
  padding: 4px;
}

.arena-participant {
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.2);
  margin-bottom: 2px;
  padding: 4px 6px;
}

.arena-participant:last-child {
  margin-bottom: 0;
}

.arena-placement {
  font-size: 10px;
  padding: 1px 4px;
  border-radius: 3px;
  background: rgba(0, 0, 0, 0.3);
}
</style>