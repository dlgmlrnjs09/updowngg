<template>
  <div class="stats-card filter-container">
    <div class="filter-options">
      <!-- PC 필터 (데스크톱에서만 보임) -->
      <div v-if="!isMobile" class="filter-row">
        <div class="filter-label">큐 타입</div>
        <div class="filter-content">
          <button
              v-for="queue in ['전체', '솔로랭크', '자유랭크', '무작위 총력전']"
              :key="queue"
              :class="['filter-chip', { active: selectedQueue === queue }]"
              @click="updateQueue(queue)"
          >
            {{ queue }}
          </button>
        </div>
      </div>

      <!-- 모바일 큐 타입 필터 -->
      <div v-if="isMobile" class="mobile-filter-row">
        <div class="filter-item">
          <div class="filter-label">큐 타입</div>
          <select
              v-model="selectedQueue"
              class="filter-select"
              @change="updateFilter"
          >
            <option value="전체">전체</option>
            <option value="솔로랭크">솔로랭크</option>
            <option value="자유랭크">자유랭크</option>
            <option value="무작위 총력전">무작위 총력전</option>
          </select>
        </div>
      </div>

      <!-- PC 티어 필터 -->
      <div v-if="!isMobile" class="filter-row">
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

      <!-- 모바일 티어 필터 -->
      <div v-if="isMobile" class="mobile-filter-row">
        <div class="filter-item">
          <div class="filter-label">티어</div>
          <select
              v-model="selectedTier"
              class="filter-select"
              @change="updateFilter"
          >
            <option value="전체">전체</option>
            <option value="Iron">Iron</option>
            <option value="Bronze">Bronze</option>
            <option value="Silver">Silver</option>
            <option value="Gold">Gold</option>
            <option value="Platinum">Platinum</option>
            <option value="Emerald">Emerald</option>
            <option value="Diamond">Diamond</option>
            <option value="Master">Master</option>
            <option value="Grandmaster">Grandmaster</option>
            <option value="Challenger">Challenger</option>
          </select>
        </div>
      </div>

      <!-- PC 기간 필터 -->
      <div v-if="!isNotShowPeriod && !isMobile" class="filter-row">
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

      <!-- 모바일 기간 필터 -->
      <div v-if="!isNotShowPeriod && isMobile" class="mobile-filter-row">
        <div class="filter-item">
          <div class="filter-label">기간</div>
          <select
              v-model="selectedPeriod"
              class="filter-select"
              @change="updateFilter"
          >
            <option value="전체">전체</option>
            <option value="최근 1달">최근 1달</option>
            <option value="최근 7일">최근 7일</option>
            <option value="오늘">오늘</option>
          </select>
        </div>
      </div>

      <!-- PC 포지션 필터 -->
      <div v-if="selectedQueue !== '무작위 총력전' && !isMobile" class="filter-row">
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

      <!-- 모바일 포지션 필터 -->
      <div v-if="selectedQueue !== '무작위 총력전' && isMobile" class="mobile-filter-row">
        <div class="filter-item">
          <div class="filter-label">포지션</div>
          <select
              v-model="selectedPosition"
              class="filter-select"
              @change="updateFilter"
          >
            <option value="전체">전체</option>
            <option value="탑">탑</option>
            <option value="정글">정글</option>
            <option value="미드">미드</option>
            <option value="바텀">바텀</option>
            <option value="서포터">서포터</option>
          </select>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, watch, onMounted, onUnmounted} from 'vue'
import {MatchGameMode, MatchPosition, Period} from "@/types/match.ts"
import type {SearchFilter} from "@/types/stats.ts"
import type {Tier} from "@/types/league.ts"

// 모바일 상태 관리
const isMobile = ref(window.innerWidth <= 640)

// 필터 상태
const selectedQueue = ref('전체')
const selectedTier = ref('전체')
const selectedPeriod = ref('전체')
const selectedPosition = ref('전체')

const props = defineProps<{
  isNotShowPeriod?: boolean;
}>()

// 이벤트 emit 정의
const emit = defineEmits<{
  (e: 'update:filter', filter: SearchFilter): void
}>()

// 반응형 처리
const handleResize = () => {
  isMobile.value = window.innerWidth <= 640
}

// 큐 타입 매핑
const queueMapping: Record<string, MatchGameMode> = {
  '전체' : MatchGameMode.ALL,
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

// 기간 매핑
const periodMapping: Record<string, Period> = {
  '전체': Period.ALL,
  '최근 1달': Period.MONTH,
  '최근 7일': Period.WEEK,
  '오늘': Period.TODAY
}

// PC 버전 업데이트 함수들
const updateQueue = (queue: string) => {
  selectedQueue.value = queue
  updateFilter()
}

const updateTier = (tier: string) => {
  selectedTier.value = tier
  updateFilter()
}

const updatePeriod = (period: string) => {
  selectedPeriod.value = period
  updateFilter()
}

const updatePosition = (position: string) => {
  selectedPosition.value = position
  updateFilter()
}

// 공통 필터 업데이트 함수
const updateFilter = () => {
  const filter: SearchFilter = {}

  if (selectedQueue.value !== '전체') {
    filter.queueType = queueMapping[selectedQueue.value]
  }

  if (selectedTier.value !== '전체') {
    filter.tier = selectedTier.value.toUpperCase() as Tier
  }

  if (selectedPosition.value !== '전체' && selectedQueue.value !== '무작위 총력전') {
    filter.position = positionMapping[selectedPosition.value]
  }

  // 기간 필터 수정
  if (selectedPeriod.value !== '전체') {
    filter.period = periodMapping[selectedPeriod.value]
  }

  emit('update:filter', filter)
}

// 이벤트 리스너 추가 및 제거
onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
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

/* 모바일 대응 */
@media (max-width: 640px) {
  .mobile-filter-row {
    @apply flex items-center p-2 border-b border-[#ffffff1a];
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .mobile-filter-row {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 8px;
  }

  .filter-item {
    @apply flex-1 px-1;
    min-width: 0; /* 플렉스 아이템의 최소 너비 설정 */
  }


  .filter-select {
    @apply w-full bg-[#1a1a1a] text-white text-xs rounded border border-[#ffffff1a] py-1 px-2 outline-none;
  }
}
</style>