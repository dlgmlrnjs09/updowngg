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
          :review-stats="reviewStatsInfo || null"
          :frequent-tags="frequentTags"
          :recent-reviews="recentReviews"
          :is-updated-match-list="isUpdatedMatchList"
          :rating-by-position="ratingByPosition"
          :rating-by-champ="ratingByChamp"
          :written-review="writtenReview || null"
          :play-together-match="playTogetherLatestMatch || null"
          @show-detail="showDetailModal = true"
          @update-matches="updateMatchList"
          @open-previous-modal="openPreviousModal"
          @open-review-modal="openReviewModal"
      />

      <CurrentMatchInfo v-if="currentMatchInfo?.matchInfoDto" :current-match-info-dto="currentMatchInfo"/>

      <MatchList
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
        @delete="handleReviewDelete"
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
import type {CurrentMatchInfoDto, LolMatchInfoRes, LolMatchParticipant} from '@/types/match'
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
import CurrentMatchInfo from "@/components/match/CurrentMatchInfo.vue";

const pageCount = 10;
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
const writtenReview = ref<ReviewRequestDto | null>(null)
const playTogetherLatestMatch = ref<LolMatchInfoRes | null>(null);
const currentMatchInfo = ref<CurrentMatchInfoDto>();

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
    const response = await matchApi.getMatchList(summonerInfo.value.riotAccountInfoEntity.puuid, startIndex, pageCount);

    if (startIndex === 0) {
      matches.value = response.data
    } else {
      matches.value = [...matches.value, ...response.data]
    }

    // 더 이상 불러올 데이터가 없는 경우
    if (response.data.length < pageCount) {
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
      fetchRatingByPosition(),
      fetchWrittenReview(),
      checkPlayedTogether()
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
    await fetchMatchList(currentStartIndex.value + pageCount)
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
  console.log('player === ' + JSON.stringify(player))
  selectedPlayer.value = player;
  suggestTag.value = []; // suggestTag 배열 초기화

  reviewedMatch.value = matches.value.find(match =>
      match.matchInfo.matchId == selectedPlayer.value.matchId
  )!;

  if (authStore.isAuthenticated) {
    // optional chaining을 사용하여 reviewDto가 없는 경우도 처리
    if (player?.reviewDto?.reviewable === false) {
      console.log('previous')
      showPreviousReviewModal.value = true;
    } else {
      console.log('normal')
      showReviewModal.value = true;
    }
  } else {
    toast.error('리뷰작성은 로그인 후에 가능합니다.');
  }
}

const openPreviousModal = async () => {
  /*selectedPlayer.value = player;*/
  if (authStore.isAuthenticated && writtenReview.value) {
    const response = await matchApi.getMatchInfo(writtenReview.value?.matchId);
    reviewedMatch.value = response.data;
    showPreviousReviewModal.value = true;
  }

  selectedPlayer.value = reviewedMatch.value.participantList.filter(p => p.puuid == writtenReview.value?.targetPuuid)[0];
}

const handleReviewModify = () => {
  showPreviousReviewModal.value = false;
  showReviewModal.value = true;
}

const handleReviewDelete = async (reviewSeq: number) => {
  try {
    const response = await reviewApi.deleteReview(reviewSeq)
    if (response.data === true) {
      // 모달 닫기
      showPreviousReviewModal.value = false;

      // 모든 매치에서 삭제된 리뷰 정보 업데이트
      matches.value.forEach((match, matchIndex) => {
        const playerIndex = match.participantList.findIndex(
            p => p.puuid === selectedPlayer.value.puuid
        );

        if (playerIndex !== -1) {
          // reviewable 상태 변경
          // @ts-ignore
          matches.value[matchIndex].participantList[playerIndex].reviewDto = {
            reviewable: true
          };
        }
      });

      // 관련된 모든 데이터 재조회
      await Promise.all([
        fetchSummonerReviewStats(),
        fetchFrequentTags(),
        fetchRecentReviews(),
        fetchRatingByChamp(),
        fetchRatingByPosition(),
        fetchWrittenReview(),
        checkPlayedTogether()
      ]);

      toast.success('리뷰가 삭제되었습니다.');
    }
  } catch (error) {
    toast.error('리뷰 삭제 중 오류가 발생했습니다.');
  }
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

const fetchWrittenReview = async() => {
  if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
  if (authStore.isAuthenticated) {
    const response = await reviewApi.checkExistWritten(summonerInfo.value.riotAccountInfoEntity.puuid)
    if (response.data === true) {
      const response = await reviewApi.getWrittenReviewToTarget(summonerInfo.value.riotAccountInfoEntity.puuid);
      writtenReview.value = response.data;
    }
  }
}

const fetchCurrentMatchInfo = async () => {
  if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
  const response = await matchApi.getCurrentMatchInfo(summonerInfo.value.riotAccountInfoEntity.puuid);
  if (response.data) {
    currentMatchInfo.value = response.data;
  }
}

const checkPlayedTogether = async () => {
  if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return
  if (authStore.isAuthenticated) {
    const today = new Date();
    const sevenDaysAgo = new Date(today);
    sevenDaysAgo.setDate(today.getDate() - 7);

    const formatDate = (date: Date) => {
      return date.toISOString().slice(0, 10).replace(/-/g, '');
    }

    const response = await matchApi.checkPlayedTogether(summonerInfo.value.riotAccountInfoEntity.puuid, formatDate(sevenDaysAgo), formatDate(today));
    if (response.data === true) {
      const response = await matchApi.getLatestMatchInfoTogether(summonerInfo.value.riotAccountInfoEntity.puuid, formatDate(sevenDaysAgo), formatDate(today));
      playTogetherLatestMatch.value = response.data;
    }
  }
}

watchEffect(async () => {
  const name = route.params.name;
  const tag = route.params.tag;
  if (name && tag) {
    isLoading.value = true;
    currentStartIndex.value = 0;
    noMoreMatches.value = false;
    writtenReview.value = null;
    playTogetherLatestMatch.value = null;
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
      await fetchWrittenReview();
      await fetchCurrentMatchInfo();
      await checkPlayedTogether();
    } finally {
      isLoading.value = false;
    }
  }
});

onMounted(async () => {
  await fetchReviewTags()
  await fetchReviewTagCategories();
  writtenReview.value = null;
  playTogetherLatestMatch.value = null;
})
</script>
<style scoped>
.container {
  max-width: 1000px;
  margin: 0 auto 0;
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