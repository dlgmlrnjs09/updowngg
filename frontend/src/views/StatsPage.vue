<template>
  <div class="stats-page max-w-[1200px] mx-auto px-6 mt-[20px]">
    <FilterSection @update:filter="onFilterUpdate" />

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
                <TagList :tags="champion.topTagList" size="small" is-show-count/>
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
import {computed, onMounted, ref} from 'vue'
import {statsApi} from "@/api/stats.ts"
import {ArrowUpDown, ThumbsDown, ThumbsUp} from 'lucide-vue-next'
import type {ChampionResDto, SearchFilter} from "@/types/stats.ts"
import FilterSection from '@/components/common/SearchFilter.vue'
import TagList from "@/components/common/TagList.vue"
import {MatchGameMode} from "@/types/match.ts";

const championStats = ref<ChampionResDto[] | null>(null)
const sortColumn = ref<string>('upRatio')
const sortDirection = ref<'asc' | 'desc' | null>('desc')

// 정렬 토글 함수
const toggleSort = (column: string) => {
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
  console.log('필터 == ' + JSON.stringify(filter))
  fetchChampionStats(filter)
}

onMounted(() => {
  fetchChampionStats({"queueType": MatchGameMode.SOLO_RANK})
})
</script>

<style scoped>
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

table {
  table-layout: fixed;
  min-width: 1000px;
}
</style>