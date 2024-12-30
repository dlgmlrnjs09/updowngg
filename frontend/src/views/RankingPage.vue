<template>
  <div class="ranking-page">
    <RankingSearchSection
        :selected-period="selectedPeriod"
        :selected-position="selectedPosition"
        @update:selected-position="selectedPosition = $event"
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
const currentStartIndex = ref(0);

const fetchRankerList = async (startIndex: number = 0, limit: number = 10) => {
  const response = await rankingApi.getRankerCardList(startIndex, limit)
  rankerCards.value = response.data;

  if (startIndex === 0) {
    rankerCards.value = response.data
  } else {
    rankerCards.value = [...rankerCards.value, ...response.data]
  }

  if (rankerCards.value.length < limit) {
    isLastPage.value = true;
  }
}

const loadMore = async () => {
  if (isLoading.value || isLastPage.value) return

  isLoading.value = true
  try {
    await fetchRankerList(currentStartIndex.value + 10)
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