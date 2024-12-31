# FilterSection.vue
<template>
  <div class="stats-card">
    <div class="filter-options">
      <!-- 큐 타입 필터 -->
      <div class="filter-row">
        <div class="filter-label">큐 타입</div>
        <div class="filter-content">
          <button
              v-for="queue in ['솔로랭크', '자유랭크', '무작위 총력전']"
              :key="queue"
              :class="['filter-chip', { active: selectedQueue === queue }]"
              @click="updateQueue(queue)"
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
              @click="updateTier(tier)"
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
              @click="updatePeriod(period)"
          >
            {{ period }}
          </button>
        </div>
      </div>

      <!-- 포지션 필터 -->
      <div class="filter-row" v-if="selectedQueue !== '무작위 총력전'">
        <div class="filter-label">포지션</div>
        <div class="filter-content">
          <button
              v-for="position in ['전체', '탑', '정글', '미드', '바텀', '서포터']"
              :key="position"
              :class="['filter-chip', { active: selectedPosition === position }]"
              @click="updatePosition(position)"
          >
            {{ position }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { MatchGameMode, MatchPosition } from "@/types/match.ts"
import type { SearchFilter } from "@/types/stats.ts"
import type { Tier } from "@/types/league.ts"

// 필터 상태
const selectedQueue = ref('솔로랭크')
const selectedTier = ref('전체')
const selectedPeriod = ref('전체')
const selectedPosition = ref('전체')

// 이벤트 emit 정의
const emit = defineEmits<{
  (e: 'update:filter', filter: SearchFilter): void
}>()

// 큐 타입 매핑
const queueMapping: Record<string, MatchGameMode> = {
  '솔로랭크': MatchGameMode.SOLO_RANK,
  '자유랭크': MatchGameMode.FLEX_RANK,
  '무작위 총력전': MatchGameMode.ARAM,
  '일반게임': MatchGameMode.NORMAL,
}

// 포지션 매핑
const positionMapping: Record<string, MatchPosition> = {
  '탑': MatchPosition.TOP,
  '정글': MatchPosition.JUNGLE,
  '미드': MatchPosition.MIDDLE,
  '바텀': MatchPosition.BOTTOM,
  '서포터': MatchPosition.UTILITY,
}

// 업데이트 함수들
const updateQueue = (queue: string) => {
  selectedQueue.value = queue
}

const updateTier = (tier: string) => {
  selectedTier.value = tier
}

const updatePeriod = (period: string) => {
  selectedPeriod.value = period
}

const updatePosition = (position: string) => {
  selectedPosition.value = position
}

// 필터 변경 감지 및 이벤트 발생
watch(
    [selectedQueue, selectedTier, selectedPosition, selectedPeriod],
    () => {
      const filter: SearchFilter = {}

      // 솔로랭크는 기본값이므로 항상 포함
      filter.queueType = queueMapping[selectedQueue.value]

      if (selectedTier.value !== '전체') {
        filter.tier = selectedTier.value.toUpperCase() as Tier
      }

      if (selectedPosition.value !== '전체' && selectedQueue.value !== '무작위 총력전') {
        filter.position = positionMapping[selectedPosition.value]
      }

      if (selectedPeriod.value !== '전체') {
        filter.period = selectedPeriod.value
      }

      emit('update:filter', filter)
    },
    { immediate: true } // 즉시 실행 옵션 추가
)
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
</style>