<template>
  <div class="ranking-page">
    <RankingSearchSection
        :period="selectedPeriod"
        :position="selectedPosition"
    />

    <RankingResultSection
        :ranker-cards="rankerCards"
        :is-loading="isLoading"
        :is-last-page="isLastPage"
        @load-more="loadMore"
    />
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'
import RankingSearchSection from '@/components/ranking/RankingSearchSection.vue'
import RankingResultSection from '@/components/ranking/RankingResultSection.vue'
import type {Player, RankingCard} from '@/types/ranking'
import {summonerApi} from "@/api/summoner.ts";
import {useRouter} from "vue-router";
import {rankingApi} from "@/api/ranking.ts";

const selectedPeriod = ref('all')
const selectedPosition = ref('all')
const isLoading = ref(false)
const isLastPage = ref(false)
const router = useRouter();
const rankerCards = ref<RankingCard[] | null>(null);

const fetchRankerList = async () => {
  const response = await rankingApi.getRankerCardList(0, 10)
  rankerCards.value = response.data;
}

const loadMore = async () => {
  if (isLoading.value) return

  isLoading.value = true
  try {
    // API 호출 로직
    await new Promise(resolve => setTimeout(resolve, 1000))
    // rankings.value.push(...newData)
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  await fetchRankerList();
})
</script>

<style scoped>
.ranking-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  margin-top: 75px;
}
</style>