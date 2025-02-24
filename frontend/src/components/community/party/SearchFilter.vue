// searchFilter.vue
<template>
  <div class="flex flex-col space-y-4 sm:flex-row sm:justify-between sm:items-center mb-8 sm:mb-14">
    <!-- 필터 그룹 -->
    <div class="grid grid-cols-2 gap-2 sm:bg-[#141414] sm:p-3 sm:rounded-xl sm:flex sm:gap-3 sm:flex-1 sm:mr-4">
      <select
          v-model="selectedFilters.gameMode"
          class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
          @change="handleFilterChange"
      >
        <option value="">모든 큐</option>
        <option v-for="(name, mode) in gameModes" :key="mode" :value="mode">
          {{ name }}
        </option>
      </select>

      <select
          v-model="selectedFilters.position"
          class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
          @change="handleFilterChange"
      >
        <option value="">모든 포지션</option>
        <option v-for="(name, position) in positions" :key="position" :value="position">
          {{ name }}
        </option>
      </select>

      <select
          v-model="selectedFilters.tier"
          class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF] col-span-2 sm:col-span-1"
          @change="handleFilterChange"
      >
        <option value="">모든 티어</option>
        <option v-for="(name, tier) in tiers" :key="tier" :value="tier">
          {{ name }}
        </option>
      </select>
    </div>

    <!-- 등록 버튼 -->
    <button
        @click="$emit('openWriteModal')"
        class="w-full sm:w-auto bg-[#2979FF] text-white px-4 py-3 sm:py-1.5 rounded-lg hover:bg-[#2565D1] transition-colors text-sm font-medium sm:h-12"
    >
      {{ isMobile ? '듀오 등록' : '듀오 등록하기' }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface FilterOptions {
  gameMode: string
  position: string
  tier: string
}

// Props 정의
interface Props {
  initialFilters?: FilterOptions
}

// Emits 정의
const emit = defineEmits<{
  (e: 'filter', filters: FilterOptions): void
  (e: 'openWriteModal'): void
}>()

const props = withDefaults(defineProps<Props>(), {
  initialFilters: () => ({
    gameMode: '',
    position: '',
    tier: ''
  })
})

// 필터 옵션 상수
const gameModes = {
  'SOLO_RANK': '솔로랭크',
  'FLEX_RANK': '자유랭크',
  'NORMAL': '일반게임',
  'ARAM': '칼바람나락'
} as const

const positions = {
  'TOP': '탑',
  'JG': '정글',
  'MID': '미드',
  'AD': '원딜',
  'SUP': '서포터'
} as const

const tiers = {
  'IRON': '아이언',
  'BRONZE': '브론즈',
  'SILVER': '실버',
  'GOLD': '골드',
  'PLATINUM': '플래티넘'
} as const

// 반응형 상태 관리
const selectedFilters = ref<FilterOptions>({ ...props.initialFilters })
const isMobile = computed(() => window.innerWidth < 640)

// 필터 변경 핸들러
const handleFilterChange = () => {
  emit('filter', selectedFilters.value)
}

// 필터 초기화 메서드 (외부에서 호출 가능)
const resetFilters = () => {
  selectedFilters.value = {
    gameMode: '',
    position: '',
    tier: ''
  }
  handleFilterChange()
}

// 컴포넌트 메서드를 외부로 노출
defineExpose({
  resetFilters,
  selectedFilters
})
</script>