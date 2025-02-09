<template>
  <div class="stats-page max-w-[1200px] mx-auto px-4 mt-[20px]">
    <FilterSection @update:filter="onFilterUpdate" is-not-show-period/>

    <div class="stats-card">
      <div class="champion-table">
        <!-- PC 테이블 (데스크톱에서만 보임) -->
        <table class="w-full desktop-table">
          <thead>
          <tr class="text-left border-b border-[#ffffff1a]">
            <th class="py-2 px-4 text-gray-300 w-12">#</th>
            <th class="py-2 px-4 text-gray-300 w-52">소환사</th>
            <th class="py-2 px-4 text-gray-300 w-24">평가수</th>
            <th class="py-2 px-4 text-gray-300 w-24">평가점수</th>
            <th class="py-2 px-4 text-gray-300 w-48">평가비율</th>
            <th class="py-2 px-4 text-gray-300 w-52 most-champion-column">모스트 챔피언별 평가</th>
            <th class="py-2 px-4 text-gray-300">자주 받은 태그</th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="(player, index) in rankerPlayers"
              :key="player.summonerBasicInfoDto.puuid"
              class="border-b border-[#ffffff1a] hover:bg-[#ffffff0a] cursor-pointer"
              @click="selectPlayer(player)"
              :class="{ 'bg-[#ffffff0a]': selectedPlayer === player }"
          >
            <td class="py-2 px-4 text-gray-300 text-sm">{{ index + 1 }}</td>
            <td class="py-2 px-4 text-gray-400">
              <div class="flex items-center gap-3 player-name-wrapper">
                <img :src="player.summonerBasicInfoDto.profileIconUrl" :alt="player.summonerBasicInfoDto.gameName" class="w-8 h-8 rounded">
                <span class="player-name text-sm" @click.stop="goSelectedSummonerProfile(player.summonerBasicInfoDto.gameName, player.summonerBasicInfoDto.tagLine)">{{ player.summonerBasicInfoDto.gameName }}</span>
                <div class="player-tag">#{{ player.summonerBasicInfoDto.tagLine }}</div>
              </div>
            </td>
            <td class="py-2 px-4 text-sm text-gray-400">{{player.reviewStatsDto.totalReviewCnt}} </td>
            <td class="py-2 px-4 text-sm text-gray-400">{{ player.reviewStatsDto.score }}</td>
            <td class="py-2 px-4 text-gray-400">
              <div class="flex items-center justify-between">
                <div class="w-56 h-4 bg-[#1a1a1a] rounded-full overflow-hidden relative">
                  <div class="absolute top-0 left-0 h-full bg-[#4CAF50] flex items-center"
                       :style="{ width: `${player.reviewStatsDto.upRatio}%` }">
                    <span class="text-[10px] text-white pl-1">{{ player.reviewStatsDto.upRatio }}%</span>
                  </div>
                  <div class="absolute top-0 right-0 h-full bg-[#FF5252] flex items-center justify-end"
                       :style="{ width: `${player.reviewStatsDto.downRatio}%` }">
                    <span class="text-[10px] text-white pr-1">{{ player.reviewStatsDto.downRatio }}%</span>
                  </div>
                </div>
              </div>
            </td>
            <td class="py-2 px-4 text-gray-400 most-champion-column">
              <div class="flex items-center gap-4">
                <div v-for="champion in player.championStatsDtoList" :key="champion.nameUs"
                     class="flex flex-col items-center gap-1">
                  <img :src="champion.iconUrl" :alt="champion.nameKr" class="w-8 h-8 rounded">
                  <div class="w-8 h-1.5 bg-[#1a1a1a] rounded-full overflow-hidden relative">
                    <div class="absolute top-0 left-0 h-full bg-[#4CAF50]"
                         :style="{ width: `${champion.upRatio}%` }">
                    </div>
                    <div class="absolute top-0 right-0 h-full bg-[#FF5252]"
                         :style="{ width: `${champion.downRatio}%` }">
                    </div>
                  </div>
                </div>
              </div>
            </td>
            <td class="py-4 px-4">
              <div class="flex gap-2">
                <TagList :tags="player.reviewTagDtoList" size="small" is-show-count/>
              </div>
            </td>
          </tr>
          </tbody>
        </table>

        <!-- 모바일 리스트 (모바일에서만 보임) -->
        <div v-if="isMobile" class="mobile-table">
          <div class="mobile-table-header">
            <div class="mobile-header-rank">#</div>
            <div class="mobile-header-summoner">소환사</div>
            <div class="mobile-header-review-count">평가수</div>
            <div class="mobile-header-score">평가점수</div>
          </div>
          <div
              v-for="(player, index) in rankerPlayers"
              :key="player.summonerBasicInfoDto.puuid"
              class="mobile-ranking-item"
          >
            <div class="mobile-ranking-content">
              <div class="mobile-ranking-number">{{ index + 1 }}</div>
              <div class="mobile-summoner-info">
                <img
                    :src="player.summonerBasicInfoDto.profileIconUrl"
                    :alt="player.summonerBasicInfoDto.gameName"
                    class="mobile-summoner-image"
                >
                <div class="mobile-summoner-details">
                  <div class="mobile-summoner-name">
                    {{ player.summonerBasicInfoDto.gameName }}
                    <span class="mobile-summoner-tag">#{{ player.summonerBasicInfoDto.tagLine }}</span>
                  </div>
                </div>
              </div>
              <div class="mobile-summoner-review-count">
                {{ player.reviewStatsDto.totalReviewCnt }}
              </div>
              <div class="mobile-summoner-score">
                {{ player.reviewStatsDto.score }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, onUnmounted } from 'vue'
import { statsApi } from "@/api/stats.ts"
import FilterSection from '@/components/common/SearchFilter.vue'
import TagList from "@/components/common/TagList.vue"
import {rankingApi} from "@/api/ranking.ts";
import type {RankerPlayer, RankingSearchFilter} from "@/types/ranking.ts";
import type {SearchFilter} from "@/types/stats.ts";
import {goSelectedSummonerProfile} from "@/utils/common.ts";

const rankerPlayers = ref<RankerPlayer[] | null>(null)
const selectedPlayer = ref<RankerPlayer | null>(null)

// 모바일 여부 판단
const isMobile = ref(window.innerWidth <= 640)

// 반응형 처리를 위한 윈도우 리사이즈 이벤트 핸들러
const handleResize = () => {
  isMobile.value = window.innerWidth <= 640
}

const fetchSummonerStats = async (filter: SearchFilter) => {
  const reqDto: RankingSearchFilter = {
    sortTypeReqDto: filter,
    limit: 100,
    offset: 0
  };

  const response = await rankingApi.getRankerCardList(reqDto);
  rankerPlayers.value = response.data;
}

const onFilterUpdate = (filter: SearchFilter) => {
  fetchSummonerStats(filter)
}

const selectPlayer = (player: RankerPlayer) => {
  if (!isMobile.value) {
    selectedPlayer.value = player;
  }
}

onMounted(() => {
  fetchSummonerStats({})
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* PC 테이블 스타일 */
.stats-card {
  @apply bg-[#141414] rounded-xl border border-[#ffffff1a] mb-6;
}

.champion-table {
  @apply overflow-x-auto;
}

.champion-table::-webkit-scrollbar {
  @apply h-2;
}

.champion-table::-webkit-scrollbar-track {
  @apply bg-[#1a1a1a] rounded-full;
}

.champion-table::-webkit-scrollbar-thumb {
  @apply bg-[#333] rounded-full;
}

table {
  table-layout: fixed;
}

.player-tag {
  font-size: 12px;
  color: #666;
}

.player-name-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.player-name {
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

/* 선택된 소환사 상세 정보 스타일 */
@media (min-width: 641px) {
  .selected-summoner-details {
    @apply bg-[#1a1a1a] rounded-b-xl p-6;
  }

  .selected-summoner-header {
    @apply flex items-center mb-6;
  }

  .selected-summoner-image {
    @apply w-20 h-20 rounded-full mr-6;
  }

  .selected-summoner-name {
    @apply text-2xl font-bold text-white mb-2 flex items-center;
  }

  .selected-summoner-tag {
    @apply text-sm text-gray-400 ml-2;
  }

  .selected-summoner-stats {
    @apply flex space-x-4 mt-2;
  }

  .stat-item {
    @apply flex flex-col;
  }

  .stat-label {
    @apply text-xs text-gray-400 mb-1;
  }

  .stat-value {
    @apply text-base text-white font-semibold;
  }

  .selected-summoner-rating {
    @apply mb-6;
  }

  .rating-bar {
    @apply space-y-2;
  }

  .bar-background {
    @apply h-3 bg-[#333] rounded-full overflow-hidden flex;
  }

  .bar-positive {
    @apply bg-[#4CAF50] h-full;
  }

  .bar-negative {
    @apply bg-[#FF5252] h-full;
  }

  .rating-labels {
    @apply flex justify-between text-sm px-1;
  }

  .label-positive {
    @apply text-[#4CAF50];
  }

  .label-negative {
    @apply text-[#FF5252];
  }

  .selected-summoner-bottom {
    @apply flex space-x-6;
  }

  .most-champions {
    @apply flex-grow;
  }

  .most-champions h3 {
    @apply text-lg font-semibold text-white mb-4;
  }

  .champions-grid {
    @apply grid grid-cols-3 gap-4;
  }

  .champion-item {
    @apply flex flex-col items-center;
  }

  .champion-image {
    @apply w-12 h-12 rounded-full mb-2;
  }

  .champion-rating-bar {
    @apply w-full h-1.5 bg-[#333] rounded-full overflow-hidden flex;
  }

  .rating-positive {
    @apply bg-[#4CAF50] h-full;
  }

  .rating-negative {
    @apply bg-[#FF5252] h-full;
  }

  .most-tags h3 {
    @apply text-lg font-semibold text-white mb-4;
  }
}

/* 태블릿 환경에서의 스타일 추가 */
@media (min-width: 641px) and (max-width: 1100px) {
  .most-champion-column {
    display: none;
  }

  /* 태블릿 전용 스타일 (641px ~ 1024px) */
  @media (min-width: 641px) and (max-width: 1024px) {
    .desktop-table {
      table-layout: fixed;
      min-width: 800px;
    }

    /* 태블릿에서만 헤더 텍스트 크기 줄임 */
    .desktop-table th {
      @apply text-xs whitespace-nowrap;
    }

    /* 열 너비 최적화 */
    .desktop-table th:nth-child(1),
    .desktop-table td:nth-child(1) {
      width: 40px;
    }

    .desktop-table th:nth-child(2),
    .desktop-table td:nth-child(2) {
      width: 200px;
    }

    /* 평가수, 평가점수 열 너비 증가 */
    .desktop-table th:nth-child(3),
    .desktop-table td:nth-child(3),
    .desktop-table th:nth-child(4),
    .desktop-table td:nth-child(4) {
      width: 60px;
    }

    /* 평가비율 열 너비 줄임 */
    .desktop-table th:nth-child(5),
    .desktop-table td:nth-child(5) {
      width: 180px; /* 기존 w-48 (192px)에서 120px로 줄임 */
    }

    /* 평가비율 바 크기 조정 */
    .desktop-table td:nth-child(5) .w-56 {
      width: 180px; /* 기존 w-56 (224px)에서 100px로 줄임 */
    }
  }
}

/* 모바일 전용 스타일 */
@media (max-width: 640px) {
  .desktop-table {
    display: none;
  }

  .mobile-table {
    @apply bg-[#0A0A0A];
  }

  .mobile-table-header {
    @apply flex items-center px-4 py-2 border-b border-[#ffffff1a] bg-[#1A1A1A];
  }

  .mobile-table-header > div {
    @apply text-xs text-gray-300 font-semibold;
  }

  .mobile-header-rank {
    @apply w-6 text-center mr-2;
  }

  .mobile-header-summoner {
    @apply flex-grow mr-2;
  }

  .mobile-header-review-count {
    @apply w-12 text-right mr-2;
  }

  .mobile-header-score {
    @apply w-12 text-right;
  }

  .mobile-ranking-item {
    @apply border-b border-[#1A1A1A] px-4 py-2;
  }

  .mobile-ranking-content {
    @apply flex items-center;
  }

  .mobile-ranking-number {
    @apply text-base font-bold text-gray-300 w-6 text-center mr-2;
  }

  .mobile-summoner-info {
    @apply flex items-center flex-grow mr-2;
  }

  .mobile-summoner-image {
    @apply w-8 h-8 rounded-full mr-2;
  }

  .mobile-summoner-details {
    @apply flex-grow;
  }

  .mobile-summoner-name {
    @apply text-white font-semibold text-xs flex items-center;
  }

  .mobile-summoner-tag {
    @apply text-[10px] text-gray-400 ml-1;
  }

  .mobile-summoner-review-count {
    @apply text-sm text-gray-400 w-12 text-right mr-2;
  }

  .mobile-summoner-score {
    @apply text-sm text-white font-semibold w-12 text-right;
  }
}
</style>