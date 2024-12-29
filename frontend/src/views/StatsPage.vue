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
                v-for="queue in ['전체', '솔로랭크', '자유랭크', '무작위 총력전', '우르프']"
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

        <!-- 포지션 필터 -->
        <div class="filter-row">
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
            <th class="py-2 px-4 text-gray-300">#</th>
            <th class="py-2 px-4 text-gray-300">챔피언</th>
            <th class="py-2 px-4 text-gray-300">플레이 수</th>
            <th class="py-2 px-4 text-gray-300">평가받은 수</th>
            <th class="py-2 px-4 text-gray-300"><ThumbsUp class="thumb-icon up" :size="20" /></th>
            <th class="py-2 px-4 text-gray-300"><ThumbsDown class="thumb-icon down" :size="20"/></th>
            <th class="py-2 px-4 text-gray-300">자주 받은 태그</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(champion, index) in championStats"
              :key="champion.name"
              class="border-b border-[#ffffff1a] hover:bg-[#ffffff0a]">
            <td class="py-3 px-4 text-gray-300 text-sm">{{ index + 1 }}</td>
            <td class="py-3 px-4 text-gray-400">
              <div class="flex items-center gap-3">
                <img :src="champion.iconUrl" :alt="champion.name" class="w-8 h-8 rounded">
                <span class="text-sm">{{ champion.name }}</span>
              </div>
            </td>
            <td class="py-3 px-4 text-sm text-gray-400">{{ champion.playCount.toLocaleString() }}</td>
            <td class="py-3 px-4 text-sm text-gray-400">{{ champion.reviewCount.toLocaleString() }}</td>
            <td class="py-3 px-4 text-gray-400">
              <div class="flex items-center gap-2">
                <div class="w-24 h-1.5 bg-[#1a1a1a] rounded-full overflow-hidden">
                  <div
                      class="h-full bg-[#4CAF50]"
                      :style="{ width: `${champion.likeRatio}%` }"
                  ></div>
                </div>
                <span class="text-sm">{{ champion.likeRatio }}%</span>
              </div>
            </td>
            <td class="py-3 px-4 text-gray-400">
              <div class="flex items-center gap-2">
                <div class="w-24 h-1.5 bg-[#1a1a1a] rounded-full overflow-hidden">
                  <div
                      class="h-full bg-[#FF5252]"
                      :style="{ width: `${champion.dislikeRatio}%` }"
                  ></div>
                </div>
                <span class="text-sm">{{ champion.dislikeRatio }}%</span>
              </div>
            </td>
            <td class="py-4 px-4">
              <div class="flex gap-2">
                  <span v-for="tag in champion.topTags"
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
import { ref } from 'vue'
import {statsApi} from "@/api/stats.ts";
import { ThumbsUp, ThumbsDown } from 'lucide-vue-next'

// 필터 상태
const selectedQueue = ref('전체')
const selectedTier = ref('전체')
const selectedPeriod = ref('전체')
const selectedPosition = ref('전체')

// 예시 데이터
const championStats = ref([
  {
    name: '케이틀린',
    iconUrl: '/champions/caitlyn.png',
    playCount: 1301379,
    reviewCount: 28822,
    likeRatio: 50.4,
    dislikeRatio: 49.6,
    topTags: ['CS압도', '라인전', '캐리']
  },
  {
    name: '럭스',
    iconUrl: '/champions/lux.png',
    playCount: 1145046,
    reviewCount: 25360,
    likeRatio: 51.48,
    dislikeRatio: 48.52,
    topTags: ['포킹', '딜량', '유틸']
  },
  // ... 더미 데이터 생략
])
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

</style>