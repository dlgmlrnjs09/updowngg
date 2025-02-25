<!-- MyActiveParty.vue -->
<template>
  <div
      v-if="party"
      class="max-w-6xl mx-auto mb-8"
      :class="{ 'highlight-effect': hasNewApplicant }"
  >
    <div class="bg-[#141414] rounded-xl border border-[#2979FF] p-4">
      <!-- PC/태블릿 뷰 -->
      <div class="hidden sm:flex items-center justify-between">
        <!-- 좌측: 기본 정보 -->
        <div class="flex items-center gap-4">
          <div class="flex items-center gap-2">
            <span class="text-[#2979FF] text-sm font-medium">
              {{ party.postCardDto.writerPuuid === myPuuid ? '모집중' : '참가중' }}
            </span>
            <span class="text-white text-sm">
              {{ getGameModeName(party.postCardDto.gameMode) }}
            </span>
          </div>
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
          <!-- 포지션 정보 (PC) -->
          <div class="flex items-center gap-2">
            <template v-for="participant in party.postCardDto.participantDtoList" :key="participant.position">
              <div v-if="participant.isOpenPosition" class="relative">
                <MyActivePartyParticipant
                    :participant="participant"
                    :is-writer="party.postCardDto.writerPuuid === participant.summonerInfoDto?.summonerBasicInfoDto?.puuid"
                    :position="participant.position"
                    :is-applicants-visible="showApplicants[participant.position]"
                    :applicants-count="party.applicantByPositionMap?.[participant.position]?.length || 0"
                    @toggle-applicants="toggleApplicants(participant.position, $event)"
                />

                <!-- 신청자 목록 팝오버 -->
                <ApplicantPopover
                    :show-popover="showApplicants[participant.position]"
                    :applicants="party.applicantByPositionMap?.[participant.position] || []"
                    :position="participant.position"
                    :post-id="party.postCardDto.postId"
                    @approve="handleApprove"
                    @reject="handleReject"
                    @close="showApplicants[participant.position] = false"
                />
              </div>
            </template>
          </div>
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

      <!-- 모바일 뷰 -->
      <div class="sm:hidden space-y-4">
        <!-- 상태 및 기본 정보 -->
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-[#2979FF] text-sm font-medium">
              {{ party.postCardDto.writerPuuid === myPuuid ? '모집중' : '참가중' }}
            </span>
            <span class="text-white text-sm">{{ getGameModeName(party.postCardDto.gameMode) }}</span>
          </div>
          <component
              :is="party.postCardDto.isUseMic ? MicIcon : MicOffIcon"
              :class="party.postCardDto.isUseMic ? 'text-[#2979FF]' : 'text-gray-500'"
              class="w-4 h-4"
          />
        </div>

        <!-- 포지션 정보 (모바일) -->
        <div class="grid grid-cols-1 gap-2">
          <MyActivePartyParticipant
              v-for="participant in party.postCardDto.participantDtoList"
              :key="participant.position"
              :participant="participant"
              :is-writer="party.postCardDto.writerPuuid === participant.summonerInfoDto?.summonerBasicInfoDto?.puuid"
              :position="participant.position"
              :is-applicants-visible="showApplicants[participant.position]"
              :applicants-count="party.applicantByPositionMap?.[participant.position]?.length || 0"
              :is-mobile="true"
              @toggle-applicants="toggleApplicants(participant.position)"
          />
        </div>

        <!-- 신청자 목록 (모바일용) -->
        <div v-for="(applicants, position) in party.applicantByPositionMap" :key="position">
          <div v-if="true" class="space-y-2">
            <div
                v-for="applicant in applicants"
                :key="applicant.applicantSeq"
                class="bg-[#141414] rounded-lg p-3"
            >
              <div class="flex items-center gap-3">
                <img
                    :src="applicant.summonerInfoDto.summonerBasicInfoDto.profileIconUrl"
                    :alt="applicant.summonerInfoDto.summonerBasicInfoDto.gameName"
                    class="w-10 h-10 rounded-lg"
                />
                <div class="flex-1">
                  <div class="flex items-center gap-1">
                    <span class="text-white text-sm">
                      {{ applicant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                    </span>
                    <span class="text-gray-400 text-xs">
                      #{{ applicant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
                    </span>
                  </div>
                  <div class="flex gap-1 mt-1">
                    <span
                        v-for="tag in applicant.summonerInfoDto.frequentTagDtoList"
                        :key="tag.tagCode"
                        class="bg-[#2979FF]/10 text-[#2979FF] text-[9px] px-1 py-0.5 rounded"
                    >
                      {{ tag.tagValue }}
                    </span>
                  </div>
                </div>
                <div class="flex items-center gap-1">
                  <button
                      @click="handleApprove(party.postCardDto.postId, applicant.summonerInfoDto.summonerBasicInfoDto.puuid, applicant.applicantSeq, position)"
                      class="bg-[#2979FF] hover:bg-[#2565D1] text-white p-1.5 rounded"
                  >
                    <UserCheck class="w-4 h-4" />
                  </button>
                  <button
                      @click="handleReject(party.postCardDto.postId, applicant.summonerInfoDto.summonerBasicInfoDto.puuid, applicant.applicantSeq, position)"
                      class="bg-[#FF5252] hover:bg-[#D32F2F] text-white p-1.5 rounded"
                  >
                    <UserX class="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 액션 버튼 -->
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { MicIcon, MicOffIcon, Users, ChevronUp, ChevronDown, UserCheck, UserX } from 'lucide-vue-next'
import { useImageUrl } from "@/utils/imageUtil"
import type { MyPartyPostDto } from "@/types/community"
import ApplicantPopover from './ApplicantPopover.vue'
import MyActivePartyParticipant from "@/components/community/party/MyActivePartyParticipant.vue";

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
}>()

// Reactive states
const showApplicants = ref<{[key: string]: boolean}>({
  'TOP': false,
  'JG': false,
  'MID': false,
  'AD': false,
  'SUP': false
})

// Methods
const toggleApplicants = (position: string, event?: MouseEvent) => {
  if (event) {
    event.stopPropagation()
  }
  Object.keys(showApplicants.value).forEach(key => {
    if (key !== position) {
      showApplicants.value[key] = false
    }
  })
  showApplicants.value[position] = !showApplicants.value[position]
}

const handleUpdateStatus = (status: string) => {
  emit('update-status', props.party.postCardDto.postId, status)
}

const handleApprove = (postId: number, applicantPuuid: string, applicantSeq: number, position: string) => {
  emit('approve-applicant', postId, applicantPuuid, applicantSeq, position)
}

const handleReject = (postId: number, applicantPuuid: string, applicantSeq: number, position: string) => {
  emit('reject-applicant', postId, applicantPuuid, applicantSeq, position)
}

const handleLeave = () => {
  emit('leave-party', props.party.postCardDto.postId)
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