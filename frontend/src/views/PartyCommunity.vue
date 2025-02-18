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

      <!-- 듀오 카드 그리드 -->
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <div
            v-for="card in postCards"
            :key="card.postDto.postId"
            class="bg-[#141414] rounded-xl p-4 border border-[#2979FF1A]"
        >
          <!-- 본문 -->
          <div class="bg-[#1A1A1A] p-3 rounded-lg mb-4 border-l-4 border-l-[#2979FF] min-h-[102px]">
            <p class="text-white leading-relaxed line-clamp-3 whitespace-pre-wrap break-words">
              {{ card.content }}
            </p>
          </div>
            <!-- 포지션 그리드 -->
            <div class="grid grid-cols-1 gap-2 mb-4">
              <!-- 각 포지션 슬롯 -->
              <div
                  v-for="position in ['TOP', 'JG', 'MID', 'AD', 'SUP']"
                  :key="position"
                  class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between text-center h-[52px] w-full"
                  :class="{
        'border-2 border-[#2979FF]': card.postDto.positionSelf === position,
        'border border-[#383838]': card.postDto.positionSelf !== position
      }"
              >
                <!-- 포지션 채워진 경우 -->
                <template v-if="card.postDto.positionSelf === position && card.duoSummonerInfoDto">
                  <div class="flex items-center gap-2">
                    <img
                        :src="getPositionImage(position)"
                        :alt="position"
                        class="w-5 h-5"
                    >
                  </div>

                  <div class="flex-1 text-left ml-2 min-w-0">
                    <div class="flex items-center gap-1">
                      <div
                          class="text-xs font-medium text-white hover:text-[#2979FF] cursor-pointer truncate max-w-full"
                          @click="goSelectedSummonerProfile(
        card.duoSummonerInfoDto.summonerBasicInfoDto.gameName,
        card.duoSummonerInfoDto.summonerBasicInfoDto.tagLine
      )"
                      >
                        {{ card.duoSummonerInfoDto.summonerBasicInfoDto.gameName }}
                      </div>
                      <div class="text-[10px] text-gray-400 truncate">
                        #{{ card.duoSummonerInfoDto.summonerBasicInfoDto.tagLine }}
                      </div>
                    </div>

                    <!-- frequentTag 추가 -->
                    <div class="flex gap-1 mt-0.5">
    <span
        v-for="tag in card.duoSummonerInfoDto.frequentTagDtoList"
        :key="tag.name"
        class="bg-[#2979FF]/10 text-[#2979FF] text-[9px] px-1 py-0.5 rounded"
    >
      {{ tag.name }}
    </span>
                    </div>
                  </div>

                  <div class="flex flex-col items-end">
                    <div class="flex items-center gap-1 text-[10px] mb-0.5">
                      <span class="text-gray-400">평가</span>
                      <span class="text-[#2979FF] font-medium">
              {{ card.duoSummonerInfoDto.reviewStatsDto.score?.toFixed(1) ?? 0 }}점
            </span>
                    </div>

                    <div class="flex items-center gap-2">
                      <div class="flex items-center gap-1">
                        <ThumbsUp class="w-3 h-3 text-[#4CAF50]" />
                        <span class="text-[#4CAF50] text-[10px]">
                {{ card.duoSummonerInfoDto.reviewStatsDto.upCount }}
              </span>
                      </div>
                      <div class="flex items-center gap-1">
                        <ThumbsDown class="w-3 h-3 text-[#FF5252]" />
                        <span class="text-[#FF5252] text-[10px]">
                {{ card.duoSummonerInfoDto.reviewStatsDto.downCount }}
              </span>
                      </div>
                    </div>
                  </div>

                  <div class="flex gap-1 ml-2">
                    <div
                        v-for="(champion, index) in card.duoSummonerInfoDto.mostChampionDto.slice(0, 2)"
                        :key="index"
                        class="bg-[#141414] rounded-lg p-1 flex flex-col items-center"
                    >
                      <img
                          :src="champion.iconUrl"
                          :alt="champion.nameUs"
                          class="w-5 h-5 rounded mb-0.5"
                      >
                      <span class="text-[9px] text-[#4CAF50]">
              {{ champion.winRate }}%
            </span>
                    </div>
                  </div>
                </template>

                <!-- 대기 중인 경우 -->
                <template v-else>
                  <img
                      :src="getPositionImage(position)"
                      :alt="position"
                      class="w-5 h-5"
                  >
                  <div class="text-gray-500 text-xs flex-1">대기 중</div>
                </template>
              </div>
            </div>

          <!-- 추가 정보 -->
          <div class="bg-[#1A1A1A] rounded-lg p-3 flex justify-between items-center">
            <div class="flex items-center gap-2">
              <span class="text-gray-400 text-xs">큐타입</span>
              <span class="text-white text-sm">
                {{ getGameModeName(card.postDto.gameMode) }}
              </span>
            </div>
            <div>
              <MicIcon
                  v-if="card.postDto.isUseMic"
                  class="w-4 h-4 text-[#2979FF]"
              />
              <MicOffIcon
                  v-else
                  class="w-4 h-4 text-gray-500"
              />
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ThumbsUp, ThumbsDown, MicIcon, MicOffIcon } from 'lucide-vue-next'
import WriteModal from '@/components/community/duo/WriteModal.vue'
import type { CommunityPostDto, DuoPostCardDto, SearchFilter } from "@/types/community.ts"
import { communityApi } from "@/api/community.ts"
import TagList from "@/components/common/TagList.vue"
import { useToast } from "vue-toastification"
import { goSelectedSummonerProfile } from "@/utils/common.ts"
import { useImageUrl } from "@/utils/imageUtil.ts"

const { getPositionImage } = useImageUrl()

const selectedPosition = ref('')
const selectedGameMode = ref('')
const selectedTier = ref('')
const showWriteModal = ref(false)
const showReadMore = ref(true)
const isLoading = ref(false)
/*const postCards = ref<DuoPostCardDto[]>([])*/
const currentStartIndex = ref(0)
const isMobile = computed(() => window.innerWidth < 640)

const dummyPostCards = [
  {
    postDto: {
      postId: '1',
      positionSelf: 'TOP',
      gameMode: 'SOLO_RANK',
      isUseMic: true,
      content: '탑 포지션 듀오 구해요! 매너있고 소통 잘하는 분 환영합니다. 같이 즐겁게 플레이해요!'
    },
    duoSummonerInfoDto: {
      summonerBasicInfoDto: {
        profileIconUrl: 'https://example.com/profile1.png',
        gameName: 'Hide on bush',
        tagLine: 'KR1'
      },
      reviewStatsDto: {
        score: 4.7,
        upCount: 128,
        downCount: 12
      },
      frequentTagDtoList: [
        { name: '매너' },
        { name: '친절' }
      ],
      mostChampionDto: [
        {
          iconUrl: 'https://example.com/champion1.png',
          nameUs: 'Darius',
          winRate: 61
        },
        {
          iconUrl: 'https://example.com/champion2.png',
          nameUs: 'Garen',
          winRate: 58
        }
      ]
    },
    content: '탑 포지션 듀오 구해요! 매너있고 소통 잘하는 분 환영합니다. 같이 즐겁게 플레이해요!'
  },
  {
    postDto: {
      postId: '2',
      positionSelf: 'JG',
      gameMode: 'FLEX_RANK',
      isUseMic: false,
      content: '정글러 구합니다. 트롤 노노하고 승리에 진심인 분만 와주세요.'
    },
    duoSummonerInfoDto: {
      summonerBasicInfoDto: {
        profileIconUrl: 'https://example.com/profile2.png',
        gameName: 'Fierce Jungler',
        tagLine: 'KR2'
      },
      reviewStatsDto: {
        score: 4.2,
        upCount: 95,
        downCount: 25
      },
      frequentTagDtoList: [
        { name: '숙련' },
        { name: '소통' }
      ],
      mostChampionDto: [
        {
          iconUrl: 'https://example.com/champion3.png',
          nameUs: 'Lee Sin',
          winRate: 65
        },
        {
          iconUrl: 'https://example.com/champion4.png',
          nameUs: 'Elise',
          winRate: 59
        }
      ]
    },
    content: '정글러 구합니다. 트롤 노노하고 승리에 진심인 분만 와주세요.'
  },
  {
    postDto: {
      postId: '3',
      positionSelf: 'MID',
      gameMode: 'NORMAL',
      isUseMic: true,
      content: '같이 재미있게 즐길 미드 라이너 찾아요. 스트레스 받지 말고 즐겁게 해요!'
    },
    duoSummonerInfoDto: {
      summonerBasicInfoDto: {
        profileIconUrl: 'https://example.com/profile3.png',
        gameName: 'Mid Master',
        tagLine: 'KR3'
      },
      reviewStatsDto: {
        score: 4.5,
        upCount: 110,
        downCount: 18
      },
      frequentTagDtoList: [
        { name: '친절' },
        { name: '위트' }
      ],
      mostChampionDto: [
        {
          iconUrl: 'https://example.com/champion5.png',
          nameUs: 'Ahri',
          winRate: 62
        },
        {
          iconUrl: 'https://example.com/champion6.png',
          nameUs: 'Syndra',
          winRate: 57
        }
      ]
    },
    content: '같이 재미있게 즐길 미드 라이너 찾아요. 스트레스 받지 말고 즐겁게 해요!'
  }
]
const postCards = ref<DuoPostCardDto[]>(dummyPostCards)

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
  const gameModeMap: {[key: string]: string} = {
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

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>