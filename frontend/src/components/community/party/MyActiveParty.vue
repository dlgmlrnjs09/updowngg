<!-- MyActiveParty.vue 수정 -->
<template>
  <div
      v-if="party"
      class="max-w-6xl mx-auto mb-8"
      :class="{ 'highlight-effect': hasNewApplicant }"
  >
    <div class="bg-[#141414] rounded-xl border border-[#2979FF] p-4">
      <!-- PC/태블릿 뷰 -->
      <div class="hidden sm:flex flex-col gap-4">
        <!-- 첫 번째 줄: 기본 정보 및 액션 버튼 -->
        <div class="flex items-center justify-between">
          <!-- 좌측: 기본 정보 -->
          <div class="flex items-center gap-4">
            <div class="flex items-center gap-4">
              <span class="text-[#2979FF] text-sm font-medium">
                {{ party.postCardDto.writerPuuid === myPuuid ? '모집중' : '참가중' }}
              </span>
              <div class="h-4 w-[1px] bg-gray-700"></div>
              <div
                  class="flex items-center gap-2"
                  :class="{ 'participant-highlight': participantCount === party.postCardDto.participantCount }"
              >
                <Users class="w-4 h-4 text-gray-400" />
                <span class="text-white text-sm">
                {{ party.postCardDto.participantCount }} / {{ party.postCardDto.recruitCount }}
              </span>
              </div>
              <div class="h-4 w-[1px] bg-gray-700"></div>
              <component
                  :is="party.postCardDto.isUseMic ? MicIcon : MicOffIcon"
                  :class="party.postCardDto.isUseMic ? 'text-[#2979FF]' : 'text-gray-500'"
                  class="w-4 h-4"
              />
              <div class="h-4 w-[1px] bg-gray-700"></div>
              <span class="text-white text-sm">
                {{ getGameModeName(party.postCardDto.gameMode) }}
              </span>
            </div>
            <!-- 티어 범위 정보 추가 -->
            <template v-if="isRankedMode && hasTierRange">
              <div class="h-4 w-[1px] bg-gray-700"></div>
              <div class="flex items-center gap-2">
                <div class="flex items-center gap-1">
                  <img v-if="party.postCardDto.tierMin && party.postCardDto.tierMin !== '전체'" :src="getTierImage(party.postCardDto.tierMin)" :alt="party.postCardDto.tierMin" class="w-4 h-4">
                  <span class="text-white text-sm">{{ getTierRangeDisplay() }}</span>
                  <img v-if="party.postCardDto.tierMax && party.postCardDto.tierMax !== '전체' && party.postCardDto.tierMax !== party.postCardDto.tierMin" :src="getTierImage(party.postCardDto.tierMax)" :alt="party.postCardDto.tierMax" class="w-4 h-4">
                </div>
              </div>
            </template>
          </div>

          <!-- 우측: 액션 버튼 -->
          <div class="flex items-center gap-2">
            <template v-if="party.postCardDto.writerPuuid === myPuuid">
              <button
                  @click="handleUpdateStatus('CLOSE')"
                  class="bg-[#1A1A1A] hover:bg-[#242424] text-white px-3 py-1.5 rounded-lg text-xs"
              >
                마감
              </button>
              <button
                  @click="handleUpdateStatus('CANCEL')"
                  class="bg-[#1A1A1A] hover:bg-[#242424] text-[#FF5252] px-3 py-1.5 rounded-lg text-xs"
              >
                취소
              </button>
            </template>
            <template v-else>
              <button
                  @click="handleLeave"
                  class="bg-[#1A1A1A] hover:bg-[#242424] text-[#FF5252] px-3 py-1.5 rounded-lg text-xs"
              >
                나가기
              </button>
            </template>
          </div>
        </div>

        <!-- 구분선 추가 -->
        <div class="h-[1px] w-full bg-gray-700"></div>

        <!-- 두 번째 줄: 포지션별 참가자 정보 -->
        <div class="flex items-center gap-3">
          <template v-for="participant in party.postCardDto.participantDtoList" :key="participant.position">
            <div v-if="participant.isOpenPosition" class="relative">
              <MyActivePartyParticipant
                  :participant="participant"
                  :is-writer="party.postCardDto.writerPuuid === participant.summonerInfoDto?.summonerBasicInfoDto?.puuid"
                  :position="participant.position"
                  :is-applicants-visible="activePosition === participant.position"
                  :applicants-count="party.applicantByPositionMap?.[participant.position]?.length || 0"
                  :writer-puuid="party.postCardDto.writerPuuid"
                  :post-id="party.postCardDto.postId"
                  @toggle-applicants="toggleApplicants"
                  @kick-member="handleKickMember"
              />
            </div>
          </template>
        </div>
      </div>

      <!-- 모바일 뷰 -->
      <div class="sm:hidden space-y-4">
        <!-- 첫 번째 줄: 상태 및 기본 정보 -->
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-[#2979FF] text-sm font-medium">
              {{ party.postCardDto.writerPuuid === myPuuid ? '모집중' : '참가중' }}
            </span>
            <span class="text-white text-sm">{{ getGameModeName(party.postCardDto.gameMode) }}</span>
          </div>
          <div class="flex items-center gap-2">
            <div
                class="flex items-center gap-1"
                :class="{ 'participant-highlight': participantCount === party.postCardDto.participantCount }"
            >
              <Users class="w-4 h-4 text-gray-400" />
              <span class="text-white text-sm">
                {{ party.postCardDto.participantCount }} / {{ party.postCardDto.recruitCount }}
              </span>
            </div>
            <component
                :is="party.postCardDto.isUseMic ? MicIcon : MicOffIcon"
                :class="party.postCardDto.isUseMic ? 'text-[#2979FF]' : 'text-gray-500'"
                class="w-4 h-4"
            />
          </div>
        </div>

        <!-- 구분선 추가 -->
        <div class="h-[1px] w-full bg-gray-700"></div>

        <!-- 두 번째 줄: 포지션 정보 -->
        <div class="grid grid-cols-1 gap-2">
          <MyActivePartyParticipant
              v-for="participant in party.postCardDto.participantDtoList"
              :key="participant.position"
              :participant="participant"
              :is-writer="party.postCardDto.writerPuuid === participant.summonerInfoDto?.summonerBasicInfoDto?.puuid"
              :position="participant.position"
              :is-applicants-visible="activePosition === participant.position"
              :applicants-count="party.applicantByPositionMap?.[participant.position]?.length || 0"
              :is-mobile="true"
              :writer-puuid="party.postCardDto.writerPuuid"
              :post-id="party.postCardDto.postId"
              @toggle-applicants="toggleApplicants"
              @kick-member="handleKickMember"
          />
        </div>

        <!-- 세 번째 줄: 액션 버튼 -->
        <div class="flex justify-end gap-2">
          <template v-if="party.postCardDto.writerPuuid === myPuuid">
            <button
                @click="handleUpdateStatus('CLOSE')"
                class="bg-[#1A1A1A] hover:bg-[#242424] text-white px-3 py-1.5 rounded-lg text-xs"
            >
              마감
            </button>
            <button
                @click="handleUpdateStatus('CANCEL')"
                class="bg-[#1A1A1A] hover:bg-[#242424] text-[#FF5252] px-3 py-1.5 rounded-lg text-xs"
            >
              취소
            </button>
          </template>
          <template v-else>
            <button
                @click="handleLeave"
                class="bg-[#1A1A1A] hover:bg-[#242424] text-[#FF5252] px-3 py-1.5 rounded-lg text-xs"
            >
              나가기
            </button>
          </template>
        </div>
      </div>
    </div>

    <!-- 신청자 모달 -->
    <ApplicantModal
        :show="!!activePosition"
        :applicants="activePosition ? (party.applicantByPositionMap?.[activePosition] || []) : []"
        :position="activePosition || ''"
        :post-id="party.postCardDto.postId"
        @approve="handleApprove"
        @reject="handleReject"
        @close="closeApplicantModal"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { MicIcon, MicOffIcon, Users, ChevronUp, ChevronDown, UserCheck, UserX } from 'lucide-vue-next'
import { useImageUrl } from "@/utils/imageUtil"
import type { MyPartyPostDto } from "@/types/community"
import MyActivePartyParticipant from "@/components/community/party/MyActivePartyParticipant.vue"
import ApplicantModal from "@/components/community/party/ApplicantModal.vue"
const {getTierImage } = useImageUrl()

const { getPositionImage } = useImageUrl()

// Props
interface Props {
  party: MyPartyPostDto
  myPuuid: string
  hasNewApplicant?: boolean
  participantCount?: number | null
}

const props = withDefaults(defineProps<Props>(), {
  hasNewApplicant: false,
  participantCount: null
})

// Emits
const emit = defineEmits<{
  (e: 'update-status', postId: number, status: string): void
  (e: 'approve-applicant', postId: number, applicantPuuid: string, applicantSeq: number, position: string): void
  (e: 'reject-applicant', postId: number, applicantPuuid: string, applicantSeq: number, position: string): void
  (e: 'leave-party', postId: number): void
  (e: 'kick-member', postId: number, memberPuuid: string): void
}>()

// Reactive states
const activePosition = ref<string | null>(null)

// 티어 범위 관련 computed
const isRankedMode = computed(() => {
  const gameMode = props.party.postCardDto.gameMode;
  return gameMode === 'SOLO_RANK' || gameMode === 'FLEX_RANK';
});

const hasTierRange = computed(() => {
  const tierMin = props.party.postCardDto.tierMin;
  const tierMax = props.party.postCardDto.tierMax;
  return (tierMin && tierMin !== '전체') || (tierMax && tierMax !== '전체');
});

const getTierRangeDisplay = () => {
  const tierMin = props.party.postCardDto.tierMin;
  const tierMax = props.party.postCardDto.tierMax;

  if (!tierMin && !tierMax) return '전체 티어';
  if (tierMin === '전체' && tierMax === '전체') return '전체 티어';

  if (tierMin === '전체' || !tierMin) {
    return `${getTierName(tierMax!)} 이하`;
  }

  if (tierMax === '전체' || !tierMax) {
    return `${getTierName(tierMin)} 이상`;
  }

  return `${getTierName(tierMin)} ~ ${getTierName(tierMax)}`;
};

const getTierName = (tierCode: string) => {
  const tierMap: {[key: string]: string} = {
    'IRON': '아이언',
    'BRONZE': '브론즈',
    'SILVER': '실버',
    'GOLD': '골드',
    'PLATINUM': '플래티넘',
    'EMERALD': '에메랄드',
    'DIAMOND': '다이아몬드',
    'MASTER': '마스터',
    'GRANDMASTER': '그랜드마스터',
    'CHALLENGER': '챌린저',
  };
  return tierMap[tierCode] || tierCode;
};

// Methods
const toggleApplicants = (position: string) => {
  if (activePosition.value === position) {
    activePosition.value = null
  } else {
    activePosition.value = position
  }
}

const closeApplicantModal = () => {
  activePosition.value = null
}

const handleUpdateStatus = (status: string) => {
  emit('update-status', props.party.postCardDto.postId, status)
}

const handleApprove = (postId: number, applicantPuuid: string, applicantSeq: number, position: string) => {
  emit('approve-applicant', postId, applicantPuuid, applicantSeq, position)
  // 승인 후 모달 닫기
  closeApplicantModal()
}

const handleReject = (postId: number, applicantPuuid: string, applicantSeq: number, position: string) => {
  emit('reject-applicant', postId, applicantPuuid, applicantSeq, position)
  // 마지막 신청자를 거절한 경우, 모달을 자동으로 닫지 않고 남겨두기
  // (사용자가 직접 모달을 닫을 수 있게 함)
}

const handleLeave = () => {
  emit('leave-party', props.party.postCardDto.postId)
}

const handleKickMember = (postId: number, memberPuuid: string) => {
  emit('kick-member', postId, memberPuuid)
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
.highlight-effect {
  animation: highlight 2s ease-out;
}

.participant-highlight {
  animation: highlight 2s ease-out;
}

@keyframes highlight {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(41, 121, 255, 0.4);
  }
  50% {
    transform: scale(1.02);
    box-shadow: 0 0 0 10px rgba(41, 121, 255, 0);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(41, 121, 255, 0);
  }
}

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>