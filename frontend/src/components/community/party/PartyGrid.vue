<!-- PartyGrid.vue -->
<template>
  <div>
    <!-- 듀오 카드 그리드 -->
    <transition-group
        name="card-transition"
        tag="div"
        class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3"
    >
      <PartyCard
          v-for="card in cards"
          :key="card.postId"
          :card="card"
          :my-puuid="myPuuid"
          :applied-positions="appliedPositions"
          @apply="handleApply"
          @profile-click="handleProfileClick"
      />
    </transition-group>

    <!-- 더보기 버튼 -->
    <div v-if="showReadMore" class="mt-6">
      <button
          class="w-full bg-[#141414] border border-[#2979FF] text-[#2979FF] py-3 rounded-lg text-sm font-medium hover:bg-[rgba(41,121,255,0.1)] transition-all disabled:opacity-70 disabled:cursor-not-allowed disabled:border-[#4a4a4a] disabled:text-[#4a4a4a]"
          @click="handleLoadMore"
          :disabled="isLoading"
      >
        <div class="flex justify-center items-center h-5">
          <span v-if="!isLoading">더보기</span>
          <div v-else class="spinner"></div>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs } from 'vue'
import PartyCard from './PartyCard.vue'
import type { PartyPostCardDto, SummonerBasicInfoDto } from "@/types/community"
import { goSelectedSummonerProfile } from "@/utils/common"

interface Props {
  cards: PartyPostCardDto[]
  myPuuid: string
  appliedPositions: Map<string, boolean>
  isLoading?: boolean
  showReadMore?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isLoading: false,
  showReadMore: true
})

const { cards, myPuuid, appliedPositions, isLoading, showReadMore } = toRefs(props)

const emit = defineEmits<{
  (e: 'apply', postId: number, position: string): void
  (e: 'load-more'): void
}>()

// Methods
const handleApply = (postId: number, position: string) => {
  emit('apply', postId, position)
}

const handleLoadMore = () => {
  emit('load-more')
}

const handleProfileClick = (summoner: SummonerBasicInfoDto) => {
  goSelectedSummonerProfile(summoner.gameName, summoner.tagLine)
}
</script>

<style scoped>
.spinner {
  border: 2px solid transparent;
  border-top: 2px solid #2979FF;
  border-right: 2px solid #2979FF;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  animation: spin 1s linear infinite;
}

.card-transition-move {
  transition: transform 0.5s ease;
}

.card-transition-enter-active {
  transition: all 0.5s ease;
}

.card-transition-leave-active {
  transition: all 0.5s ease;
  position: absolute;
  width: calc(100% - 1rem);
}

.card-transition-enter-from,
.card-transition-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 반응형에 따른 카드 너비 조정 */
@media (min-width: 640px) {
  .card-transition-leave-active {
    width: calc(50% - 1rem);
  }
}

@media (min-width: 1024px) {
  .card-transition-leave-active {
    width: calc(33.333% - 1rem);
  }
}
</style>