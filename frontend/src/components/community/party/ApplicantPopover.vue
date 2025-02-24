<!-- ApplicantPopover.vue -->
<template>
  <div
      v-if="showPopover"
      ref="popoverRef"
      class="absolute top-full left-0 mt-2 w-[300px] bg-[#1A1A1A] rounded-lg shadow-lg border border-gray-700 z-10"
  >
    <div v-if="applicants && applicants.length > 0">
      <div
          v-for="applicant in applicants"
          :key="applicant.applicantSeq"
          class="p-3 border-b border-gray-700 last:border-0"
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
                @click="handleApprove(applicant)"
                class="bg-[#2979FF] hover:bg-[#2565D1] text-white p-1.5 rounded"
            >
              <UserCheck class="w-4 h-4" />
            </button>
            <button
                @click="handleReject(applicant)"
                class="bg-[#FF5252] hover:bg-[#D32F2F] text-white p-1.5 rounded"
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
import { UserCheck, UserX } from 'lucide-vue-next'
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

// 라이프사이클 훅
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// showPopover 변경 감지
watch(() => props.showPopover, (newVal) => {
  if (newVal) {
    // 팝오버가 열리면 이벤트 리스너 추가
    document.addEventListener('click', handleClickOutside)
  } else {
    // 팝오버가 닫히면 이벤트 리스너 제거
    document.removeEventListener('click', handleClickOutside)
  }
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
</style>