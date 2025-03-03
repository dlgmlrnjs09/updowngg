<!-- ApplicantPopover.vue -->
<template>
  <div
      v-if="showPopover"
      ref="popoverRef"
      class="absolute top-full left-0 mt-2 w-[450px] bg-[#1A1A1A] rounded-lg shadow-lg border border-gray-700 z-50"
  >
    <div v-if="applicants && applicants.length > 0">
      <div
          v-for="applicant in applicants"
          :key="applicant.applicantSeq"
          class="p-3 border-b border-gray-700 last:border-0"
      >
        <div class="flex items-center w-full">
          <!-- 왼쪽: 소환사 기본 정보 -->
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-1">
              <div class="text-white text-sm hover:text-[#2979FF] truncate">
                {{ applicant.summonerInfoDto.summonerBasicInfoDto.gameName }}
              </div>
              <div class="text-gray-400 text-xs">
                #{{ applicant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
              </div>
            </div>
            <div class="flex gap-1 mt-0.5">
              <span
                  v-for="tag in applicant.summonerInfoDto.frequentTagDtoList"
                  :key="tag.tagCode"
                  class="bg-[#2979FF]/10 text-[#2979FF] text-[9px] px-1 py-0.5 rounded"
              >
                {{ tag.tagValue }}
              </span>
            </div>
          </div>

          <!-- 중앙: 평가 점수 -->
          <div class="flex flex-col items-end mr-4">
            <div class="flex items-center gap-1 text-[10px] mb-0.5">
              <span class="text-gray-400">평가</span>
              <span class="text-[#2979FF] font-medium">
                {{ applicant.summonerInfoDto.reviewStatsDto?.score?.toFixed(1) ?? 0 }}점
              </span>
            </div>
            <div class="flex items-center gap-2">
              <div class="flex items-center gap-1">
                <ThumbsUp class="w-3 h-3 text-[#4CAF50]" />
                <span class="text-[#4CAF50] text-[10px]">
                  {{ applicant.summonerInfoDto.reviewStatsDto?.upCount ?? 0 }}
                </span>
              </div>
              <div class="flex items-center gap-1">
                <ThumbsDown class="w-3 h-3 text-[#FF5252]" />
                <span class="text-[#FF5252] text-[10px]">
                  {{ applicant.summonerInfoDto.reviewStatsDto?.downCount ?? 0 }}
                </span>
              </div>
            </div>
          </div>

          <!-- 모스트 챔피언 정보 -->
          <div class="flex gap-1 mr-4">
            <div
                v-for="(champion, index) in applicant.summonerInfoDto.mostChampionDto?.slice(0, 2)"
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

          <!-- 승인/거절 버튼 -->
          <div class="flex items-center gap-1">
            <button
                @click="handleApprove(applicant)"
                class="bg-[#2979FF] hover:bg-[#2565D1] text-white p-1.5 rounded"
                title="승인"
            >
              <UserCheck class="w-4 h-4" />
            </button>
            <button
                @click="handleReject(applicant)"
                class="bg-[#FF5252] hover:bg-[#D32F2F] text-white p-1.5 rounded"
                title="거절"
            >
              <UserX class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="p-4 text-center text-gray-400 text-sm">
      신청자가 없습니다
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { UserCheck, UserX, ThumbsUp, ThumbsDown } from 'lucide-vue-next'
import type { PartyCommunityApplicantDetailDto } from "@/types/community"

interface Props {
  showPopover: boolean
  applicants: PartyCommunityApplicantDetailDto[]
  position: string
  postId: number
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'approve', postId: number, applicantPuuid: string, applicantSeq: number, position: string): void
  (e: 'reject', postId: number, applicantPuuid: string, applicantSeq: number, position: string): void
  (e: 'close'): void
}>()

const popoverRef = ref<HTMLElement | null>(null)

// Methods
const handleClickOutside = (event: MouseEvent) => {
  // 팝오버 외부 클릭 시 닫기
  if (
      popoverRef.value &&
      !popoverRef.value.contains(event.target as Node) &&
      !(event.target as HTMLElement).closest('.applicant-button')
  ) {
    emit('close')
  }
}

const handleApprove = (applicant: PartyCommunityApplicantDetailDto) => {
  emit(
      'approve',
      props.postId,
      applicant.summonerInfoDto.summonerBasicInfoDto.puuid,
      applicant.applicantSeq,
      props.position
  )
}

const handleReject = (applicant: PartyCommunityApplicantDetailDto) => {
  emit(
      'reject',
      props.postId,
      applicant.summonerInfoDto.summonerBasicInfoDto.puuid,
      applicant.applicantSeq,
      props.position
  )
}

// showPopover 변경 감지
watch(() => props.showPopover, (newVal) => {
  if (newVal) {
    // 팝오버가 열리면 이벤트 리스너 추가 (약간의 지연을 두어 클릭 이벤트와 충돌 방지)
    setTimeout(() => {
      document.addEventListener('click', handleClickOutside)
    }, 100)
  } else {
    // 팝오버가 닫히면 이벤트 리스너 제거
    document.removeEventListener('click', handleClickOutside)
  }
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.applicant-popover-enter-active,
.applicant-popover-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.applicant-popover-enter-from,
.applicant-popover-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>