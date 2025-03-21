<template>
  <div class="stats-page max-w-[1200px] mx-auto px-6 mt-[20px]">
    <FilterSection @update:filter="onFilterUpdate" />

    <!-- 챔피언 통계 테이블 -->
    <div class="stats-card">
      <div class="champion-table">
        <!-- PC/태블릿 테이블 -->
        <table class="w-full desktop-table">
          <thead>
          <tr class="text-left border-b border-[#ffffff1a]">
            <th class="py-2 px-4 text-gray-300 w-12">#</th>
            <th class="py-2 px-4 text-gray-300 w-48 cursor-pointer" @click="toggleSort('nameKr')">
              <div class="flex items-center gap-1">
                챔피언
                <ArrowUpDown class="w-3.5 h-3.5" :class="{ 'text-[#2979FF]': sortColumn === 'nameKr' }" />
              </div>
            </th>
            <th class="py-2 px-4 text-gray-300 w-28 cursor-pointer play-count-column" @click="toggleSort('playCount')">
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
            <!-- PC에서만 보이는 개별 평가 컬럼 -->
            <th class="py-2 px-4 text-gray-300 w-40 cursor-pointer rating-separate-columns" @click="toggleSort('upRatio')">
              <div class="flex items-center gap-1">
                <ThumbsUp class="thumb-icon up" :size="20" />
                <ArrowUpDown class="w-3.5 h-3.5" :class="{ 'text-[#2979FF]': sortColumn === 'upRatio' }" />
              </div>
            </th>
            <th class="py-2 px-4 text-gray-300 w-40 cursor-pointer rating-separate-columns" @click="toggleSort('downRatio')">
              <div class="flex items-center gap-1">
                <ThumbsDown class="thumb-icon down" :size="20"/>
                <ArrowUpDown class="w-3.5 h-3.5" :class="{ 'text-[#2979FF]': sortColumn === 'downRatio' }" />
              </div>
            </th>
            <!-- 태블릿에서만 보이는 통합 평가 컬럼 -->
            <th class="py-2 px-4 text-gray-300 w-40 cursor-pointer rating-combined-column" @click="toggleSort('upRatio')">
              <div class="flex items-center gap-1">
                평가 비율
                <ArrowUpDown class="w-3.5 h-3.5" :class="{ 'text-[#2979FF]': sortColumn === 'upRatio' }" />
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
            <td class="py-3 px-4 text-sm text-gray-400 play-count-column">{{ champion.playCount }}</td>
            <td class="py-3 px-4 text-sm text-gray-400">{{ champion.reviewCount }}</td>
            <!-- PC에서만 보이는 개별 평가 셀 -->
            <td class="py-3 px-4 text-gray-400 rating-separate-columns">
              <div class="flex items-center gap-2">
                <div class="w-24 h-1.5 bg-[#1a1a1a] rounded-full overflow-hidden">
                  <div class="h-full bg-[#4CAF50]" :style="{ width: `${champion.upRatio}%` }"></div>
                </div>
                <span class="text-sm">{{ champion.upRatio }}%</span>
              </div>
            </td>
            <td class="py-3 px-4 text-gray-400 rating-separate-columns">
              <div class="flex items-center gap-2">
                <div class="w-24 h-1.5 bg-[#1a1a1a] rounded-full overflow-hidden">
                  <div class="h-full bg-[#FF5252]" :style="{ width: `${champion.downRatio}%` }"></div>
                </div>
                <span class="text-sm">{{ champion.downRatio }}%</span>
              </div>
            </td>
            <!-- 태블릿에서만 보이는 통합 평가 셀 -->
            <td class="py-3 px-4 text-gray-400 rating-combined-column">
              <div class="combined-rating-wrapper">
                <div class="combined-rating-bar">
                  <div class="combined-rating-up" :style="{ width: `${champion.upRatio}%` }"></div>
                  <div class="combined-rating-down" :style="{ width: `${champion.downRatio}%` }"></div>
                </div>
                <div class="combined-rating-text">
                  <span>{{ champion.upRatio }}%</span>
                  <span>{{ champion.downRatio }}%</span>
                </div>
              </div>
            </td>
            <td class="py-4 px-4">
              <div class="flex gap-2">
                <TagList :tags="champion.topTagList" size="small" is-show-count/>
              </div>
            </td>
          </tr>
          </tbody>
        </table>

        <!-- 모바일 리스트 (모바일에서만 보임) -->
        <div v-if="isMobile" class="mobile-table">
          <div class="mobile-table-header">
            <div class="mobile-header-rank">#</div>
            <div class="mobile-header-champion">챔피언</div>
            <div class="mobile-header-review-count">평가수</div>
            <div class="mobile-header-ratio">평가 비율</div>
          </div>
          <div
              v-for="(champion, index) in sortedChampionStats"
              :key="champion.nameKr"
              class="mobile-ranking-item"
          >
            <div class="mobile-ranking-content">
              <div class="mobile-ranking-number">{{ index + 1 }}</div>
              <div class="mobile-summoner-info">
                <img
                    :src="champion.iconUrl"
                    :alt="champion.nameKr"
                    class="mobile-summoner-image"
                >
                <div class="mobile-summoner-details">
                  <div class="mobile-summoner-name">
                    {{ champion.nameKr }}
                  </div>
                </div>
              </div>
              <div class="mobile-summoner-review-count">
                {{ champion.reviewCount }}
              </div>
              <div class="mobile-summoner-ratio">
                <div class="mobile-ratio-container">
                  <div
                      class="mobile-ratio-up"
                      :style="{ width: `${champion.upRatio}%` }"
                  ></div>
                  <div
                      class="mobile-ratio-down"
                      :style="{ width: `${champion.downRatio}%` }"
                  ></div>
                </div>
                <div class="mobile-ratio-text">
                  <span>{{ champion.upRatio }}%</span>
                  <span>{{ champion.downRatio }}%</span>
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
import {computed, onMounted, ref, onUnmounted} from 'vue'
import {statsApi} from "@/api/stats.ts"
import {ArrowUpDown, ThumbsDown, ThumbsUp} from 'lucide-vue-next'
import type {ChampionResDto, SearchFilter} from "@/types/stats.ts"
import FilterSection from '@/components/common/SearchFilter.vue'
import TagList from "@/components/common/TagList.vue"
import {MatchGameMode} from "@/types/match.ts";

const championStats = ref<ChampionResDto[] | null>(null)
const sortColumn = ref<string>('upRatio')
const sortDirection = ref<'asc' | 'desc' | null>('desc')

// 모바일 여부 판단
const isMobile = ref(window.innerWidth <= 640)

// 반응형 처리를 위한 윈도우 리사이즈 이벤트 핸들러
const handleResize = () => {
  isMobile.value = window.innerWidth <= 640
}

// 정렬 토글 함수
const toggleSort = (column: string) => {
  if (!isMobile.value) {
    if (sortColumn.value === column) {
      if (sortDirection.value === 'asc') {
        sortDirection.value = 'desc'
      } else if (sortDirection.value === 'desc') {
        sortDirection.value = null
        sortColumn.value = ''
      } else {
        sortDirection.value = 'asc'
      }
    } else {
      sortColumn.value = column
      sortDirection.value = 'asc'
    }
  }
}

// 정렬된 챔피언 통계
const sortedChampionStats = computed(() => {
  if (!championStats.value || !sortColumn.value || !sortDirection.value) {
    return championStats.value
  }

  return [...championStats.value].sort((a, b) => {
    const aValue = a[sortColumn.value]
    const bValue = b[sortColumn.value]

    if (sortDirection.value === 'asc') {
      return aValue > bValue ? 1 : -1
    } else {
      return aValue < bValue ? 1 : -1
    }
  })
})

const fetchChampionStats = async (filter: SearchFilter) => {
  const response = await statsApi.getChampionList(filter)
  championStats.value = response.data
}

const onFilterUpdate = (filter: SearchFilter) => {
  fetchChampionStats(filter)
}

onMounted(() => {
  fetchChampionStats({})
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* 기본 스타일 */
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

.thumb-icon.up {
  color: #4CAF50;
}

.thumb-icon.down {
  color: #FF5252;
}

/* PC 스타일 (1025px 이상) */
@media (min-width: 1025px) {
  table {
    table-layout: fixed;
    min-width: 1000px;
  }

  .play-count-column {
    display: table-cell;
  }

  .rating-separate-columns {
    display: table-cell;
  }

  .rating-combined-column {
    display: none;
  }
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

  /* 플레이수 열 숨기기 */
  .play-count-column {
    display: none;
  }

  /* 개별 up/down 컬럼 숨기기 */
  .rating-separate-columns {
    display: none;
  }

  /* 합쳐진 평가 비율 컬럼 보이기 */
  .rating-combined-column {
    display: table-cell;
  }

  /* 열 너비 최적화 */
  .desktop-table th:nth-child(1),
  .desktop-table td:nth-child(1) {
    width: 40px;
  }

  .desktop-table th:nth-child(2),
  .desktop-table td:nth-child(2) {
    width: 160px;
  }

  .desktop-table th:nth-child(4),
  .desktop-table td:nth-child(4) {
    width: 60px;
  }

  /* 통합 평가 비율 바 스타일 */
  .combined-rating-wrapper {
    width: 100%;
  }

  .combined-rating-bar {
    @apply h-1.5 bg-[#1a1a1a] rounded-full overflow-hidden relative;
  }

  .combined-rating-up {
    @apply absolute top-0 left-0 h-full bg-[#4CAF50];
  }

  .combined-rating-down {
    @apply absolute top-0 right-0 h-full bg-[#FF5252];
  }

  .combined-rating-text {
    @apply flex items-center justify-between text-xs mt-1;
  }

  .combined-rating-text span:first-child {
    @apply text-[#4CAF50];
  }

  .combined-rating-text span:last-child {
    @apply text-[#FF5252];
  }
}

/* 모바일 스타일 (640px 이하) */
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

  .mobile-header-champion {
    @apply w-36 text-left;
  }

  .mobile-header-review-count {
    @apply w-12 text-right;
  }

  .mobile-header-ratio {
    @apply w-[5.5rem] text-right;
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
    @apply flex items-center w-36 mr-2;
  }

  .mobile-summoner-image {
    @apply w-8 h-8 rounded mr-2;
  }

  .mobile-summoner-details {
    @apply flex-grow;
  }

  .mobile-summoner-name {
    @apply text-white font-semibold text-xs;
  }

  .mobile-summoner-review-count {
    @apply text-sm text-gray-400 w-12 text-center mr-2;
  }

  .mobile-summoner-ratio {
    @apply w-24 text-right ml-[30px];
  }

  .mobile-ratio-container {
    @apply h-1.5 bg-[#1a1a1a] rounded-full overflow-hidden flex mb-1;
  }

  .mobile-ratio-up {
    @apply h-full bg-[#4CAF50];
  }

  .mobile-ratio-down {
    @apply h-full bg-[#FF5252];
  }

  .mobile-ratio-text {
    @apply flex justify-between text-[10px];
  }

  .mobile-ratio-text span:first-child {
    @apply text-[#4CAF50];
  }

  .mobile-ratio-text span:last-child {
    @apply text-[#FF5252];
  }
}
</style>