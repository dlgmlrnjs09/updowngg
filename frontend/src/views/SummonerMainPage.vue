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
          @filter-change="handleFilterChange"
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
    
    <!-- 가이드 모달 -->
    <GuideModal
      v-if="showGuide"
      :pages="guidePages"
      :show="showGuide"
      :close-on-overlay-click="true"
      @close="closeGuide"
      @page-changed="handleGuidePageChange"
      @complete="completeGuide"
    />
    
    <!-- 가이드 시작 버튼 -->
    <button
      v-if="!showGuide && !guideCompleted && !isLoading"
      @click="startGuide"
      class="fixed bottom-8 right-8 bg-[#2979FF] text-white p-3 rounded-full shadow-lg hover:bg-[#1A67E0] transition-all z-50 flex items-center group"
      aria-label="소환사 프로필 설명 보기"
    >
      <HelpCircle class="w-6 h-6" />
      <span class="max-w-0 overflow-hidden whitespace-nowrap group-hover:max-w-xs transition-all duration-500 ease-linear bg-[#2979FF] rounded-l-full -ml-3 pl-5 pr-2">
        프로필 설명
      </span>
    </button>
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
import GuideModal from '@/components/common/GuideModal.vue'
import { HelpCircle } from 'lucide-vue-next'
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
import {cookieUtils} from "@/utils/cookieUtil.ts";

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

// 가이드 모달 관련 상태
const showGuide = ref(false);
const guideCompleted = ref(false);
const GUIDE_COMPLETED_KEY = 'summoner_guide_completed';
const currentGuidePageIndex = ref(0);

// 가이드 페이지 정의
const guidePages = [
  {
    title: '소환사 프로필',
    content: '소환사의 기본 정보, 티어, 주요 챔피언 정보를 볼 수 있습니다. 프로필 사진을 클릭하면 상세 정보를 볼 수 있으며, 매치 데이터 업데이트 버튼을 통해 최신 경기 기록을 불러올 수 있습니다.'
    // image: '/path/to/your/image.jpg' - 이미지는 직접 추가 예정
  },
  {
    title: '평가 정보',
    content: '소환사에 대한 다른 유저들의 평가 정보를 확인할 수 있습니다. 평가 점수, 좋아요/싫어요 비율, 자주 받은 태그와 최근 받은 평가 내용을 볼 수 있습니다. 포지션별, 챔피언별 평가 통계도 확인할 수 있습니다.'
    // image: '/path/to/your/image.jpg' - 이미지는 직접 추가 예정
  },
  {
    title: '현재 게임 정보',
    content: '소환사가 현재 게임 중이라면 현재 진행 중인 게임의 정보를 확인할 수 있습니다. 팀원들의 정보와 상대 팀 구성, 각 소환사의 최근 승률 등 유용한 정보를 제공합니다.'
    // image: '/path/to/your/image.jpg' - 이미지는 직접 추가 예정
  },
  {
    title: '경기 기록',
    content: '소환사의 과거 경기 기록을 볼 수 있습니다. 각 게임의 결과, KDA, 아이템 빌드, 룬 선택 등 상세 정보를 확인할 수 있으며, 같이 플레이한 다른 소환사에 대한 평가도 남길 수 있습니다. 게임 모드나 평가 여부로 필터링이 가능합니다.'
    // image: '/path/to/your/image.jpg' - 이미지는 직접 추가 예정
  }
];

const currentMatchFilters = ref({
  gameMode: 'ALL',
  reviewFilter: undefined as 'reviewed' | 'unreviewed' | undefined
});

const handleFilterChange = async (filters: {
  gameMode: string;
  reviewFilter?: 'reviewed' | 'unreviewed';
}) => {
  currentMatchFilters.value = filters;

  // 필터가 변경되면 첫 페이지부터 다시 로드
  currentStartIndex.value = 0;
  noMoreMatches.value = false;
  matches.value = [];

  await fetchMatchList(0);
};

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
    if (!summonerInfo.value?.riotAccountInfoEntity.puuid) return;

    const response = await matchApi.getMatchList(
        summonerInfo.value.riotAccountInfoEntity.puuid,
        startIndex,
        pageCount,
        currentMatchFilters.value.gameMode,
        currentMatchFilters.value.reviewFilter
    );

    if (startIndex === 0) {
      matches.value = response.data;
    } else {
      matches.value = [...matches.value, ...response.data];
    }

    // 더 이상 불러올 데이터가 없는 경우
    if (response.data.length < pageCount) {
      noMoreMatches.value = true;
    }

    currentStartIndex.value = startIndex;
  } catch (error) {
    console.error('Failed to fetch matches:', error);
  }
};

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
  selectedPlayer.value = player;
  suggestTag.value = []; // suggestTag 배열 초기화

  reviewedMatch.value = matches.value.find(match =>
      match.matchInfo.matchId == selectedPlayer.value.matchId
  )!;

  if (authStore.isAuthenticated) {
    // optional chaining을 사용하여 reviewDto가 없는 경우도 처리
    if (player?.reviewDto?.reviewable === false) {
      showPreviousReviewModal.value = true;
    } else {
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
    // 필터 초기화
    currentMatchFilters.value = {
      gameMode: 'ALL',
      reviewFilter: undefined
    };

    try {
      // SummonerInfo 에서 불러온 puuid를 사용해야하기 때문에 동기로 불러옴
      await fetchSummonerInfo();

      if (!summonerInfo.value?.riotAccountInfoEntity.puuid) {
        throw new Error('Failed to fetch summoner info');
      }

      // 소환사 정보를 최근 검색 기록에 저장
      cookieUtils.addRecentSummoner({
        gameName: summonerInfo.value.riotAccountInfoEntity.gameName,
        tagLine: summonerInfo.value.riotAccountInfoEntity.tagLine,
        lastViewedAt: Date.now()
      });

      await Promise.all([
        fetchSummonerReviewStats(),
        fetchMatchList(),
        fetchReviewTags(),
        fetchReviewTagCategories(),
        fetchFrequentTags(),
        fetchRecentReviews(),
        fetchRatingByChamp(),
        fetchRatingByPosition(),
        fetchWrittenReview(),
        fetchCurrentMatchInfo(),
        checkPlayedTogether()
      ]);
    } catch (error) {
      console.error('Error loading summoner data:', error);
      toast.error('소환사 정보를 불러오는데 실패했습니다.');
    } finally {
      isLoading.value = false;
      
      // 처음 방문한 사용자에게 2초 후 가이드 표시
      if (!guideCompleted.value) {
        setTimeout(() => {
          startGuide();
        }, 2000);
      }
    }
  }
});

// 가이드 관련 메서드
const startGuide = () => {
  currentGuidePageIndex.value = 0;
  showGuide.value = true;
};

const closeGuide = () => {
  showGuide.value = false;
};

const handleGuidePageChange = (pageIndex: number) => {
  currentGuidePageIndex.value = pageIndex;
};

const completeGuide = () => {
  showGuide.value = false;
  guideCompleted.value = true;
  
  // 로컬 스토리지에 가이드 완료 상태 저장
  localStorage.setItem(GUIDE_COMPLETED_KEY, 'true');
  
  // 완료 메시지
  toast.success('소환사 프로필 설명을 완료했습니다!');
  
  // 5분 후에 도움 버튼 다시 표시
  setTimeout(() => {
    guideCompleted.value = false;
  }, 300000); // 5분
};

onMounted(async () => {
  await fetchReviewTags();
  await fetchReviewTagCategories();
  writtenReview.value = null;
  playTogetherLatestMatch.value = null;
  
  // 로컬 스토리지에서 가이드 완료 상태 확인
  const completedStatus = localStorage.getItem(GUIDE_COMPLETED_KEY);
  guideCompleted.value = completedStatus === 'true';
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