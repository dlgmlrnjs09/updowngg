<template>
  <div class="space-y-4 mt-5">
    <!-- 필터 섹션 -->
    <div class="flex items-center gap-2 md:gap-3 p-2 md:p-3 bg-[#141414] rounded-lg border border-gray-800 overflow-x-auto whitespace-nowrap">
      <!-- 필터 아이콘 -->
      <Filter class="w-4 h-4 md:w-5 md:h-5 text-gray-400" />

      <!-- 게임 모드 드롭다운 -->
      <select
          v-model="selectedGameMode"
          class="min-w-[90px] md:min-w-[150px] px-2 md:px-3 py-1 md:py-1.5 bg-gray-700 text-gray-200 text-xs md:text-sm rounded-md border border-gray-600 focus:outline-none focus:border-blue-500"
      >
        <option v-for="mode in gameModes" :key="mode.id" :value="mode.id">
          {{ mode.name }}
        </option>
      </select>

      <!-- 구분선 -->
      <div class="h-5 md:h-6 w-px bg-gray-700"></div>

      <!-- 평가 필터 토글 버튼들 -->
      <div class="flex gap-1.5 md:gap-2">
        <button
            @click="toggleReviewFilter('reviewed')"
            :class="[
            'px-2 md:px-3 py-1 md:py-1.5 text-xs md:text-sm rounded-md transition-colors whitespace-nowrap',
            showReviewed ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
          ]"
        >
          <span class="hidden md:inline">평가받은 게임</span>
          <span class="inline md:hidden">평가됨</span>
        </button>
        <button
            v-if="authStore?.user?.puuid == profileData.riotAccountInfoEntity?.puuid"
            @click="toggleReviewFilter('unreviewed')"
            :class="[
              'px-2 md:px-3 py-1 md:py-1.5 text-xs md:text-sm rounded-md transition-colors whitespace-nowrap',
              showUnreviewed ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
            ]"
        >
          <span class="hidden md:inline">평가하지 않은 게임</span>
          <span class="inline md:hidden">미평가</span>
        </button>
      </div>
    </div>

    <!-- 매치 목록 -->
    <div v-if="matches.length > 0" class="games-section">
      <div v-for="match in matches" :key="match.matchInfo.matchId" class="mb-4">
        <MatchSummary
            :match="match"
            :profile-data="profileData"
            @review-player="openReview"
        />
      </div>

      <div v-if="!noMoreMatches" class="mt-4">
        <button
            class="load-more-button"
            @click="$emit('loadMore')"
            :disabled="isLoading"
        >
          <div class="flex justify-center items-center">
            <span v-if="!isLoading">더보기</span>
            <div v-else class="animate-spin w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full"></div>
          </div>
        </button>
      </div>

      <div v-if="noMoreMatches" class="text-center py-4 text-gray-500">
        더 이상 매치 기록이 없습니다
      </div>
    </div>

    <MatchDataEmpty v-else />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import type { LolMatchInfoRes } from '@/types/match';
import type { LolSummonerProfileResDto } from '@/types/summoner';
import MatchSummary from '@/components/match/MatchSummary.vue';
import MatchDataEmpty from "@/components/match/MatchDataEmpty.vue";
import { Filter } from 'lucide-vue-next';
import {useAuthStore} from "@/stores/auth.ts";

const props = defineProps<{
  matches: LolMatchInfoRes[];
  profileData: LolSummonerProfileResDto;
  isLoading: boolean;
  noMoreMatches: boolean;
}>();

const emit = defineEmits<{
  (e: 'reviewPlayer', player: any): void;
  (e: 'loadMore'): void;
  (e: 'filterChange', filters: {
    gameMode: string;
    reviewFilter?: 'reviewed' | 'unreviewed';
  }): void;
}>();

const selectedGameMode = ref('ALL');
const showReviewed = ref(false);
const showUnreviewed = ref(false);
const authStore = useAuthStore();

const gameModes = [
  { id: 'ALL', name: '전체' },
  { id: 'RANKED_SOLO_5x5', name: '솔로랭크' },
  { id: 'RANKED_FLEX_SR', name: '자유랭크' },
  { id: 'ARAM', name: '무작위 총력전' },
  { id: 'NORMAL', name: '일반' },
  { id: 'BOT', name: 'AI 대전' },
  { id: 'URF', name: '우르프' },
  { id: 'CLASH', name: '격전' },
  { id: 'ARENA', name: '아레나' },
  { id: 'NEXUS_BLITZ', name: '돌격! 넥서스' },
  { id: 'SPECIAL', name: '특별 게임모드' }
];

const toggleReviewFilter = (type: 'reviewed' | 'unreviewed') => {
  if (type === 'reviewed') {
    showReviewed.value = !showReviewed.value;
    if (showReviewed.value) showUnreviewed.value = false;
  } else {
    showUnreviewed.value = !showUnreviewed.value;
    if (showUnreviewed.value) showReviewed.value = false;
  }
  emitFilterChange();
};

watch(selectedGameMode, () => {
  emitFilterChange();
});

const emitFilterChange = () => {
  let reviewFilter: 'reviewed' | 'unreviewed' | undefined;
  if (showReviewed.value) reviewFilter = 'reviewed';
  if (showUnreviewed.value) reviewFilter = 'unreviewed';

  emit('filterChange', {
    gameMode: selectedGameMode.value,
    reviewFilter
  });
};

const openReview = (player: any) => {
  emit('reviewPlayer', player);
};
</script>

<style scoped>
.games-section {
  background: #141414;
  border-radius: 12px;
  padding: 10px;
  border: 1px solid rgba(255,255,255,.05);
}

.load-more-button {
  background: #141414;
  border: 1px solid #2979FF;
  color: #2979FF;
  padding: 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
  height: 48px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.load-more-button:hover:not(:disabled) {
  background: rgba(41, 121, 255, 0.1);
}

select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='white'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.5rem center;
  background-size: 1.5em 1.5em;
  padding-right: 2rem;
}
</style>