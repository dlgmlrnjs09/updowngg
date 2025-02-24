<template>
  <div class="min-h-screen bg-[#0A0A0A] p-6">
    <div class="max-w-[1000px] mx-auto">
      <!-- Header -->
      <div class="flex justify-between items-center mb-8">
        <h1 class="text-2xl font-bold text-white">파티 내역</h1>
        <div class="flex overflow-x-auto space-x-2 scrollbar-hide">
          <button
              @click="setActiveHistoryTab('hosted')"
              class="flex-shrink-0 px-3 py-2 rounded-lg text-sm transition-colors flex items-center gap-1"
              :class="[
              activeHistoryTab === 'hosted'
                ? 'bg-[#2979FF] text-white'
                : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
            ]"
          >
            <Users class="w-4 h-4" />
            모집
          </button>
          <button
              @click="setActiveHistoryTab('participated')"
              class="flex-shrink-0 px-3 py-2 rounded-lg text-sm transition-colors flex items-center gap-1"
              :class="[
              activeHistoryTab === 'participated'
                ? 'bg-[#2979FF] text-white'
                : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
            ]"
          >
            <History class="w-4 h-4" />
            참가
          </button>
          <button
              @click="setActiveHistoryTab('applied')"
              class="flex-shrink-0 px-3 py-2 rounded-lg text-sm transition-colors flex items-center gap-1"
              :class="[
              activeHistoryTab === 'applied'
                ? 'bg-[#2979FF] text-white'
                : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
            ]"
          >
            <Clock class="w-4 h-4" />
            신청
          </button>
        </div>
      </div>

      <!-- 모집했던 파티 -->
      <div class="space-y-4 min-h-[600px]">
        <template v-if="getCurrentHistoryList?.length > 0">
          <div v-for="party in getCurrentHistoryList"
               :key="party.postId"
               class="bg-[#141414] rounded-xl border border-[#ffffff1a] p-4"
          >
            <div class="flex justify-between items-start mb-4">
              <div class="flex flex-col">
                <span class="text-sm text-gray-400">{{ formatTimeAgo(party.regDt) }}</span>
                <div class="flex items-center gap-2 mt-1">
                  <span class="text-white font-medium">{{ getGameModeName(party.gameMode) }}</span>
                  <span :class="getStatusColor(party.postStatus)" class="text-sm">
                    {{ getPostStatusName(party.postStatus) }}
                  </span>
                </div>
              </div>
              <div class="flex items-center gap-2">
                <div class="flex items-center gap-1">
                  <Users class="w-4 h-4 text-gray-400" />
                  <span class="text-gray-400 text-sm">{{ party.participantCount }}/{{party.recruitCount}}</span>
                </div>
                <div class="flex items-center gap-1 cursor-pointer text-gray-400 hover:text-gray-300"
                     @click="togglePartyDetails(party.postId)">
                  <component
                      :is="expandedParties.has(party.postId) ? ChevronUp : ChevronDown"
                      class="w-4 h-4"
                  />
                </div>
              </div>
            </div>
            <div class="bg-[#1A1A1A] rounded-lg p-3 mb-3">
              <p class="text-white text-sm leading-relaxed">{{ party.content }}</p>
            </div>
            <!-- 펼쳐졌을 때 보이는 포지션별 참가자 정보 -->
            <div v-if="expandedParties.has(party.postId)"
                 class="mt-3 space-y-2">
              <div v-for="participant in party.participantList"
                   :key="participant.position"
                   class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <img :src="getPositionImage(participant.position)" :alt="participant.position" class="w-5 h-5"/>
                  <span class="text-gray-400 text-sm">{{ participant.position }}</span>
                </div>
                <div v-if="participant.summonerInfoDto" class="flex items-center gap-1">
                  <span class="text-white text-sm">
                    {{ participant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                  </span>
                  <span class="text-gray-400 text-xs">
                    #{{ participant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
                  </span>
                </div>
                <div v-else class="text-gray-500 text-sm">-</div>
              </div>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="flex-1 flex flex-col items-center justify-center">
            <img class="w-28 h-28 text-gray-600 mb-4" src="@/assets/icon/emoji/crying_bee.png" alt="crying_bee">
            <h3 class="text-lg font-medium text-gray-200 mb-2">
              {{ getNoDataMessage() }}
            </h3>
            <p class="text-gray-400 text-sm">아직 파티 히스토리가 없습니다.</p>
          </div>
        </template>
      </div>

      <!-- Pagination -->
      <div
          v-if="getCurrentHistoryList?.length > 0"
          class="flex justify-center mt-6 gap-2"
      >
        <button
            v-if="currentPaging.hasPrevious"
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
            currentPaging.currentPage === page
              ? 'bg-[#2979FF] text-white'
              : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
          ]"
        >
          {{ page }}
        </button>
        <button
            v-if="currentPaging.hasNext"
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
  ChevronDown, ChevronUp, Users, History, Clock
} from 'lucide-vue-next'
import {createInitialPaging, formatTimeAgo, getPageNumbers} from '@/utils/common'
import {useImageUrl} from '@/utils/imageUtil'
import {communityApi} from '@/api/community'
import {
  type PartyCommunityHistoryDto,
  type PartyCommunityHistoryResponse,
  type PartyCommunityAppliedHistoryDto
} from "@/types/community.ts";

const {getPositionImage} = useImageUrl()
const route = useRoute()
const router = useRouter()
const activeHistoryTab = ref('hosted')
const expandedParties = ref(new Set<number>())
const ITEMS_PER_PAGE = 10;

const hostedPartyHistory = ref<PartyCommunityHistoryDto[] | null>(null)
const participatedPartyHistory = ref<PartyCommunityHistoryDto[] | null>(null)
const appliedPartyHistory = ref<PartyCommunityAppliedHistoryDto[] | null>(null)
const hostedPaging = ref<PartyCommunityHistoryResponse>(createInitialPaging());
const participatedPaging = ref<PartyCommunityHistoryResponse>(createInitialPaging());
const appliedPaging = ref<PartyCommunityHistoryResponse>(createInitialPaging());
const pageNumbers = computed(() => getPageNumbers(currentPaging.value));

const currentPaging = computed(() => {
  switch (activeHistoryTab.value) {
    case 'hosted':
      return hostedPaging.value;
    case 'participated':
      return participatedPaging.value;
    case 'applied':
      return appliedPaging.value;
    default:
      return hostedPaging.value;
  }
});

const getCurrentHistoryList = computed(() => {
  switch (activeHistoryTab.value) {
    case 'hosted':
      return hostedPartyHistory.value;
    case 'participated':
      return participatedPartyHistory.value;
    case 'applied':
      return appliedPartyHistory.value;
    default:
      return hostedPartyHistory.value;
  }
});

const getNoDataMessage = () => {
  switch (activeHistoryTab.value) {
    case 'hosted':
      return '모집했던 파티가 없습니다';
    case 'participated':
      return '참가했던 파티가 없습니다';
    case 'applied':
      return '신청했던 파티가 없습니다';
    default:
      return '파티 히스토리가 없습니다';
  }
};

const setActiveHistoryTab = async (tab: string) => {
  activeHistoryTab.value = tab
  await router.push({
    query: {...route.query, historyTab: tab, page: '1'}
  })
  await fetchHistoryData(tab)
}

const togglePartyDetails = (partyId: number) => {
  if (expandedParties.value.has(partyId)) {
    expandedParties.value.delete(partyId)
  } else {
    expandedParties.value.add(partyId)
  }
}

const getStatusColor = (status: string) => {
  const colors = {
    'OPEN': 'text-[#4CAF50]',
    'CLOSE': 'text-[#2979FF]',
    'CANCEL': 'text-[#FF5252]',
  }
  return colors[status] || 'text-gray-400'
}

const getPostStatusName = (postStatus: string) => {
  if (postStatus === 'OPEN') {
    return '대기중'
  } else if (postStatus === 'CLOSE') {
    return '마감'
  } else if (postStatus === 'CANCEL') {
    return '취소'
  } else {
    return ''
  }
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

const handlePageChange = async (page: number) => {
  await router.push({
    query: {...route.query, page: page.toString()}
  });
}

const handlePrevPage = () => {
  currentPaging.value.currentPage = currentPaging.value.startPage - 1;
  fetchHistoryData(activeHistoryTab.value);
}

const handleNextPage = () => {
  currentPaging.value.currentPage = currentPaging.value.endPage + 1;
  fetchHistoryData(activeHistoryTab.value);
}

const fetchHistoryData = async (type: string) => {
  const page = currentPaging.value.currentPage;
  switch (type) {
    case 'hosted': {
      const hostedResponse = await communityApi.getPartyHostedHistory(page, ITEMS_PER_PAGE)
      hostedPartyHistory.value = hostedResponse.data.items
      hostedPaging.value = hostedResponse.data
      break
    }
    case 'participated': {
      const participatedResponse = await communityApi.getPartyParticipatedHistory(page, ITEMS_PER_PAGE)
      participatedPartyHistory.value = participatedResponse.data.items
      participatedPaging.value = participatedResponse.data
      break
    }
    case 'applied': {
      const appliedResponse = await communityApi.getPartyAppliedHistory(page, ITEMS_PER_PAGE)
      appliedPartyHistory.value = appliedResponse.data.items
      appliedPaging.value = appliedResponse.data
      break
    }
  }
}

onMounted(async () => {
  // Set default to hosted history tab
  if (route.query.historyTab) {
    activeHistoryTab.value = route.query.historyTab as string
  }

  if (route.query.page) {
    currentPaging.value.currentPage = parseInt(route.query.page as string)
  }

  // Fetch initial data
  await fetchHistoryData(activeHistoryTab.value)

  // Watch for route changes
  watch(
      () => route.query,
      async (query) => {
        if (query.historyTab) {
          activeHistoryTab.value = query.historyTab as string
        }
        if (query.page) {
          currentPaging.value.currentPage = parseInt(query.page as string)
        }
        await fetchHistoryData(activeHistoryTab.value)
      }
  )
})
</script>

<style scoped>
/* 파티 하이라이트 효과 */
.highlight-party {
  background-color: rgba(41, 121, 255, 0.1);
  border-color: #2979FF;
  box-shadow: 0 0 0 1px #2979FF;
}

/* 페이드 아웃 애니메이션 */
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

/* 소환사 이름 호버 효과 */
.party-name:hover {
  cursor: pointer;
  color: #2979FF;
  text-decoration: underline;
}

/* 포지션 아이콘 효과 */
.position-icon {
  transition: transform 0.2s ease;
}

.position-icon:hover {
  transform: scale(1.1);
}

/* 탭 버튼 트랜지션 효과 */
.tab-button {
  transition: all 0.3s ease;
}

.tab-button:hover {
  opacity: 0.8;
}

/* 파티 카드 호버 효과 */
.party-card {
  transition: all 0.3s ease;
}

.party-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* 확장/축소 애니메이션 */
.expand-enter-active,
.expand-leave-active {
  transition: max-height 0.3s ease, opacity 0.3s ease;
  max-height: 1000px;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  max-height: 0;
  opacity: 0;
}

/* 상태 색상 */
.status-open {
  color: #4CAF50;
}

.status-close {
  color: #2979FF;
}

.status-cancel {
  color: #FF5252;
}

/* 참가자 정보 호버 효과 */
.participant-info {
  transition: background-color 0.2s ease;
}

.participant-info:hover {
  background-color: rgba(41, 121, 255, 0.05);
}

/* 페이지네이션 버튼 효과 */
.pagination-button {
  transition: all 0.2s ease;
}

.pagination-button:hover {
  background-color: rgba(41, 121, 255, 0.1);
}

/* 스크롤바 숨기기 (테일윈드 커스텀) */
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

.scrollbar-hide {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}

/* 빈 상태 일러스트레이션 애니메이션 */
@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.empty-state-illustration {
  animation: bounce 2s infinite;
}
</style>