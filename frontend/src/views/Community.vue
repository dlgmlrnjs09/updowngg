<template>
  <div class="min-h-screen bg-[#0A0A0A] p-6">
    <div class="max-w-7xl mx-auto mb-8">
      <!-- 상단 영역 -->
      <div class="flex justify-between items-center mt-20 mb-14">
        <div class="bg-[#141414] p-3 rounded-xl flex-1 mr-4">
          <div class="flex gap-3 items-center">
            <select
                v-model="selectedGameMode"
                class="bg-[#1A1A1A] text-white text-sm px-3 py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
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
                class="bg-[#1A1A1A] text-white text-sm px-3 py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
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
                class="bg-[#1A1A1A] text-white text-sm px-3 py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
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
        </div>
        <button
            @click="showWriteModal = true"
            class="bg-[#2979FF] text-white px-4 py-1.5 rounded-lg hover:bg-[#2565D1] transition-colors text-sm whitespace-nowrap h-12"
        >
          듀오 등록하기
        </button>
      </div>

      <!-- 글쓰기 모달 -->
      <WriteModal
          v-if="showWriteModal"
          @close="showWriteModal = false"
          @submit="handleDuoSubmit"
      />

      <!-- 듀오 카드 그리드 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <div
            v-for="card in postCards"
            :key="card.postDto.postId"
            class="bg-[#141414] rounded-xl p-4 group transition-all duration-200 h-[360px] flex flex-col"
        >
          <div class="flex items-start gap-3 mb-2 bg-[#1A1A1A] p-2 rounded-lg">
            <div class="grid grid-cols-4 gap-3 items-start flex-1">
              <!-- 나의 포지션 -->
              <div class="flex flex-col items-center">
                <span class="text-[10px] text-gray-400 mb-0.5">주 포지션</span>
                <div class="bg-[#141414] p-1 rounded">
                  <img
                      :src="`/src/assets/icon/position/position_${card.postDto.positionSelf}.svg`"
                      :alt="card.postDto.positionSelf"
                      class="w-4 h-4"
                  />
                </div>
              </div>

              <!-- 찾는 포지션 -->
              <div class="flex flex-col items-center">
                <span class="text-[10px] text-gray-400 mb-0.5">찾는 포지션</span>
                <div class="bg-[#141414] p-1 rounded">
                  <img
                      :src="require(`@/assets/icon/position/position_${card.postDto.positionFind}.svg`)"
                      :alt="card.postDto.positionFind"
                      class="w-4 h-4"
                  />
                </div>
              </div>

              <!-- 큐 타입 -->
              <div class="flex flex-col items-center">
                <span class="text-[10px] text-gray-400 mb-0.5">큐 타입</span>
                <span class="text-xs text-white">
                  {{ getGameModeName(card.postDto.gameMode) }}
                </span>
              </div>

              <!-- 음성 -->
              <div v-if="true" class="flex flex-col items-center">
                <span class="text-[10px] text-gray-400 mb-0.5">마이크 사용</span>
                <MicIcon class="w-4 h-4 text-[#2979FF]" />
              </div>
            </div>
          </div>

          <!-- 내용 -->
          <p class="text-white text-base leading-relaxed mb-3 line-clamp-3 mt-3 font-pretendard">{{ card.content }}</p>

          <div class="mt-auto"></div>

          <!-- 소환사 프로필 섹션 -->
          <div class="bg-[#1A1A1A] rounded-lg rounded-b-none p-3 transition-colors duration-200">
            <div class="flex gap-3">
              <img
                  :src="card.duoSummonerInfoDto?.summonerBasicInfoDto.profileIconUrl"
                  alt="Game Profile"
                  class="rounded-full w-[4.6rem] h-[4.6rem]"
              />
              <div class="flex-1">
                <div class="flex justify-between h-[4.6rem]">
                  <div class="flex flex-col justify-between py-1">
                    <div class="flex items-center gap-2">
                      <div class="text-white text-sm font-medium">
                        {{ card.duoSummonerInfoDto?.summonerBasicInfoDto.gameName }} #{{card.duoSummonerInfoDto?.summonerBasicInfoDto.tagLine}}
                      </div>
                    </div>
                    <div class="flex items-center gap-3">
                      <div class="flex items-center gap-1">
                        <ThumbsUp class="w-3 h-3 text-[#4CAF50]" />
                        <span class="text-[#4CAF50] text-xs">{{ card.duoSummonerInfoDto?.reviewStatsDto.upCount }}</span>
                      </div>
                      <div class="flex items-center gap-1">
                        <ThumbsDown class="w-3 h-3 text-[#FF5252]" />
                        <span class="text-[#FF5252] text-xs">{{ card.duoSummonerInfoDto?.reviewStatsDto.downCount }}</span>
                      </div>
                    </div>
                    <div class="flex gap-1.5 flex-wrap">
                      <tag-list :tags="card.duoSummonerInfoDto?.frequentTagDtoList" size="xSmall" :is-show-count="false"/>
                    </div>
                  </div>
                  <!-- 버튼 영역 -->
                  <div class="flex flex-col gap-1 ml-2 justify-center">
                    <button class="bg-[#5865F2] hover:bg-[#3d61df] text-white text-xs px-3 py-1 rounded-lg transition-colors flex items-center justify-center">
                      <img class="w-5 h-5" src="@/assets/icon/discord_logo.svg" alt="discord"/>
                    </button>
                    <button class="bg-[#1F1F1F] text-white text-xs px-3 py-1 rounded-lg hover:bg-[#2A2A2A] transition-colors" style="border: 1.5px solid #ababab; color: #888">
                      <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 게임 정보 섹션 -->
          <div class="bg-[#1A1A1A] rounded-lg rounded-t-none divide-y divide-[#232323] transition-colors duration-200">
            <!-- 챔피언 정보 -->
            <div class="px-3 py-2">
              <div class="grid grid-cols-3 gap-2">
                <div
                    v-for="(champion, index) in card.duoSummonerInfoDto?.mostChampionDto"
                    :key="index"
                    class="bg-[#141414] rounded-lg p-2 transition-colors duration-200"
                >
                  <div class="flex items-start gap-2">
                    <div class="relative">
                      <img
                          :src="champion.iconUrl"
                          :alt="champion.nameUs"
                          class="w-8 h-8 rounded"
                      />
                    </div>
                    <div class="flex-1">
                      <div class="flex items-center gap-1.5 mt-0.5">
                        <span class="text-gray-500 text-[10px]">승률</span>
                        <span class="text-[#4CAF50] text-[10px] font-medium">{{ champion.winRate }}%</span>
                      </div>
                      <div class="flex items-center gap-1 mt-0.5">
                        <ThumbsUp class="w-3 h-3 text-[#4CAF50]" />
                        <span class="text-[#4CAF50] text-xs">{{ card.duoSummonerInfoDto?.reviewStatsDto.upCount }}</span>
                        <ThumbsDown class="w-3 h-3 text-[#FF5252]" />
                        <span class="text-[#FF5252] text-xs">{{ card.duoSummonerInfoDto?.reviewStatsDto.downCount }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-if="showReadMore" class="max-w-7xl mx-auto mb-20">
    <div class="grid grid-cols-1">
      <button
          class="load-more-button"
          @click="onLoadMore"
      >
        <div class="button-content">
          <span v-if="!isLoading">더보기</span>
          <div v-else class="spinner"></div>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ThumbsUp, ThumbsDown, MicIcon } from 'lucide-vue-next'
import WriteModal from '@/components/community/duo/WriteModal.vue'
import type {CommunityPostDto, DuoPostCardDto, SearchFilter} from "@/types/community.ts";
import { communityApi } from "@/api/community.ts";
import TagList from "@/components/common/TagList.vue";
import { useToast } from "vue-toastification";
import type {RankingSearchFilter} from "@/types/ranking.ts";

const selectedPosition = ref('')
const selectedGameMode = ref('')
const selectedTier = ref('')
const showWriteModal = ref(false)
const showReadMore = ref(true)
const isLoading = ref(false)
const postCards = ref<DuoPostCardDto[]>([]);
const currentStartIndex = ref(0);

onMounted(async () => {
  await fetchPosts({})
})

const handleDuoSubmit = async (formData: CommunityPostDto) => {
  await communityApi.insertPost('duo', formData);
  showWriteModal.value = false
  const toast = useToast();
  toast.success('게시글 등록에 성공했습니다!')
  await fetchPosts({});
}

const fetchPosts = async (filter: SearchFilter) => {
  isLoading.value = true
  const response = await communityApi.getPost('duo', filter);

  if (filter.offset === 0) {
    postCards.value = response.data
  } else {
    postCards.value = [...postCards.value, ...response.data]
  }

  showReadMore.value = response.data.length >= 15;
  isLoading.value = false
}

// 필터 변경시 (셀렉트박스 변경)
const onFilterUpdate = () => {
  currentStartIndex.value = 0
  const reqDto: SearchFilter = {
    gameMode: selectedGameMode.value,
    tier: selectedTier.value,
    positionSelf: selectedPosition.value,
    offset: currentStartIndex.value,
    limit: 15
  };
  fetchPosts(reqDto)
}

// 더보기 버튼 클릭시
const onLoadMore = () => {
  currentStartIndex.value += 15  // 더보기 시에만 증가
  const reqDto: SearchFilter = {
    gameMode: selectedGameMode.value,
    tier: selectedTier.value,
    positionSelf: selectedPosition.value,
    offset: currentStartIndex.value,
    limit: 15
  };
  fetchPosts(reqDto)
}

const getGameModeName = (code: string) => {
  const gameModeMap = {
    'ARAM': '칼바람나락',
    'NORMAL': '일반게임',
    'SOLO_RANK': '개인랭크',
    'FLEX_RANK': '자유랭크'
  };
  return gameModeMap[code] || code;
};
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.load-more-button {
  background: #141414;
  border: 1px solid #2979FF;
  color: #2979FF;
  padding: 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
  height: 48px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.load-more-button:hover:not(:disabled) {
  background: rgba(41, 121, 255, 0.1);
}

.load-more-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  border-color: #4a4a4a;
  color: #4a4a4a;
}

.button-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 20px;
}
</style>