<template>
  <div class="container">
    <template v-if="isLoading">
      <ProfileSkeleton />
      <MatchListSkeleton class="mt-6" />
    </template>

    <template v-else>
      <Profile
          v-if="summonerInfo"
          :profile-data="summonerInfo"
          :review-stats="reviewStatsInfo"
          :frequent-tags="frequentTags"
          :recent-reviews="recentReviews"
          :is-updated-match-list="isUpdatedMatchList"
          @show-detail="showDetailModal = true"
          @update-matches="updateMatchList"
      />

      <MatchList
          v-if="matches.length"
          :matches="matches"
          :profile-data="summonerInfo"
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
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watchEffect } from 'vue'
import { useRoute } from 'vue-router'
import Profile from '@/components/summoner/SummonerProfile.vue'
import MatchList from '@/components/match/MatchList.vue'
import DetailModal from '@/components/summoner/modal/DetailModal.vue'
import ReviewModal from '@/components/summoner/modal/ReviewModal.vue'
import ProfileSkeleton from '@/components/summoner/SummonerProfileSkeleton.vue'
import MatchListSkeleton from '@/components/match/MatchListSkeleton.vue'
import { summonerApi } from '@/api/summoner'
import { matchApi } from '@/api/match'
import { reviewApi } from '@/api/review'
import type { LolSummonerProfileResDto } from '@/types/summoner'
import type { LolMatchInfoRes, LolMatchParticipant } from '@/types/match'
import type {ReviewRequestDto, ReviewStatsDto, ReviewTagDto} from "@/types/review.ts";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/auth.ts";

const toast = useToast();
const authStore = useAuthStore();
const route = useRoute();
const isLoading = ref(true);
const isUpdatedMatchList = ref(false)

const summonerInfo = ref<LolSummonerProfileResDto | null>(null)
const reviewStatsInfo = ref<ReviewStatsDto | null>(null)
const matches = ref<LolMatchInfoRes[]>([])
const reviewTags = ref<ReviewTagDto[]>([])
const frequentTags = ref<ReviewTagDto | null>(null)
const recentReviews = ref<ReviewRequestDto | null>(null)
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

const fetchSummonerReviewStats = async () => {
  try {
    if (summonerInfo.value) {
      const response = await reviewApi.getReviewStats(summonerInfo?.value.riotAccountInfoEntity.puuid);
      reviewStatsInfo.value = response.data;
    }
  } catch (error) {
    console.error('Failed to fetch summoner review stats:', error)
  }
}

const fetchMatchList = async () => {
  try {
    if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
    const response = await matchApi.getMatchList(summonerInfo.value.riotAccountInfoEntity.puuid, 0, 5);
    matches.value = response.data
  } catch (error) {
    console.error('Failed to fetch matches:', error)
  }
}

const updateMatchList = async () => {
  try {
    if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
    isUpdatedMatchList.value = true
    const response = await matchApi.updateMatchList(summonerInfo.value.riotAccountInfoEntity.puuid, 0, 5);
    matches.value = response.data
  } catch (error) {
    console.error('Failed to fetch matches:', error)
  } finally {
    isUpdatedMatchList.value = false
  }
}

const openReviewModal = (player: any) => {
  selectedPlayer.value = player
  if (authStore.isAuthenticated) {
    showReviewModal.value = true
  } else {
    toast.error('리뷰작성은 로그인 후에 가능합니다.')
  }
}

const fetchReviewTags = async () => {
  const response = await reviewApi.getTagList();
  reviewTags.value = response.data
}

const fetchFrequentTags = async () => {
  if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
  const response = await reviewApi.getReviewTagFrequent(summonerInfo.value.riotAccountInfoEntity.puuid);
  frequentTags.value = response.data;
}

const fetchRecentReviews = async () => {
  if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
  const response = await reviewApi.getRecentReviews(summonerInfo.value.riotAccountInfoEntity.puuid);
  console.log('recentReviews', response.data);
  recentReviews.value = response.data;
}

watchEffect(async () => {
  const name = route.params.name;
  const tag = route.params.tag;
  if (name && tag) {
    isLoading.value = true;
    try {
      await fetchSummonerInfo();
      await fetchSummonerReviewStats();
      await fetchMatchList();
      await fetchReviewTags();
      await fetchFrequentTags();
      await fetchRecentReviews();
    } finally {
      isLoading.value = false;
    }
  }
});

onMounted(async () => {
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