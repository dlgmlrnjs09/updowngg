<template>
  <div class="min-h-screen bg-[#0A0A0A] p-6">
    <!-- 컨텐츠 컨테이너 -->
    <div class="max-w-[1000px] mx-auto">
      <!-- 탭 네비게이션 -->
      <div class="flex justify-between items-center mb-8">
        <div class="flex gap-4">
          <button
              @click="handleTabChange('current')"
              :class="[
              'px-4 py-2 rounded-lg text-sm transition-colors',
              activeTab === 'current'
                ? 'bg-[#2979FF] text-white'
                : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
            ]"
          >현재 파티
          </button>
          <button
              @click="handleTabChange('history')"
              :class="[
              'px-4 py-2 rounded-lg text-sm transition-colors',
              activeTab === 'history'
                ? 'bg-[#2979FF] text-white'
                : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
            ]"
          >히스토리
          </button>
        </div>
      </div>

      <!-- Current Party Section -->
      <div v-if="activeTab === 'current'" class="space-y-4 min-h-[600px]">
        <div v-if="myActivePost" class="bg-[#141414] rounded-xl border border-[#ffffff1a] p-4">
          <!-- 파티 상단 버튼들 -->
          <div class="flex justify-end gap-2 mb-4">
            <button
                @click="handleCloseRecruitment"
                class="bg-[#2979FF] hover:bg-[#2565D1] text-white px-3 py-1.5 rounded flex items-center gap-1 text-sm"
            >
              <Users class="w-4 h-4"/>
              모집 마감
            </button>
            <button
                @click="handleCancelParty"
                class="bg-[#FF5252] hover:bg-[#D32F2F] text-white px-3 py-1.5 rounded flex items-center gap-1 text-sm"
            >
              <X class="w-4 h-4"/>
              모집 취소
            </button>
          </div>

          <!-- 파티 본문 -->
          <div class="bg-[#1A1A1A] p-3 rounded-lg mb-4 border-l-4 border-l-[#2979FF]">
            <p class="text-white leading-relaxed whitespace-pre-wrap break-words">
              {{ myActivePost?.postCardDto.content }}
            </p>
          </div>

          <!-- 포지션 섹션 -->
          <div class="space-y-3">
            <div v-for="(applicants, position) in myActivePost?.applicantByPositionMap" :key="position"
                 class="bg-[#1A1A1A] rounded-lg p-3">
              <!-- 현재 참여자 정보 -->
              <div class="flex items-center justify-between h-[52px]">
                <div class="flex items-center gap-2">
                  <img :src="getPositionImage(position)" :alt="position" class="w-5 h-5"/>
                </div>

                <!-- 참여자가 있는 경우 -->
                <template v-if="getParticipantByPosition[position]?.summonerInfoDto">
                  <div class="flex-1 text-left ml-2 min-w-0">
                    <div class="flex items-center gap-1">
                      <div class="text-xs font-medium text-white hover:text-[#2979FF] cursor-pointer truncate max-w-full">
                        {{ getParticipantByPosition[position].summonerInfoDto.summonerBasicInfoDto.gameName }}
                      </div>
                      <div class="text-[10px] text-gray-400 truncate">
                        #{{ getParticipantByPosition[position].summonerInfoDto.summonerBasicInfoDto.tagLine }}
                      </div>
                    </div>

                    <div class="flex gap-1 mt-0.5">
                      <span
                          v-for="tag in getParticipantByPosition[position].summonerInfoDto.frequentTagDtoList"
                          :key="tag.tagCode"
                          class="bg-[#2979FF]/10 text-[#2979FF] text-[9px] px-1 py-0.5 rounded"
                      >
                        {{ tag.tagValue }}
                      </span>
                    </div>
                  </div>

                  <div class="flex flex-col items-end">
                    <div class="flex items-center gap-1 text-[10px] mb-0.5">
                      <span class="text-gray-400">평가</span>
                      <span class="text-[#2979FF] font-medium">
                        {{ getParticipantByPosition[position].summonerInfoDto.reviewStatsDto.score?.toFixed(1) ?? 0 }}점
                      </span>
                    </div>

                    <div class="flex items-center gap-2">
                      <div class="flex items-center gap-1">
                        <ThumbsUp class="w-3 h-3 text-[#4CAF50]"/>
                        <span class="text-[#4CAF50] text-[10px]">
                          {{ getParticipantByPosition[position].summonerInfoDto.reviewStatsDto.upCount }}
                        </span>
                      </div>
                      <div class="flex items-center gap-1">
                        <ThumbsDown class="w-3 h-3 text-[#FF5252]"/>
                        <span class="text-[#FF5252] text-[10px]">
                          {{ getParticipantByPosition[position].summonerInfoDto.reviewStatsDto.downCount }}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div class="flex gap-1 ml-2">
                    <div
                        v-for="(champion, index) in getParticipantByPosition[position].summonerInfoDto.mostChampionDto?.slice(0, 2)"
                        :key="index"
                        class="bg-[#141414] rounded-lg p-1 flex flex-col items-center"
                    >
                      <img
                          :src="champion.iconUrl"
                          :alt="champion.nameKr"
                          class="w-5 h-5 rounded mb-0.5"
                      >
                      <span class="text-[9px] text-[#4CAF50]">
                        {{ champion.winRate }}%
                      </span>
                    </div>
                  </div>
                </template>

                <!-- 참여자가 없는 경우 -->
                <template v-else>
                  <div class="text-gray-500 text-xs flex-1">
                    {{ myActivePost?.postCardDto.participantDtoList.find(p => p.position === position)?.isOpenPosition ? '대기 중' : '-' }}
                  </div>
                </template>

                <!-- 신청자 수와 토글 버튼 -->
                <div v-if="applicants?.length && !getParticipantByPosition[position]?.summonerInfoDto" class="flex items-center gap-2 ml-2">
                  <span class="text-[#2979FF] text-sm">신청자 {{ applicants.length }}명</span>
                  <button
                      @click="toggleApplicants(position)"
                      class="text-[#2979FF] hover:bg-[#2979FF1A] p-1 rounded"
                  >
                    <component :is="showApplicants[position] ? ChevronUp : ChevronDown" class="w-4 h-4"/>
                  </button>
                </div>
              </div>

              <!-- 신청자 목록 -->
              <div v-if="showApplicants[position] && applicants?.length"
                   class="mt-3 border border-[#333] rounded-lg overflow-hidden">
                <!-- 신청자 헤더 -->
                <div class="bg-[#1A1A1A] px-4 py-2 text-xs text-gray-400 flex items-center">
                  <div class="w-[240px]">신청자</div>
                  <div class="w-[140px]">평가</div>
                  <div class="w-[120px]">모스트 챔피언</div>
                  <div class="flex-1 text-right">승인/거절</div>
                </div>

                <!-- 신청자 리스트 -->
                <div class="divide-y divide-[#333]">
                  <div v-for="applicant in applicants"
                       :key="applicant"
                       class="px-4 py-3 flex items-center hover:bg-[#1A1A1A] transition-colors">
                    <!-- 신청자 정보 -->
                    <div class="w-[240px] flex items-center gap-3">
                      <img :src="applicant.summonerInfoDto.summonerBasicInfoDto.profileIconUrl"
                           :alt="applicant.summonerInfoDto.summonerBasicInfoDto.profileIconId"
                           class="w-10 h-10 rounded-lg"/>
                      <div>
                        <div class="flex items-center gap-1">
            <span class="text-white text-sm font-medium">
              {{ applicant.summonerInfoDto.summonerBasicInfoDto.gameName }}
            </span>
                          <span class="text-gray-400 text-xs">
              #{{ applicant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
            </span>
                        </div>
                        <div class="flex gap-1 mt-1">
            <span v-for="tag in applicant.summonerInfoDto.frequentTagDtoList"
                  :key="tag.tagCode"
                  class="bg-[#2979FF]/10 text-[#2979FF] text-[9px] px-1.5 py-0.5 rounded">
              {{ tag.tagValue }}
            </span>
                        </div>
                      </div>
                    </div>

                    <!-- 평가 정보 -->
                    <div class="w-[140px] flex flex-col">
                      <div class="flex items-center gap-2">
          <span class="text-[#2979FF] text-sm font-medium">
            {{ applicant.summonerInfoDto.reviewStatsDto.score ?? 0 }}점
          </span>
                        <span class="text-gray-400 text-xs">
            ({{ applicant.summonerInfoDto.reviewStatsDto.totalReviewCnt }}회)
          </span>
                      </div>
                      <div class="flex items-center gap-3 mt-1">
                        <div class="flex items-center gap-1">
                          <ThumbsUp class="w-3 h-3 text-[#4CAF50]"/>
                          <span class="text-[#4CAF50] text-xs">
              {{ applicant.summonerInfoDto.reviewStatsDto.upCount }}
            </span>
                        </div>
                        <div class="flex items-center gap-1">
                          <ThumbsDown class="w-3 h-3 text-[#FF5252]"/>
                          <span class="text-[#FF5252] text-xs">
              {{ applicant.summonerInfoDto.reviewStatsDto.downCount }}
            </span>
                        </div>
                      </div>
                    </div>

                    <!-- 모스트 챔피언 -->
                    <div class="w-[120px] flex gap-2">
                      <div v-for="(champion, index) in applicant.summonerInfoDto.mostChampionDto?.slice(0, 2)"
                           :key="index"
                           class="bg-[#141414] rounded-lg p-1.5 flex flex-col items-center">
                        <img :src="champion.iconUrl"
                             :alt="champion.nameKr"
                             class="w-6 h-6 rounded mb-1">
                        <span class="text-[#4CAF50] text-[10px]">{{ champion.winRate }}%</span>
                      </div>
                    </div>

                    <!-- 승인/거절 버튼 -->
                    <div class="flex-1 flex justify-end gap-2">
                      <button @click="handleApprove(applicant.postId, applicant.applicantSeq, position)"
                              class="bg-[#2979FF] hover:bg-[#2565D1] text-white px-3 py-1.5 rounded-lg flex items-center gap-1.5 text-xs font-medium transition-colors">
                        <UserCheck class="w-4 h-4"/>
                      </button>
                      <button @click="handleReject(applicant.postId, applicant.applicantSeq, position)"
                              class="bg-[#FF5252] hover:bg-[#D32F2F] text-white px-3 py-1.5 rounded-lg flex items-center gap-1.5 text-xs font-medium transition-colors">
                        <UserX class="w-4 h-4"/>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 파티 정보 -->
          <div class="bg-[#1A1A1A] rounded-lg p-3 mt-4 flex justify-between items-center">
            <div class="flex items-center gap-2">
              <span class="text-gray-400 text-xs">큐타입</span>
              <span class="text-white text-sm">{{ getGameModeName(myActivePost.postCardDto.gameMode) }}</span>
            </div>
            <div>
              <component :is="myActivePost.postCardDto.isUseMic ? Mic : MicOff"
                         :class="myActivePost.postCardDto.isUseMic ? 'text-[#2979FF]' : 'text-gray-500'"
                         class="w-4 h-4"/>
            </div>
          </div>
        </div>
        <div v-else class="flex-1 flex flex-col items-center justify-center">
          <img class="w-28 h-28 text-gray-600 mb-4" src="@/assets/icon/emoji/crying_bee.png" alt="crying_bee">
          <h3 class="text-lg font-medium text-gray-200 mb-2">현재 등록된 파티가 없습니다</h3>
          <p class="text-gray-400 text-sm">새로운 파티를 등록해보세요!</p>
        </div>
      </div>

      <!-- History Section -->
      <div v-else class="space-y-4">
        <!-- 모집했던 파티 -->
        <div class="bg-[#141414] rounded-xl p-4">
          <h3 class="text-white text-lg mb-4 flex items-center gap-2">
            <Users class="w-5 h-5"/>
            모집했던 파티
          </h3>
          <div class="space-y-3">
            <div v-for="party in hostedParties" :key="party.id"
                 class="bg-[#1A1A1A] rounded-lg p-3">
              <div class="flex justify-between items-center mb-2">
                <span class="text-gray-400 text-sm">{{ formatTimeAgo(party.createdAt) }}</span>
                <span :class="party.status === '마감' ? 'text-[#2979FF]' : 'text-[#FF5252]'"
                      class="text-sm">
                  {{ party.status }}
                </span>
              </div>
              <p class="text-white mb-2">{{ party.content }}</p>
              <div class="flex justify-between items-center text-sm">
                <span class="text-gray-400">{{ getGameModeName(party.gameMode) }}</span>
                <span class="text-gray-400">참가자 {{ party.participantCount }}명</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 신청했던 파티 -->
        <div class="bg-[#141414] rounded-xl p-4">
          <h3 class="text-white text-lg mb-4 flex items-center gap-2">
            <History class="w-5 h-5"/>
            신청했던 파티
          </h3>
          <div class="space-y-3">
            <div v-for="party in appliedParties" :key="party.id"
                 class="bg-[#1A1A1A] rounded-lg p-3">
              <div class="flex justify-between items-center mb-2">
                <span class="text-gray-400 text-sm">{{ formatTimeAgo(party.createdAt) }}</span>
                <span :class="getStatusColor(party.status)" class="text-sm">
                  {{ party.status }}
                </span>
              </div>
              <p class="text-white mb-2">{{ party.content }}</p>
              <div class="flex justify-between items-center text-sm">
                <span class="text-gray-400">{{ getGameModeName(party.gameMode) }}</span>
                <span class="text-gray-400">신청 포지션: {{ party.position }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="activeTab === 'history' && (hostedParties.length > 0 || appliedParties.length > 0)"
           class="flex justify-center mt-6 gap-2">
        <button
            v-if="paging.hasPrevious"
            @click="handlePrevPage"
            class="px-3 h-8 rounded-lg text-sm font-medium bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]"
        >
          <
        </button>
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
import {ref, computed, onMounted, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {
  ThumbsUp, ThumbsDown, Mic, MicOff, ChevronDown, ChevronUp,
  UserCheck, UserX, History, Users, X
} from 'lucide-vue-next'
import {createInitialPaging, formatTimeAgo, getPageNumbers} from '@/utils/common'
import {useToast} from 'vue-toastification'
import {useImageUrl} from '@/utils/imageUtil'
import {communityApi} from '@/api/community'
import type {MyPartyPostDto} from "@/types/community.ts";

const {getPositionImage} = useImageUrl()
const route = useRoute()
const router = useRouter()
const toast = useToast()

const activeTab = ref('current')
const showApplicants = ref<{ [key in 'TOP' | 'JG' | 'MID' | 'BOT' | 'SUP']: boolean }>({
  'TOP': false,
  'JG': false,
  'MID': false,
  'BOT': false,
  'SUP': false
})

const myActivePost = ref<MyPartyPostDto | null>(null)

const hostedParties = ref([
  {
    id: 1,
    content: "솔랭 파티 구합니다",
    gameMode: "SOLO_RANK",
    createdAt: "2024-02-20T15:00:00",
    status: "마감",
    participantCount: 5
  },
  {
    id: 2,
    content: "자랭 같이 하실 분~",
    gameMode: "FLEX_RANK",
    createdAt: "2024-02-19T10:00:00",
    status: "마감",
    participantCount: 3
  },
  {
    id: 3,
    content: "빠른대전 한판 고?",
    gameMode: "NORMAL",
    createdAt: "2024-02-18T20:00:00",
    status: "취소",
    participantCount: 2
  }
])

const appliedParties = ref([
  {
    id: 1,
    content: "칼바람 한판 고고",
    gameMode: "ARAM",
    createdAt: "2024-02-20T14:30:00",
    status: "승인",
    position: "MID"
  },
  {
    id: 2,
    content: "일반 같이 하실 분",
    gameMode: "NORMAL",
    createdAt: "2024-02-19T09:30:00",
    status: "거절",
    position: "TOP"
  },
  {
    id: 3,
    content: "솔랭 파티 구해요",
    gameMode: "SOLO_RANK",
    createdAt: "2024-02-18T18:30:00",
    status: "대기중",
    position: "JG"
  }
])

const paging = ref(createInitialPaging())

const pageNumbers = computed(() => getPageNumbers(paging.value))

const getParticipantByPosition = computed(() => {
  if (!myActivePost.value?.postCardDto.participantDtoList) return {};

  return myActivePost.value.postCardDto.participantDtoList.reduce((acc, participant) => {
    acc[participant.position] = participant;
    return acc;
  }, {} as Record<string, any>);
});

const getStatusColor = (status) => {
  const colors = {
    '승인': 'text-[#4CAF50]',
    '거절': 'text-[#FF5252]',
    '마감': 'text-[#2979FF]',
    '취소': 'text-[#FF5252]',
    '대기중': 'text-gray-400'
  }
  return colors[status] || 'text-gray-400'
}

const getGameModeName = (code) => {
  const gameModeMap = {
    'ARAM': '칼바람나락',
    'NORMAL': '일반게임',
    'SOLO_RANK': '개인랭크',
    'FLEX_RANK': '자유랭크'
  }
  return gameModeMap[code] || code
}

const toggleApplicants = (position: 'TOP' | 'JG' | 'MID' | 'BOT' | 'SUP') => {
  showApplicants.value[position] = !showApplicants.value[position]
}

const handleTabChange = async (tab) => {
  await router.push({
    query: {...route.query, tab, page: '1'}
  })
}

const handlePageChange = async (page) => {
  await router.push({
    query: {...route.query, page: page.toString()}
  })
}

const handlePrevPage = () => {
  paging.value.currentPage = paging.value.startPage - 1
  fetchParties()
}

const handleNextPage = () => {
  paging.value.currentPage = paging.value.endPage + 1
  fetchParties()
}

const handleApprove = async (postId: number, applicantSeq: number, position: string) => {
  await communityApi.approvePartyApplicant(postId, applicantSeq, position)
  toast.success('승인되었습니다.')
  await fetchMyPartyPost()
}

const handleReject = async (postId: number, applicantSeq: number, position: string) => {
  await communityApi.rejectPartyApplicant(postId, applicantSeq, position)
  toast.success('거절되었습니다.')
  await fetchMyPartyPost()
}

const fetchParties = async () => {
  try {
    if (activeTab.value === 'current') {
      const response = await communityApi.getCurrentParty()
      currentParty.value = response.data
    } else {
      const response = await communityApi.getPartyHistory(paging.value.currentPage)
      hostedParties.value = response.data.hostedParties
      appliedParties.value = response.data.appliedParties
      paging.value = response.data.paging
    }
  } catch (error) {
    toast.error('데이터를 불러오는 중 오류가 발생했습니다.')
  }
}

const fetchMyPartyPost = async () => {
  const response = await communityApi.getMyPartyPost();
  myActivePost.value = response.data
}

onMounted(() => {
  if (route.query.tab) {
    activeTab.value = route.query.tab as string
  }
  if (route.query.page) {
    paging.value.currentPage = parseInt(route.query.page as string)
  }

  fetchParties()

  watch(
      () => route.query,
      (query) => {
        if (query.tab) {
          activeTab.value = query.tab as string
        }
        if (query.page) {
          paging.value.currentPage = parseInt(query.page as string)
        }
        fetchParties()
      }
  )
})

onMounted(async () => {
  await fetchMyPartyPost();
})
</script>

<style scoped>
.highlight-party {
  background-color: rgba(41, 121, 255, 0.1);
  border-color: #2979FF;
  box-shadow: 0 0 0 1px #2979FF;
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

.party-name:hover {
  cursor: pointer;
  color: #2979FF;
  text-decoration: underline;
}
</style>