<!-- src/views/SummonerMainPage.vue -->
<template>
  <div class="container">
    <Profile
        v-if="summonerInfo"
        :profile-data="summonerInfo"
        @show-detail="showDetailModal = true"
        @update-matches="updateMatchList"
    />

    <MatchList
        v-if="matches.length"
        :matches="matches"
        @review-player="openReviewModal"
    />

    <DetailModal
        v-if="showDetailModal"
        :name="summonerInfo?.riotAccountInfoEntity.gameName"
        @close="showDetailModal = false"
    />

    <ReviewModal
        v-if="showReviewModal"
        :player="selectedPlayer"
        @close="showReviewModal = false"
        :review-tags="reviewTags"
        @submit="fetchMatchList"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Profile from '@/components/summoner/SummonerProfile.vue'
import MatchList from '@/components/match/MatchList.vue'
import DetailModal from '@/components/summoner/modal/DetailModal.vue'
import ReviewModal from '@/components/summoner/modal/ReviewModal.vue'
import { summonerApi } from '@/api/summoner'
import { matchApi } from '@/api/match'
import { reviewApi } from '@/api/review'
import type { LolSummonerProfileResDto } from '@/types/summoner'
import type {LolMatchInfoRes, LolMatchParticipant} from '@/types/match'
import type {ReviewTagDto} from "@/types/review.ts";

const route = useRoute()
const summonerInfo = ref<LolSummonerProfileResDto | null>(null)
const matches = ref<LolMatchInfoRes[]>([])
const reviewTags = ref<ReviewTagDto[]>([])
const showDetailModal = ref(false)
const showReviewModal = ref(false)
const selectedPlayer = ref(<LolMatchParticipant>({}));

const fetchSummonerInfo = async () => {
  try {
    const summonerId = route.params.name as string
    const tagLine = route.params.tag as string
    const response = await summonerApi.getInfo(summonerId, tagLine)
    summonerInfo.value = response.data
  } catch (error) {
    console.error('Failed to fetch summoner info:', error)
  }
}

const fetchMatchList = async () => {
  try {
    console.log('fetchMatchList')
    if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
    const response = await matchApi.getMatchList(summonerInfo.value.riotAccountInfoEntity.puuid, 0, 3);
    matches.value = response.data
  } catch (error) {
    console.error('Failed to fetch matches:', error)
  }
}

const updateMatchList = async () => {
  try {
    console.log('updateMatchList')
    if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
    const response = await matchApi.updateMatchList(summonerInfo.value.riotAccountInfoEntity.puuid, 0, 3);
    matches.value = response.data
  } catch (error) {
    console.error('Failed to fetch matches:', error)
  }
}

const openReviewModal = (player: any) => {
  selectedPlayer.value = player
  showReviewModal.value = true
}

const fetchReviewTags = async () => {
  const response = await reviewApi.getTagList();
  reviewTags.value = response.data
}

onMounted(async () => {
  await fetchSummonerInfo()
  await fetchMatchList()
  await fetchReviewTags()
})
</script>

<style scoped>
.container {
  max-width: 1000px;
  margin: 80px auto 0;
  padding: 20px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #141414;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}
</style>