# SummonerRankingPage.vue
<template>
  <div class="stats-page max-w-[1200px] mx-auto px-6 mt-[100px]">
    <FilterSection @update:filter="onFilterUpdate" />

    <div class="stats-card">
      <div class="champion-table" style="min-width: 0px;">
        <table class="w-full">
          <thead>
          <tr class="text-left border-b border-[#ffffff1a]">
            <th class="py-2 px-4 text-gray-300 w-12">#</th>
            <th class="py-2 px-4 text-gray-300 w-52">소환사</th>
            <th class="py-2 px-4 text-gray-300 w-24">플레이수</th>
            <th class="py-2 px-4 text-gray-300 w-24">평가수</th>
            <th class="py-2 px-4 text-gray-300 w-48">전체 평가</th>
            <th class="py-2 px-4 text-gray-300 w-52">모스트 챔피언별 평가</th>
            <th class="py-2 px-4 text-gray-300">자주 받은 태그</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(player, index) in rankerPlayers"
              :key="player.summonerBasicInfoDto.puuid"
              class="border-b border-[#ffffff1a] hover:bg-[#ffffff0a]">
            <td class="py-2 px-4 text-gray-300 text-sm">{{ index + 1 }}</td>
            <td class="py-2 px-4 text-gray-400">
              <div class="flex items-center gap-3 player-name-wrapper">
                <img :src="player.summonerBasicInfoDto.profileIconUrl" :alt="player.summonerBasicInfoDto.gameName" class="w-8 h-8 rounded">
                <span class="player-name text-sm" @click.stop="goSelectedSummonerProfile(player.summonerBasicInfoDto.gameName, player.summonerBasicInfoDto.tagLine)">{{ player.summonerBasicInfoDto.gameName }}</span>
                <div class="player-tag">#{{ player.summonerBasicInfoDto.tagLine }}</div>
              </div>
            </td>
            <td class="py-2 px-4 text-sm text-gray-400">{{player.reviewStatsDto.playCount}} </td>
            <td class="py-2 px-4 text-sm text-gray-400">{{ player.reviewStatsDto.totalReviewCnt }}</td>
            <td class="py-2 px-4 text-gray-400">
              <div class="flex items-center justify-between gap-2">
                <span class="text-sm text-[#4CAF50]">{{ player.reviewStatsDto.upRatio }}%</span>
                <div class="w-56 h-1.5 bg-[#1a1a1a] rounded-full overflow-hidden relative">
                  <div class="absolute top-0 left-0 h-full bg-[#4CAF50]"
                       :style="{ width: `${player.reviewStatsDto.upRatio}%` }">
                  </div>
                  <div class="absolute top-0 right-0 h-full bg-[#FF5252]"
                       :style="{ width: `${player.reviewStatsDto.downRatio}%` }">
                  </div>
                </div>
                <span class="text-sm text-[#FF5252]">{{ player.reviewStatsDto.downRatio }}%</span>
              </div>
            </td>
            <td class="py-2 px-4 text-gray-400">
              <div class="flex items-center gap-4">
                <div v-for="champion in player.championStatsDtoList" :key="champion.nameUs"
                     class="flex flex-col items-center gap-1">
                  <img :src="champion.iconUrl" :alt="champion.nameKr" class="w-8 h-8 rounded">
                  <div class="w-8 h-1.5 bg-[#1a1a1a] rounded-full overflow-hidden relative">
                    <div class="absolute top-0 left-0 h-full bg-[#4CAF50]"
                         :style="{ width: `${champion.upRatio}%` }">
                    </div>
                    <div class="absolute top-0 right-0 h-full bg-[#FF5252]"
                         :style="{ width: `${champion.downRatio}%` }">
                    </div>
                  </div>
                </div>
              </div>
            </td>
            <td class="py-4 px-4">
              <div class="flex gap-2">
                <TagList :tags="player.reviewTagDtoList" size="small"/>
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
import { onMounted, ref } from 'vue'
import { statsApi } from "@/api/stats.ts"
import FilterSection from '@/components/common/SearchFilter.vue'
import TagList from "@/components/common/TagList.vue"
import {rankingApi} from "@/api/ranking.ts";
import type {RankerPlayer, RankingSearchFilter} from "@/types/ranking.ts";
import type {SearchFilter} from "@/types/stats.ts";
import {goSelectedSummonerProfile} from "@/common.ts";

const rankerPlayers = ref<RankerPlayer[] | null>(null)

const fetchSummonerStats = async (filter: SearchFilter) => {
  // TODO : 추후 페이징처리 필요
  const reqDto: RankingSearchFilter = {
    sortTypeReqDto: filter,
    limit: 100,
    offset: 0
  };

  console.log('필터 == ' + JSON.stringify(filter))
  console.log('reqDto === ' + JSON.stringify(reqDto));
  const response = await rankingApi.getRankerCardList(reqDto);
  rankerPlayers.value = response.data;
}

const onFilterUpdate = (filter: SearchFilter) => {
  fetchSummonerStats(filter)
}

onMounted(() => {
  fetchSummonerStats({})
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

table {
  table-layout: fixed;
  /*min-width: 1200px;*/
}

.player-tag {
  font-size: 12px;
  color: #666;
}

.player-name-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.player-name {
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: all 0.2s ease;
  cursor: pointer;
}

.player-name:hover {
  color: #2979FF;
  text-decoration: underline;
}
</style>