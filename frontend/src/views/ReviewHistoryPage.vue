<template>
  <div class="min-h-screen bg-[#0A0A0A] p-6 pt-24">
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
      <div class="space-y-4">
        <div v-for="(review, index) in getReviewWithParticipant"
             :key="index"
             class="bg-[#141414] rounded-xl border border-[#ffffff1a] p-4">
          <div class="flex gap-6 items-center">
            <!-- Game Info -->
            <div class="min-w-[130px]">
              <div class="text-white text-sm font-medium">{{ review.matchDto.gameModeName}}</div>
              <div class="text-gray-400 text-xs mt-1 flex gap-1">
                <span>{{ formatTimeAgo(review.matchDto.gameStartDt) }}</span>
              </div>
            </div>

            <!-- Champion Icon & Level -->
            <div class="relative flex-shrink-0  mr-[20px]">
              <div class="w-12 h-12 bg-[#1a1a1a] rounded-lg overflow-hidden">
                <img :src="review.target?.champProfileIconUrl" :alt="review.target?.champName" class="w-full h-full"/>
              </div>
              <div class="absolute -bottom-1 -right-1 bg-[#1a1a1a] w-5 h-5 rounded-full flex items-center justify-center text-xs text-gray-300">
                {{ review.target?.champLevel }}
              </div>
            </div>

            <!-- Review Content -->
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <component
                    :is="review.reviewDto.isUp ? ThumbsUp : ThumbsDown"
                    class="w-4 h-4"
                    :class="review.reviewDto.isUp ? 'text-[#4CAF50]' : 'text-[#FF5252]'"
                />
                <span class="text-white text-sm">{{ review.target?.riotIdGameName }}</span>
                <span class="text-gray-400 text-xs">#{{ review.target?.riotIdTagline }}</span>
              </div>
              <div class="flex flex-wrap gap-2 mb-2">
                <TagList :tags="review.reviewDto.tagDtoList || []" size="small" :is-show-count="false"/>
              </div>
              <p class="text-gray-300 text-sm line-clamp-2">{{ review.reviewDto.comment }}</p>
            </div>

            <!-- Players Grid -->
            <div class="grid grid-cols-2 gap-x-4 gap-y-1 w-[250px]">
              <div v-for="player in review.participantDtoList"
                   :key="player.riotIdGameName"
                   class="flex items-center gap-1.5 px-2 py-1 rounded"
                   :class="{
                      'bg-[#4CAF504D]': player.puuid === review.reviewDto.targetPuuid,
                      'bg-[#2979FF4D]': player.puuid === review.reviewDto.reviewerPuuid
                    }"
              >
                <img :src="player.champProfileIconUrl" alt="Profile" class="w-5 h-5 rounded-full"/>
                <div class="text-sm text-gray-400 truncate">{{ player.riotIdGameName }}</div>
              </div>
            </div>
          </div>
        </div>
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
import { ref, computed, onMounted } from 'vue'
import type { ReviewHistoryResponse, ReviewHistoryDto } from '@/types/review'
import {reviewApi} from "@/api/review.ts";
import { ThumbsUp, ThumbsDown } from 'lucide-vue-next'
import {createInitialPaging, formatTimeAgo, getPageNumbers} from "@/common.ts";
import TagList from "@/components/common/TagList.vue";

const activeTab = ref('written')
const histories = ref<ReviewHistoryDto[]>([])
const isLoading = ref(false)
const paging = ref<ReviewHistoryResponse>(createInitialPaging<ReviewHistoryDto>())
const pageNumbers = computed(() => getPageNumbers(paging.value))

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
  activeTab.value = tab
  paging.value.currentPage = 1
  await fetchReviews()
}

const handlePageChange = async (page: number) => {
  paging.value.currentPage = page
  await fetchReviews()
}

const handlePrevPage = () => {
  paging.value.currentPage = paging.value.startPage - 1
  fetchReviews()
}

const handleNextPage = () => {
  paging.value.currentPage = paging.value.endPage + 1
  fetchReviews()
}

onMounted(() => {
  fetchReviews()
})

const getReviewWithParticipant = computed(() => {
  return histories.value.map(review => ({
    ...review,
    reviewer: review.participantDtoList.find(
        participant => participant.puuid === review.reviewDto.reviewerPuuid
    ),
    target: review.participantDtoList.find(
        participant => participant.puuid === review.reviewDto.targetPuuid
    )
  }))
})
</script>