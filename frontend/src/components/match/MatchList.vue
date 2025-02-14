<!-- src/components/match/MatchList.vue -->
<template>
  <div v-if="matches.length > 0" class="games-section">
    <div v-for="match in matches" :key="match.matchInfo.matchId" class="mb-4 mt-4">
      <MatchSummary
          :match="match"
          :profile-data="profileData"
          @review-player="openReview"
      />
    </div>

    <!-- 더보기 버튼 -->
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

    <!-- 더 이상 데이터가 없을 때 표시 -->
    <div v-if="noMoreMatches" class="text-center py-4 text-gray-500">
      더 이상 매치 기록이 없습니다
    </div>
  </div>

  <!-- 매치 데이터가 없을 때 -->
  <MatchDataEmpty v-else />
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { LolMatchInfoRes } from '@/types/match';
import type { LolSummonerProfileResDto } from '@/types/summoner';
import MatchSummary from '@/components/match/MatchSummary.vue';
import MatchDataEmpty from "@/components/match/MatchDataEmpty.vue";

defineProps<{
  matches: LolMatchInfoRes[];
  profileData: LolSummonerProfileResDto;
  isLoading: boolean;
  noMoreMatches: boolean;
}>();

const emit = defineEmits<{
  (e: 'reviewPlayer', player: any): void;
  (e: 'loadMore'): void;
}>();

const openReview = (player: any) => {
  emit('reviewPlayer', player);
};
</script>

<style>
  .games-section {
    background: #141414;
    border-radius: 12px;
    padding: 10px;
    border: 1px solid rgba(255,255,255,.05);
    margin-top: 15px;
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
</style>