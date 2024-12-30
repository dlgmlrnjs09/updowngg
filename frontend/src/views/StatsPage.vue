# StatsPage.vue
<template>
  <div class="stats-page max-w-[1200px] mx-auto px-6 mt-[100px]">
    <!-- 필터 섹션 -->
    <div class="stats-card">
      <!-- 필터 옵션들 -->
      <div class="filter-options">
        <!-- 큐 타입 필터 -->
        <div class="filter-row">
          <div class="filter-label">큐 타입</div>
          <div class="filter-content">
            <button
                v-for="queue in ['솔로랭크', '자유랭크', '무작위 총력전', '우르프']"
                :key="queue"
                :class="['filter-chip', { active: selectedQueue === queue }]"
                @click="selectedQueue = queue"
            >
              {{ queue }}
            </button>
          </div>
        </div>

        <!-- 티어 필터 -->
        <div class="filter-row">
          <div class="filter-label">티어</div>
          <div class="filter-content">
            <button
                v-for="tier in ['전체', 'Iron', 'Bronze', 'Silver', 'Gold', 'Platinum', 'Emerald', 'Diamond', 'Master', 'Grandmaster', 'Challenger']"
                :key="tier"
                :class="['filter-chip', { active: selectedTier === tier }]"
                @click="selectedTier = tier"
            >
              {{ tier }}
            </button>
          </div>
        </div>

        <!-- 기간 필터 -->
        <div class="filter-row">
          <div class="filter-label">기간</div>
          <div class="filter-content">
            <button
                v-for="period in ['전체', '최근 1달', '최근 7일', '오늘']"
                :key="period"
                :class="['filter-chip', { active: selectedPeriod === period }]"
                @click="selectedPeriod = period"
            >
              {{ period }}
            </button>
          </div>
        </div>

        <!-- 포지션 필터 (무작위 총력전이 아닐 때만 표시) -->
        <div class="filter-row" v-if="selectedQueue !== '무작위 총력전'">
          <div class="filter-label">포지션</div>
          <div class="filter-content">
            <button
                v-for="position in ['전체', '탑', '정글', '미드', '바텀', '서포터']"
                :key="position"
                :class="['filter-chip', { active: selectedPosition === position }]"
                @click="selectedPosition = position"
            >
              {{ position }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 챔피언 통계 테이블 -->
    <div class="stats-card">
      <div class="champion-table">
        <table class="w-full">
          <thead>
          <tr class="text-left border-b border-[#ffffff1a]">
            <th class="py-2 px-4 text-gray-300 w-12">#</th>
            <th class="py-2 px-4 text-gray-300 w-48 cursor-pointer" @click="toggleSort('nameKr')">
              <div class="flex items-center gap-1">
                챔피언
                <ArrowUpDown class="w-3.5 h-3.5" :class="{ 'text-[#2979FF]': sortColumn === 'nameKr' }" />
              </div>
            </th>
            <th class="py-2 px-4 text-gray-300 w-28 cursor-pointer" @click="toggleSort('playCount')">
              <div class="flex items-center gap-1">
                플레이수
                <ArrowUpDown class="w-3.5 h-3.5" :class="{ 'text-[#2979FF]': sortColumn === 'playCount' }" />
              </div>
            </th>
            <th class="py-2 px-4 text-gray-300 w-28 cursor-pointer" @click="toggleSort('reviewCount')">
              <div class="flex items-center gap-1">
                평가수
                <ArrowUpDown class="w-3.5 h-3.5" :class="{ 'text-[#2979FF]': sortColumn === 'reviewCount' }" />
              </div>
            </th>
            <th class="py-2 px-4 text-gray-300 w-40 cursor-pointer" @click="toggleSort('upRatio')">
              <div class="flex items-center gap-1">
                <ThumbsUp class="thumb-icon up" :size="20" />
                <ArrowUpDown class="w-3.5 h-3.5" :class="{ 'text-[#2979FF]': sortColumn === 'upRatio' }" />
              </div>
            </th>
            <th class="py-2 px-4 text-gray-300 w-40 cursor-pointer" @click="toggleSort('downRatio')">
              <div class="flex items-center gap-1">
                <ThumbsDown class="thumb-icon down" :size="20"/>
                <ArrowUpDown class="w-3.5 h-3.5" :class="{ 'text-[#2979FF]': sortColumn === 'downRatio' }" />
              </div>
            </th>
            <th class="py-2 px-4 text-gray-300">자주 받은 태그</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(champion, index) in sortedChampionStats"
              :key="champion.nameKr"
              class="border-b border-[#ffffff1a] hover:bg-[#ffffff0a]">
            <td class="py-3 px-4 text-gray-300 text-sm">{{ index + 1 }}</td>
            <td class="py-3 px-4 text-gray-400">
              <div class="flex items-center gap-3">
                <img :src="champion.iconUrl" :alt="champion.nameKr" class="w-8 h-8 rounded">
                <span class="text-sm">{{ champion.nameKr }}</span>
              </div>
            </td>
            <td class="py-3 px-4 text-sm text-gray-400">{{ champion.playCount }}</td>
            <td class="py-3 px-4 text-sm text-gray-400">{{ champion.reviewCount }}</td>
            <td class="py-3 px-4 text-gray-400">
              <div class="flex items-center gap-2">
                <div class="w-24 h-1.5 bg-[#1a1a1a] rounded-full overflow-hidden">
                  <div
                      class="h-full bg-[#4CAF50]"
                      :style="{ width: `${champion.upRatio}%` }"
                  ></div>
                </div>
                <span class="text-sm">{{ champion.upRatio }}%</span>
              </div>
            </td>
            <td class="py-3 px-4 text-gray-400">
              <div class="flex items-center gap-2">
                <div class="w-24 h-1.5 bg-[#1a1a1a] rounded-full overflow-hidden">
                  <div
                      class="h-full bg-[#FF5252]"
                      :style="{ width: `${champion.downRatio}%` }"
                  ></div>
                </div>
                <span class="text-sm">{{ champion.downRatio }}%</span>
              </div>
            </td>
            <td class="py-4 px-4">
              <div class="flex gap-2">
                  <span v-for="tag in champion.topTagList"
                        :key="tag"
                        class="tag">
                    {{ tag }}
                  </span>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, computed, watch} from 'vue'
import {statsApi} from "@/api/stats.ts";
import { ThumbsUp, ThumbsDown, ArrowUpDown } from 'lucide-vue-next'
import type {ChampionResDto} from "@/types/stats.ts";
import type {RankingCard} from "@/types/ranking.ts";
import {MatchGameMode, MatchPosition} from "@/types/match.ts";
import type {SearchFilter} from "@/types/stats.ts";
import {Tier} from "@/types/league.ts";

// 필터 상태
const selectedQueue = ref('솔로랭크')
const selectedTier = ref('전체')
const selectedPeriod = ref('전체')
const selectedPosition = ref('전체')
const championStats = ref<ChampionResDto[] | null>(null);

// 정렬 상태
const sortColumn = ref<string>('');
const sortDirection = ref<'asc' | 'desc' | null>(null);

// 정렬 토글 함수
const toggleSort = (column: string) => {
  if (sortColumn.value === column) {
    if (sortDirection.value === 'asc') {
      sortDirection.value = 'desc';
    } else if (sortDirection.value === 'desc') {
      sortDirection.value = null;
      sortColumn.value = '';
    } else {
      sortDirection.value = 'asc';
    }
  } else {
    sortColumn.value = column;
    sortDirection.value = 'asc';
  }
};

// 정렬된 챔피언 통계
const sortedChampionStats = computed(() => {
  if (!championStats.value || !sortColumn.value || !sortDirection.value) {
    return championStats.value;
  }

  return [...championStats.value].sort((a, b) => {
    const aValue = a[sortColumn.value];
    const bValue = b[sortColumn.value];

    if (sortDirection.value === 'asc') {
      return aValue > bValue ? 1 : -1;
    } else {
      return aValue < bValue ? 1 : -1;
    }
  });
});

// API 요청용 필터 값 계산
const apiFilter = computed(() => {
  const filter: SearchFilter = {};

  if (selectedQueue.value !== '전체') {
    filter.queueType = queueMapping[selectedQueue.value.toUpperCase()];
  }

  if (selectedTier.value !== '전체') {
    filter.tier = selectedTier.value.toUpperCase() as Tier;
  }

  if (selectedPosition.value !== '전체' && selectedQueue.value !== '무작위 총력전') {
    filter.position = positionMapping[selectedPosition.value];
  }

  if (selectedPeriod.value !== '전체') {
    filter.period = selectedPeriod.value;
  }

  return filter;
});

const fetchChampionStats = async () => {
  const response = await statsApi.getChampionList(apiFilter.value);
  console.log('response === ' + response)
  championStats.value = response.data;
}

onMounted(() => {
  fetchChampionStats();
});

// 필터 변경시마다 API 호출
watch([selectedQueue, selectedTier, selectedPosition, selectedPeriod], () => {
  fetchChampionStats();
});

// 큐 타입 매핑
const queueMapping: Record<string, MatchGameMode> = {
  '솔로랭크': MatchGameMode.SOLO_RANK,
  '자유랭크': MatchGameMode.FLEX_RANK,
  '무작위 총력전': MatchGameMode.ARAM,
  '일반게임': MatchGameMode.NORMAL,
};

// 포지션 매핑
const positionMapping: Record<string, MatchPosition> = {
  '탑': MatchPosition.TOP,
  '정글': MatchPosition.JUNGLE,
  '미드': MatchPosition.MIDDLE,
  '바텀': MatchPosition.BOTTOM,
  '서포터': MatchPosition.UTILITY,
};

</script>

<style scoped>
.stats-card {
  @apply bg-[#141414] rounded-xl border border-[#ffffff1a] mb-6;
}

.filter-row {
  @apply flex items-center gap-4 h-14 border-b border-[#ffffff1a] px-4 m-0;
}

.filter-row:last-child {
  @apply border-b-0;
}

.filter-label {
  @apply text-sm text-gray-400 w-20 flex items-center;
}

.filter-content {
  @apply flex items-center flex-wrap gap-2;
}

.filter-chip {
  @apply h-8 px-3 rounded text-sm text-gray-400 transition-all hover:text-white border border-[#ffffff1a] flex items-center justify-center;
}

.filter-chip.active {
  @apply bg-[#2979FF] text-white border-[#2979FF];
}

.tag {
  @apply bg-[#2979FF1A] text-[#2979FF] px-2 py-1 rounded text-xs;
}

/* 스크롤바 스타일 */
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

.thumb-icon.up {
  color: #4CAF50;
}

.thumb-icon.down {
  color: #FF5252;
}

/* 테이블 레이아웃 고정 */
table {
  table-layout: fixed;
  min-width: 1000px;
}
</style>