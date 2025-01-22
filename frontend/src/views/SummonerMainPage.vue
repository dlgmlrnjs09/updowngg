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
          :rating-by-position="ratingByPosition"
          :rating-by-champ="ratingByChamp"
          @show-detail="showDetailModal = true"
          @update-matches="updateMatchList"
      />

      <MatchList
          v-if="matches.length"
          :matches="matches"
          :profile-data="summonerInfo"
          :is-loading="isLoadingMore"
          :no-more-matches="noMoreMatches"
          @review-player="openReviewModal"
          @load-more="loadMoreMatches"
      />

      <DetailModal
          v-if="showDetailModal"
          :name="summonerInfo?.riotAccountInfoEntity.gameName"
          @close="showDetailModal = false"
      />

      <ReviewModal
          v-if="showReviewModal"
          :player="selectedPlayer"
          :review-tags="reviewTags"
          :review-tag-categories="reviewTagCategories"
          :suggest-tag="suggestTag"
          @close="handleReviewModalClose"
          @clearSuggestTag="suggestTag = []"
          @openTagSuggest="showTagSuggestModal = true"
          @submit="handleReviewSubmit"
      />

      <TagSuggestModal
          v-if="showTagSuggestModal"
          :player="selectedPlayer"
          :review-tag-categories="reviewTagCategories"
          @close="showTagSuggestModal = false"
          @submit="handleTagSuggest"
      />

      <PreviousReviewModal
        v-if="showPreviousReviewModal"
        :reviewed-match="reviewedMatch"
        :player="selectedPlayer"
        @close="showPreviousReviewModal = false"
        @rewrite="handleReviewModify"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, watchEffect} from 'vue'
import {useRoute} from 'vue-router'
import Profile from '@/components/summoner/SummonerProfile.vue'
import MatchList from '@/components/match/MatchList.vue'
import DetailModal from '@/components/review/modal/DetailModal.vue'
import ReviewModal from '@/components/review/modal/ReviewModal.vue'
import ProfileSkeleton from '@/components/summoner/SummonerProfileSkeleton.vue'
import MatchListSkeleton from '@/components/match/MatchListSkeleton.vue'
import {summonerApi} from '@/api/summoner'
import {matchApi} from '@/api/match'
import {reviewApi} from '@/api/review'
import type {LolSummonerProfileResDto} from '@/types/summoner'
import type {LolMatchInfoRes, LolMatchParticipant} from '@/types/match'
import type {
  ReviewRatingByChampDto,
  ReviewRatingByPositionDto,
  ReviewRequestDto,
  ReviewStatsDto, ReviewTagCategoryDto,
  ReviewTagDto, ReviewTagSuggestDto
} from "@/types/review.ts";
import PreviousReviewModal from "@/components/review/modal/PreviousReviewModal.vue";
import {useToast} from "vue-toastification";
import {useAuthStore} from "@/stores/auth.ts";
import TagSuggestModal from "@/components/review/modal/TagSuggestModal.vue";

const toast = useToast();
const authStore = useAuthStore();
const route = useRoute();
const isLoading = ref(true);
const isLoadingMore = ref(false);
const isUpdatedMatchList = ref(false);
const currentStartIndex = ref(0);
const noMoreMatches = ref(false);

const summonerInfo = ref<LolSummonerProfileResDto | null>(null)
const reviewStatsInfo = ref<ReviewStatsDto | null>(null)
const matches = ref<LolMatchInfoRes[]>([])
const reviewedMatch = ref<LolMatchInfoRes>({} as LolMatchInfoRes)
const reviewTags = ref<ReviewTagDto[]>([])
const reviewTagCategories = ref<ReviewTagCategoryDto[]>([])
const frequentTags = ref<ReviewTagDto | null>(null)
const recentReviews = ref<ReviewRequestDto[]>([])
const ratingByChamp = ref<ReviewRatingByChampDto | null>(null)
const ratingByPosition = ref<ReviewRatingByPositionDto | null>(null)
const showDetailModal = ref(false)
const showReviewModal = ref(false)
const showTagSuggestModal = ref(false)
const showPreviousReviewModal = ref(false)
const selectedPlayer = ref(<LolMatchParticipant>({}));
const suggestTag = ref<ReviewTagSuggestDto[]>([])

const fetchSummonerInfo = async () => {
  try {
    const summonerId = route.params.name as string
    const tagLine = route.params.tag as string
    const response = await summonerApi.getInfoBySummonerId(summonerId, tagLine)
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

const fetchMatchList = async (startIndex: number = 0) => {
  try {
    if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
    const response = await matchApi.getMatchList(summonerInfo.value.riotAccountInfoEntity.puuid, startIndex, 5);

    if (startIndex === 0) {
      matches.value = response.data
    } else {
      matches.value = [...matches.value, ...response.data]
    }

    // 더 이상 불러올 데이터가 없는 경우
    if (response.data.length < 5) {
      noMoreMatches.value = true
    }

    currentStartIndex.value = startIndex
  } catch (error) {
    console.error('Failed to fetch matches:', error)
  }
}

const handleReviewModalClose = () => {
  showReviewModal.value = false;
  suggestTag.value = [];  // 모달이 닫힐 때 suggestTag 초기화
}

// 리뷰 submit 완료 후처리
const handleReviewSubmit = async (reviewData: ReviewRequestDto) => {
  showReviewModal.value = false;
  try {
    // suggestedTags 배열 형태로 변경
    if (Array.isArray(suggestTag.value)) {
      // 각 제안된 태그에 리뷰 시퀀스 번호 추가
      const tagsWithReviewSeq = suggestTag.value.map(tag => ({
        ...tag,
        reviewSeq: reviewData.summonerReviewSeq
      }));

      // 모든 제안된 태그를 한번에 전송
      await reviewApi.suggestReviewTags(tagsWithReviewSeq);
      suggestTag.value = [] as ReviewTagSuggestDto[];
    }

    // 모든 매치에서 동일한 소환사의 reviewDto 업데이트
    matches.value.forEach((match, matchIndex) => {
      const playerIndex = match.participantList.findIndex(
          p => p.puuid === reviewData.targetPuuid
      );

      if (playerIndex !== -1) {
        matches.value[matchIndex].participantList[playerIndex].reviewDto = {
          ...reviewData,
        };
      }
    });

    // 나머지 데이터들 업데이트
    await Promise.all([
      fetchSummonerReviewStats(),
      fetchFrequentTags(),
      fetchRecentReviews(),
      fetchRatingByChamp(),
      fetchRatingByPosition()
    ]);

  } catch (error) {
    toast.error('데이터 업데이트 중 오류가 발생했습니다.');
  }
}

const handleTagSuggest = async (tagData: ReviewTagSuggestDto) => {
  showTagSuggestModal.value = false;

  // 새로운 태그를 배열에 추가
  suggestTag.value = [...suggestTag.value, tagData];
}

const loadMoreMatches = async () => {
  if (isLoadingMore.value || noMoreMatches.value) return

  isLoadingMore.value = true
  try {
    await fetchMatchList(currentStartIndex.value + 5)
  } finally {
    isLoadingMore.value = false
  }
}

const updateMatchList = async () => {
  try {
    if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
    isUpdatedMatchList.value = true
    currentStartIndex.value = 0
    noMoreMatches.value = false

    await matchApi.updateMatchList(summonerInfo.value.riotAccountInfoEntity.puuid)
    currentStartIndex.value = 0
    await fetchMatchList(currentStartIndex.value)

  } catch (error) {
    console.error('Failed to fetch matches:', error)
  } finally {
    isUpdatedMatchList.value = false
  }
}

const openReviewModal = (player: any) => {
  selectedPlayer.value = player;
  suggestTag.value = []; // suggestTag 배열 초기화

  reviewedMatch.value = matches.value.find(match =>
      match.matchInfo.matchId == selectedPlayer.value.matchId
  )!;

  if (authStore.isAuthenticated) {
    if (!player.reviewDto.reviewable) {
      showPreviousReviewModal.value = true;
    } else {
      showReviewModal.value = true;
    }
  } else {
    toast.error('리뷰작성은 로그인 후에 가능합니다.');
  }
}

const handleReviewModify = () => {
  showPreviousReviewModal.value = false;
  showReviewModal.value = true;
}

const fetchReviewTags = async () => {
  const response = await reviewApi.getTagList();
  reviewTags.value = response.data
}

const fetchReviewTagCategories = async () => {
  const response = await reviewApi.getTagCategoryList();
  reviewTagCategories.value = response.data
}

const fetchFrequentTags = async () => {
  if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
  const response = await reviewApi.getReviewTagFrequent(summonerInfo.value.riotAccountInfoEntity.puuid);
  frequentTags.value = response.data;
}

const fetchRecentReviews = async () => {
  if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
  const response = await reviewApi.getRecentReviewsByPuuid(summonerInfo.value.riotAccountInfoEntity.puuid, 20);
  recentReviews.value = response.data;
}

const fetchRatingByChamp = async () => {
  if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
  const response = await reviewApi.getRatingByChamp(summonerInfo.value.riotAccountInfoEntity.puuid);
  ratingByChamp.value = response.data;
}

const fetchRatingByPosition = async () => {
  if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
  const response = await reviewApi.getRatingByPosition(summonerInfo.value.riotAccountInfoEntity.puuid);
  ratingByPosition.value = response.data;
}

watchEffect(async () => {
  const name = route.params.name;
  const tag = route.params.tag;
  if (name && tag) {
    isLoading.value = true;
    currentStartIndex.value = 0;
    noMoreMatches.value = false;
    try {
      await fetchSummonerInfo();
      await fetchSummonerReviewStats();
      await fetchMatchList();
      await fetchReviewTags();
      await fetchReviewTagCategories();
      await fetchFrequentTags();
      await fetchRecentReviews();
      await fetchRatingByChamp();
      await fetchRatingByPosition();
    } finally {
      isLoading.value = false;
    }
  }
});

onMounted(async () => {
  await fetchReviewTags()
  await fetchReviewTagCategories();
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