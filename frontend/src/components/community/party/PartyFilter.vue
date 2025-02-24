<!-- PartyFilter.vue -->
<template>
  <div class="flex flex-col space-y-4 sm:flex-row sm:justify-between sm:items-center mb-8 sm:mb-14">
    <div class="grid grid-cols-2 gap-2 sm:bg-[#141414] sm:p-3 sm:rounded-xl sm:flex sm:gap-3 sm:flex-1 sm:mr-4">
      <!-- 게임 모드 선택 -->
      <select
          v-model="selectedGameMode"
          class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
          @change="onFilterUpdate"
      >
        <option value="">모든 큐</option>
        <option value="SOLO_RANK">솔로랭크</option>
        <option value="FLEX_RANK">자유랭크</option>
        <option value="NORMAL">일반게임</option>
        <option value="ARAM">칼바람나락</option>
      </select>

      <!-- 포지션 선택 -->
      <select
          v-model="selectedPosition"
          class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
          @change="onFilterUpdate"
      >
        <option value="">모든 포지션</option>
        <option value="TOP">탑</option>
        <option value="JG">정글</option>
        <option value="MID">미드</option>
        <option value="AD">원딜</option>
        <option value="SUP">서포터</option>
      </select>

      <!-- 티어 선택 -->
      <select
          v-model="selectedTier"
          class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF] col-span-2 sm:col-span-1"
          @change="onFilterUpdate"
      >
        <option value="">모든 티어</option>
        <option value="IRON">아이언</option>
        <option value="BRONZE">브론즈</option>
        <option value="SILVER">실버</option>
        <option value="GOLD">골드</option>
        <option value="PLATINUM">플래티넘</option>
      </select>
    </div>

    <!-- 듀오 등록 버튼 -->
    <button
        @click="$emit('write')"
        class="w-full sm:w-auto bg-[#2979FF] text-white px-4 py-3 sm:py-1.5 rounded-lg hover:bg-[#2565D1] transition-colors text-sm font-medium sm:h-12"
    >
      {{ isMobile ? '듀오 등록' : '듀오 등록하기' }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

// Props
interface Props {
  initialGameMode?: string
  initialPosition?: string
  initialTier?: string
}

const props = withDefaults(defineProps<Props>(), {
  initialGameMode: '',
  initialPosition: '',
  initialTier: ''
})

// Emits
const emit = defineEmits<{
  (e: 'filter-update', filter: { gameMode: string; position: string; tier: string }): void
  (e: 'write'): void
}>()

// Reactive state
const selectedGameMode = ref(props.initialGameMode)
const selectedPosition = ref(props.initialPosition)
const selectedTier = ref(props.initialTier)

// Computed
const isMobile = computed(() => window.innerWidth < 640)

// Methods
const onFilterUpdate = () => {
  emit('filter-update', {
    gameMode: selectedGameMode.value,
    position: selectedPosition.value,
    tier: selectedTier.value
  })
}
</script>