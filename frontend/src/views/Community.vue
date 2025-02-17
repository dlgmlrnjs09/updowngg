<template>
  <div class="min-h-screen bg-[#0A0A0A] p-4 sm:p-6">
    <div class="max-w-6xl mx-auto mb-8">
      <!-- 필터 영역 -->
      <div class="flex flex-col space-y-4 sm:flex-row sm:justify-between sm:items-center mb-8 sm:mb-14">
        <div class="grid grid-cols-2 gap-2 sm:bg-[#141414] sm:p-3 sm:rounded-xl sm:flex sm:gap-3 sm:flex-1 sm:mr-4">
          <select
              v-model="selectedGameMode"
              class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
              @change="onFilterUpdate"
          >
            <option value="">모든 큐</option>
            <option value="SOLO_RANK">솔로랭크</option>
            <option value="FLEX_RANK">자유랭크</option>
            <option value="NORMAL">일반게임</option>
            <option value="ARAM">칼바람나락</option>
          </select>
          <select
              v-model="selectedPosition"
              class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
              @change="onFilterUpdate"
          >
            <option value="">모든 포지션</option>
            <option value="TOP">탑</option>
            <option value="JG">정글</option>
            <option value="MID">미드</option>
            <option value="AD">원딜</option>
            <option value="SUP">서포터</option>
          </select>
          <select
              v-model="selectedTier"
              class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF] col-span-2 sm:col-span-1"
              @change="onFilterUpdate"
          >
            <option value="">모든 티어</option>
            <option value="IRON">아이언</option>
            <option value="BRONZE">브론즈</option>
            <option value="SILVER">실버</option>
            <option value="GOLD">골드</option>
            <option value="PLATINUM">플래티넘</option>
          </select>
        </div>
        <button
            @click="showWriteModal = true"
            class="w-full sm:w-auto bg-[#2979FF] text-white px-4 py-3 sm:py-1.5 rounded-lg hover:bg-[#2565D1] transition-colors text-sm font-medium sm:h-12"
        >
          {{ isMobile ? '듀오 등록' : '듀오 등록하기' }}
        </button>
      </div>

      <!-- 글쓰기 모달 -->
      <WriteModal
          v-if="showWriteModal"
          @close="showWriteModal = false"
          @submit="handleDuoSubmit"
      />

      <!-- 듀오 카드 그리드 -->
      <div
          class="grid grid-cols-1 gap-4 sm:gap-6"
          :class="{
            'md:grid-cols-2 lg:grid-cols-3': !isMobile,
            'md:grid-cols-2': isMobile
          }"
      >
        <div v-for="card in postCards"
             :key="card.postDto.postId"
             class="bg-[#141414] rounded-xl p-3 sm:p-4 transition-all duration-200 border border-[#2979FF1A] flex flex-col h-full"
        >
          <p class="text-white leading-relaxed line-clamp-3 whitespace-pre-wrap break-words bg-[#1A1A1A] p-3 rounded-lg mb-4 border-l-4 border-l-[#2979FF] min-h-24 flex content-center">
            {{ card.content }}
          </p>

          <!-- 포지션 & 게임 정보 -->
          <div :class="['flex items-center sm:items-start gap-2 mb-2 bg-[#1A1A1A] rounded-lg', isMobile ? 'p-2' : 'p-2.5']">
            <div class="grid grid-cols-4 gap-1.5 sm:gap-3 items-center sm:items-start flex-1">
              <div class="flex flex-col items-center">
                <span class="text-[10px] text-gray-400 mb-0.5">{{ isMobile ? '주포지션' : '주 포지션' }}</span>
                <div class="bg-[#141414] p-1 rounded">
                  <img
                      :src="getPositionImage(card.postDto.positionSelf)"
                      :alt="card.postDto.positionSelf"
                      class="w-4 h-4"
                  />
                </div>
              </div>

              <div class="flex flex-col items-center">
                <span class="text-[10px] text-gray-400 mb-0.5">{{ isMobile ? '듀오포지션' : '찾는 포지션' }}</span>
                <div class="bg-[#141414] p-1 rounded">
                  <img
                      :src="getPositionImage(card.postDto.positionFind)"
                      :alt="card.postDto.positionFind"
                      class="w-4 h-4"
                  />
                </div>
              </div>

              <div class="flex flex-col items-center">
                <span class="text-[10px] text-gray-400 mb-0.5">큐타입</span>
                <span class="text-xs text-white">
                  {{ getGameModeName(card.postDto.gameMode) }}
                </span>
              </div>

              <div v-if="true" class="flex flex-col items-center">
                <span class="text-[10px] text-gray-400 mb-0.5">{{ isMobile ? '마이크' : '마이크 사용' }}</span>
                <MicIcon v-if="card.postDto.isUseMic" class="w-4 h-4 text-[#2979FF]" />
                <MicOffIcon v-else class="w-4 h-4 text-gray-500" />
              </div>
            </div>
          </div>

          <!-- 소환사 프로필 섹션 -->
          <div
              :class="[
                  'bg-[#1A1A1A] rounded-lg rounded-b-none mt-auto', isMobile ? 'p-2.5' : 'p-3',
              ]"
              class="pb-0"
          >
            <div class="flex gap-2 sm:gap-3">
              <img
                  :src="card.duoSummonerInfoDto?.summonerBasicInfoDto.profileIconUrl"
                  alt="Game Profile"
                  class="rounded-full w-12 h-12 sm:w-14 sm:h-14 object-cover"
              />
              <div class="flex-1">
                <div class="flex flex-col gap-1">
                  <div
                      class="flex items-center gap-1 cursor-pointer group"
                      @click.stop="goSelectedSummonerProfile(card.duoSummonerInfoDto?.summonerBasicInfoDto.gameName, card.duoSummonerInfoDto?.summonerBasicInfoDto.tagLine)"
                  >
                    <span class="text-white text-sm font-medium group-hover:text-[#2979FF] group-hover:underline">
                      {{ card.duoSummonerInfoDto?.summonerBasicInfoDto.gameName }}
                    </span>
                              <span class="text-gray-400 text-xs">
                      #{{ card.duoSummonerInfoDto?.summonerBasicInfoDto.tagLine }}
                    </span>
                  </div>

                  <!-- 평가 점수와 평가 지표 재배치 -->
                  <div class="flex flex-col gap-1">
                    <div class="flex items-center gap-1">
                      <span class="text-gray-400 text-xs">
                        <HelpTooltip
                            message="신뢰도 95%의 윌슨 스코어로 계산된 평가 점수입니다.
                            긍정적인 평가와 부정적인 평가의 비율, 그리고 전체 평가 수를 고려하여
                            더 신뢰할 수 있는 점수를 계산합니다."
                        />
                        평가점수
                      </span>
                      <span class="text-[#2979FF] text-sm font-medium">
                        {{ card.duoSummonerInfoDto?.reviewStatsDto.score ?? 0 }}점
                      </span>
                    </div>
                    <div class="flex items-center gap-2">
                      <div class="flex items-center gap-1">
                        <ThumbsUp class="w-3 h-3 text-[#4CAF50]" />
                        <span class="text-[#4CAF50] text-xs">{{ card.duoSummonerInfoDto?.reviewStatsDto.upCount }}</span>
                      </div>
                      <div class="flex items-center gap-1">
                        <ThumbsDown class="w-3 h-3 text-[#FF5252]" />
                        <span class="text-[#FF5252] text-xs">{{ card.duoSummonerInfoDto?.reviewStatsDto.downCount }}</span>
                      </div>
                    </div>
                  </div>

                  <div class="flex gap-1.5 flex-wrap mt-1">
                    <tag-list :tags="card.duoSummonerInfoDto?.frequentTagDtoList" size="xSmall" :is-show-count="false"/>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 챔피언 정보 -->
          <div :class="['bg-[#1A1A1A] rounded-lg rounded-t-none', isMobile ? 'p-2.5' : 'p-3']">
            <div class="grid grid-cols-2 gap-2">
              <div
                  v-for="(champion, index) in card.duoSummonerInfoDto?.mostChampionDto.slice(0, 2)"
                  :key="index"
                  class="bg-[#141414] rounded-lg p-2 transition-colors duration-200"
              >
                <div class="flex items-start gap-2">
                  <div class="relative">
                    <img
                        :src="champion.iconUrl"
                        :alt="champion.nameUs"
                        :class="[isMobile ? 'w-10 h-10' : 'w-8 h-8', 'rounded']"
                    />
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center gap-1.5 mt-0.5">
                      <span class="text-gray-500 text-[10px]">승률</span>
                      <span class="text-[#4CAF50] text-[10px] font-medium">{{ champion.winRate }}%</span>
                    </div>
                    <div class="flex items-center gap-1 mt-0.5">
                      <ThumbsUp class="w-3 h-3 text-[#4CAF50]" />
                      <span class="text-[#4CAF50] text-xs">{{ champion.upCount }}</span>
                      <ThumbsDown class="w-3 h-3 text-[#FF5252]" />
                      <span class="text-[#FF5252] text-xs">{{ champion.downCount }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 더보기 버튼 -->
    <div v-if="showReadMore" class="max-w-7xl mx-auto mb-20">
      <button
          class="w-full bg-[#141414] border border-[#2979FF] text-[#2979FF] py-3 rounded-lg text-sm font-medium hover:bg-[rgba(41,121,255,0.1)] transition-all disabled:opacity-70 disabled:cursor-not-allowed disabled:border-[#4a4a4a] disabled:text-[#4a4a4a]"
          @click="onLoadMore"
          :disabled="isLoading"
      >
        <div class="flex justify-center items-center h-5">
          <span v-if="!isLoading">더보기</span>
          <div v-else class="spinner"></div>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import {ThumbsUp, ThumbsDown, MicIcon, MicOffIcon} from 'lucide-vue-next'
import WriteModal from '@/components/community/duo/WriteModal.vue'
import type { CommunityPostDto, DuoPostCardDto, SearchFilter } from "@/types/community.ts"
import { communityApi } from "@/api/community.ts"
import TagList from "@/components/common/TagList.vue"
import { useToast } from "vue-toastification"
import { goSelectedSummonerProfile } from "@/utils/common.ts"
import {useImageUrl} from "@/utils/imageUtil.ts";
import HelpTooltip from "@/components/common/HelpTooltip.vue";

const { getPositionImage } = useImageUrl();

const selectedPosition = ref('')
const selectedGameMode = ref('')
const selectedTier = ref('')
const showWriteModal = ref(false)
const showReadMore = ref(true)
const isLoading = ref(false)
const postCards = ref<DuoPostCardDto[]>([])
const currentStartIndex = ref(0)
const isMobile = computed(() => window.innerWidth < 640)
const isDesktop = computed(() => window.innerWidth >= 1024)

onMounted(async () => {
  await fetchPosts({})
})

const handleDuoSubmit = async (formData: CommunityPostDto) => {
  await communityApi.insertPost('duo', formData)
  showWriteModal.value = false
  const toast = useToast()
  toast.success('게시글 등록에 성공했습니다!')

  const currentFilter: SearchFilter = {
    gameMode: selectedGameMode.value,
    tier: selectedTier.value,
    positionSelf: selectedPosition.value,
    offset: 0,
    limit: 15
  }
  await fetchPosts(currentFilter)
}

const fetchPosts = async (filter: SearchFilter) => {
  isLoading.value = true
  const response = await communityApi.getPost('duo', filter)

  if (filter.offset === 0) {
    postCards.value = response.data
  } else {
    postCards.value = [...postCards.value, ...response.data]
  }

  showReadMore.value = response.data.length >= 15
  isLoading.value = false
}

const onFilterUpdate = () => {
  currentStartIndex.value = 0
  const reqDto: SearchFilter = {
    gameMode: selectedGameMode.value,
    tier: selectedTier.value,
    positionSelf: selectedPosition.value,
    offset: currentStartIndex.value,
    limit: 15
  }
  fetchPosts(reqDto)
}

const onLoadMore = () => {
  currentStartIndex.value += 15
  const reqDto: SearchFilter = {
    gameMode: selectedGameMode.value,
    tier: selectedTier.value,
    positionSelf: selectedPosition.value,
    offset: currentStartIndex.value,
    limit: 15
  }
  fetchPosts(reqDto)
}

const getGameModeName = (code: string) => {
  const gameModeMap = {
    'ARAM': '칼바람나락',
    'NORMAL': '일반게임',
    'SOLO_RANK': '개인랭크',
    'FLEX_RANK': '자유랭크'
  }
  return gameModeMap[code] || code
}
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.spinner {
  border: 2px solid transparent;
  border-top: 2px solid #2979FF;
  border-right: 2px solid #2979FF;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>