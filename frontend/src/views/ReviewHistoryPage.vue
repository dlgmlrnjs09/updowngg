<template>
  <div class="min-h-screen bg-[#0A0A0A] p-6">
    <div class="max-w-[1000px] mx-auto">
      <!-- Header -->
      <div class="flex justify-between items-center mb-8">
        <h1 class="text-2xl font-bold text-white">평가 내역</h1>
        <div class="flex gap-4">
          <button
              @click="handleTabChange('written')"
              :class="[
             'px-4 py-2 rounded-lg text-sm transition-colors',
             activeTab === 'written'
               ? 'bg-[#2979FF] text-white'
               : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
           ]"
          >작성한 리뷰</button>
          <button
              @click="handleTabChange('received')"
              :class="[
             'px-4 py-2 rounded-lg text-sm transition-colors',
             activeTab === 'received'
               ? 'bg-[#2979FF] text-white'
               : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
           ]"
          >받은 리뷰</button>
        </div>
      </div>

      <!-- Reviews List -->
      <div class="space-y-4 min-h-[600px] flex flex-col">
        <template v-if="histories.length > 0">
          <div v-for="(review, index) in getReviewWithParticipant"
               :key="index"
               :ref="el => { if (review.reviewDto.summonerReviewSeq.toString() === route.query.reviewSeq) reviewRef = el }"
               :class="[
           'bg-[#141414] rounded-xl border border-[#ffffff1a] p-4 transition-all duration-1000',
           review.reviewDto.summonerReviewSeq.toString() === route.query.reviewSeq ? 'highlight-review' : ''
         ]">
            <div class="flex gap-6 items-center">
              <!-- Mobile Layout -->
              <div class="block md:hidden w-full">
                <div class="flex items-center gap-3 mb-2">
                  <div class="w-12 h-12 bg-[#1a1a1a] rounded-lg overflow-hidden flex-shrink-0">
                    <img v-if="activeTab === 'received' && review.reviewDto.isAnonymous" src="/src/assets/icon/anonymous_profile.png" alt="anonymous">
                    <img v-else :src="review.displayedUser?.champProfileIconUrl" :alt="review.displayedUser?.champName" class="w-full h-full"/>
                  </div>
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2">
                      <template v-if="activeTab === 'received' && review.reviewDto.isAnonymous">
                        <span class="text-gray-400 text-sm italic">익명의 소환사</span>
                      </template>
                      <template v-else>
                        <span class="text-white text-sm summoner-name truncate">{{ review.displayedUser?.riotIdGameName }}</span>
                        <span class="text-gray-400 text-xs">#{{ review.displayedUser?.riotIdTagline }}</span>
                      </template>
                    </div>
                    <div class="text-gray-400 text-xs flex items-center gap-2">
                      <span>{{ review.matchDto.gameModeName }}</span>
                      <span>·</span>
                      <span>{{ formatTimeAgo(review.matchDto.gameStartDt) }}</span>
                    </div>
                  </div>
                </div>
                <div class="flex items-center gap-2 mb-2">
                  <component
                      :is="review.reviewDto.isUp ? ThumbsUp : ThumbsDown"
                      class="w-4 h-4"
                      :class="review.reviewDto.isUp ? 'text-[#4CAF50]' : 'text-[#FF5252]'"
                  />
                  <div class="flex flex-wrap gap-2">
                    <TagList :tags="review.reviewDto.tagDtoList || []" size="small" :is-show-count="false"/>
                  </div>
                </div>
                <p class="text-gray-300 text-sm line-clamp-2">{{ review.reviewDto.comment }}</p>
              </div>

              <!-- Tablet/PC Layout -->
              <div class="hidden md:flex w-full">
                <!-- Game Info -->
                <div class="min-w-[130px] m-auto">
                  <div class="text-white text-sm font-medium">{{ review.matchDto.gameModeName}}</div>
                  <div class="text-gray-400 text-xs mt-1 flex gap-1">
                    <span>{{ formatTimeAgo(review.matchDto.gameStartDt) }}</span>
                  </div>
                </div>

                <!-- Champion Icon & Level -->
                <div class="relative flex-shrink-0 mr-[20px] m-auto">
                  <div class="w-12 h-12 bg-[#1a1a1a] rounded-lg overflow-hidden">
                    <img v-if="activeTab === 'received' && review.reviewDto.isAnonymous" src="/src/assets/icon/anonymous_profile.png" alt="anonymous">
                    <img v-else :src="review.displayedUser?.champProfileIconUrl" :alt="review.displayedUser?.champName" class="w-full h-full"/>
                  </div>
<!--                  <div v-if="!(activeTab === 'received' && review.reviewDto.isAnonymous)" class="absolute -bottom-1 -right-1 bg-[#1a1a1a] w-5 h-5 rounded-full flex items-center justify-center text-xs text-gray-300">
                    {{ review.displayedUser?.champLevel }}
                  </div>-->
                </div>

                <!-- Review Content -->
                <div class="flex-1 m-auto">
                  <div class="flex items-center gap-2 mb-2 cursor-pointer" @click.stop="goSelectedSummonerProfile(review.displayedUser.riotIdGameName, review.displayedUser.riotIdTagline)">
                    <component
                        :is="review.reviewDto.isUp ? ThumbsUp : ThumbsDown"
                        class="w-4 h-4"
                        :class="review.reviewDto.isUp ? 'text-[#4CAF50]' : 'text-[#FF5252]'"
                    />
                    <template v-if="activeTab === 'received' && review.reviewDto.isAnonymous">
                      <span class="text-gray-400 text-sm italic">익명의 소환사</span>
                    </template>
                    <template v-else>
                      <span class="text-white text-sm summoner-name">{{ review.displayedUser?.riotIdGameName }}</span>
                      <span class="text-gray-400 text-xs">#{{ review.displayedUser?.riotIdTagline }}</span>
                    </template>
                  </div>
                  <div class="flex flex-wrap gap-2 mb-2">
                    <TagList :tags="review.reviewDto.tagDtoList || []" size="small" :is-show-count="false"/>
                  </div>
                  <p class="text-gray-300 text-sm line-clamp-2">{{ review.reviewDto.comment }}</p>
                </div>

                <!-- Players Grid -->
                <div class="flex gap-x-4 w-[250px]">
                  <div class="flex flex-col gap-y-1 flex-1 min-w-0">
                    <div v-for="player in review.participantDtoList.filter(r => r.teamId == 100)"
                         :key="`${player.riotIdGameName}_${player.puuid}`"
                         class="flex items-center gap-1.5 px-2 py-1 rounded min-w-0"
                         :class="{
                      'bg-[#4CAF504D]': player.puuid === review.reviewDto.targetPuuid,
                      'bg-[#2979FF4D]': player.puuid === review.reviewDto.reviewerPuuid
                    }"
                    >
                      <img :src="player.champProfileIconUrl" alt="Profile" class="w-5 h-5 rounded-full flex-shrink-0"/>
                      <div
                          class="text-sm text-gray-400 truncate summoner-name w-full"
                          @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)"
                      >
                        {{ player.riotIdGameName }}
                      </div>
                    </div>
                  </div>

                  <div class="flex flex-col gap-y-1 flex-1 min-w-0">
                    <div v-for="player in review.participantDtoList.filter(r => r.teamId == 200)"
                         :key="player.riotIdGameName"
                         class="flex items-center gap-1.5 px-2 py-1 rounded min-w-0"
                         :class="{
                      'bg-[#4CAF504D]': player.puuid === review.reviewDto.targetPuuid,
                      'bg-[#2979FF4D]': player.puuid === review.reviewDto.reviewerPuuid
                    }"
                    >
                      <img :src="player.champProfileIconUrl" alt="Profile" class="w-5 h-5 rounded-full flex-shrink-0"/>
                      <div
                          class="text-sm text-gray-400 truncate summoner-name w-full"
                          @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)"
                      >
                        {{ player.riotIdGameName }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="flex-1 flex flex-col items-center justify-center">
            <img class="w-28 h-28 text-gray-600 mb-4" src="@/assets/icon/emoji/crying_bee.png" alt="crying_bee">
            <h3 class="text-lg font-medium text-gray-200 mb-2">
              {{ activeTab === 'written' ? '작성한 리뷰가 없습니다' : '받은 리뷰가 없습니다' }}
            </h3>
            <p class="text-gray-400 text-sm">
              {{ activeTab === 'written' ? '함께 게임을 한 소환사를 평가해보세요!' : '먼저 함께한 소환사를 평가해보세요!' }}
            </p>
          </div>
        </template>
      </div>

      <!-- Pagination -->
      <div class="flex justify-center mt-6 gap-2">
        <!-- 이전 버튼 -->
        <button
            v-if="paging.hasPrevious"
            @click="handlePrevPage"
            class="px-3 h-8 rounded-lg text-sm font-medium bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]"
        >
          <
        </button>

        <!-- 페이지 번호 -->
        <button
            v-for="page in pageNumbers"
            :key="page"
            @click="handlePageChange(page)"
            :class="[
          'w-8 h-8 rounded-lg text-sm font-medium transition-colors',
          paging.currentPage === page
            ? 'bg-[#2979FF] text-white'
            : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
        ]"
        >
          {{ page }}
        </button>

        <!-- 다음 버튼 -->
        <button
            v-if="paging.hasNext"
            @click="handleNextPage"
            class="px-3 h-8 rounded-lg text-sm font-medium bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]"
        >
          >
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import type { ReviewHistoryResponse, ReviewHistoryDto } from '@/types/review'
import {reviewApi} from "@/api/review.ts";
import { ThumbsUp, ThumbsDown, MessageCircleX  } from 'lucide-vue-next'
import {createInitialPaging, formatTimeAgo, getPageNumbers, goSelectedSummonerProfile} from "@/utils/common.ts";
import TagList from "@/components/common/TagList.vue";
import {useRoute, useRouter} from "vue-router";

const activeTab = ref('written')
const histories = ref<ReviewHistoryDto[]>([])
const isLoading = ref(false)
const paging = ref<ReviewHistoryResponse>(createInitialPaging<ReviewHistoryDto>())
const pageNumbers = computed(() => getPageNumbers(paging.value))
const reviewRef = ref<HTMLElement | null>(null)

const route = useRoute();
const router = useRouter();

const handleReviewHighlight = () => {
  if (reviewRef.value && route.query.reviewSeq) {
    reviewRef.value.scrollIntoView({ behavior: 'smooth', block: 'center' })
    setTimeout(() => {
      if (reviewRef.value) {
        reviewRef.value.classList.remove('highlight-review')
      }
    }, 3000)
  }
}

const fetchReviews = async () => {
  try {
    isLoading.value = true
    const response = activeTab.value === 'written'
        ? await reviewApi.getWrittenReviewHistory(paging.value.currentPage)
        : await reviewApi.getReceivedReviewHistory(paging.value.currentPage)

    paging.value = response.data
    histories.value = response.data.items
  } catch (error) {
    console.error('Error fetching reviews:', error)
  } finally {
    isLoading.value = false
  }
}

const handleTabChange = async (tab: string) => {
  await router.push({
    query: {
      ...route.query,
      tab,
      page: '1'
    }
  });
};

const handlePageChange = async (page: number) => {
  await router.push({
    query: {
      ...route.query,
      page: page.toString()
    }
  });
};

const handlePrevPage = () => {
  paging.value.currentPage = paging.value.startPage - 1
  fetchReviews()
}

const handleNextPage = () => {
  paging.value.currentPage = paging.value.endPage + 1
  fetchReviews()
}

onMounted(() => {
  if (route.query.tab) {
    activeTab.value = route.query.tab as string
  }
  if (route.query.page) {
    paging.value.currentPage = parseInt(route.query.page as string)
  }

  fetchReviews().then(() => {
    nextTick(() => {
      handleReviewHighlight()
    })
  })

  watch(
      () => route.query,
      (query) => {
        if (query.tab) {
          activeTab.value = query.tab as string
        }
        if (query.page) {
          paging.value.currentPage = parseInt(query.page as string)
        }
        fetchReviews().then(() => {
          nextTick(() => {
            handleReviewHighlight()
          })
        })
      }
  )
})

const getReviewWithParticipant = computed(() => {
  return histories.value.map(review => ({
    ...review,
    displayedUser: activeTab.value === 'written'
        ? review.participantDtoList.find(
            participant => participant.puuid === review.reviewDto.targetPuuid
        )
        : review.participantDtoList.find(
            participant => participant.puuid === review.reviewDto.reviewerPuuid
        )
  }))
})
</script>

<style scoped>
.highlight-review {
  background-color: rgba(41, 121, 255, 0.1);
  border-color: #2979FF;
  box-shadow: 0 0 0 1px #2979FF;
}

.summoner-name:hover {
  cursor: pointer;
  color: #2979FF;
  text-decoration: underline;
}

@keyframes fadeOut {
  from {
    background-color: rgba(41, 121, 255, 0.1);
    border-color: #2979FF;
    box-shadow: 0 0 0 1px #2979FF;
  }
  to {
    background-color: #141414;
    border-color: rgba(255, 255, 255, 0.1);
    box-shadow: none;
  }
}

.highlight-review {
  animation: fadeOut 3s forwards;
  animation-delay: 1s;
}
</style>