<!-- ApplicantModal.vue -->
<template>
  <Teleport to="body">
    <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-80" @click="handleClickOutside">
      <div
          class="w-full max-w-[90%] sm:max-w-md md:max-w-[38rem] lg:max-w-[40rem] bg-[#141414] rounded-lg overflow-hidden animate-fade-in border border-gray-800"
          ref="modalRef"
          @click.stop
      >
        <!-- 모달 헤더 -->
        <div class="bg-[#1A1A1A] px-3 sm:px-4 py-2 sm:py-3 flex justify-between items-center border-b border-gray-800">
          <div class="flex items-center gap-2">
            <img :src="getPositionImage(position)" :alt="position" class="w-4 h-4 sm:w-5 sm:h-5">
            <h3 class="text-white font-medium text-xs sm:text-sm flex items-center">
              {{ getPositionName(position) }} 포지션 신청자 목록
              <span class="ml-1 text-[#2979FF] text-xs">
                ({{ applicants.length }})
              </span>
            </h3>
          </div>
          <button @click="$emit('close')" class="text-gray-400 hover:text-white">
            <X class="w-4 h-4 sm:w-5 sm:h-5" />
          </button>
        </div>

        <!-- 신청자 목록 -->
        <div class="max-h-[40vh] sm:max-h-[50vh] md:max-h-[60vh] overflow-y-auto bg-[#141414]">
          <div v-if="applicants && applicants.length > 0">
            <div
                v-for="applicant in applicants"
                :key="applicant.applicantSeq"
                class="px-3 py-2 sm:px-4 sm:py-3 border-b border-gray-800 last:border-0 hover:bg-[#1A1A1A] transition-colors"
            >
              <!-- 모바일: 한 줄 레이아웃 / 태블릿 & PC: 좀 더 여유 있는 레이아웃 -->
              <div class="flex items-center w-full justify-between sm:grid sm:grid-cols-[1fr,auto,auto] sm:gap-3 md:gap-4">
                <!-- 왼쪽: 닉네임과 태그 -->
                <div class="flex-1 min-w-0 mr-2 sm:mr-0 sm:flex sm:items-center sm:gap-3">
                  <!-- 프로필 이미지 (모바일에서는 숨김, 태블릿/PC에서만 표시) -->
                  <img
                      :src="applicant.summonerInfoDto.summonerBasicInfoDto.profileIconUrl"
                      :alt="applicant.summonerInfoDto.summonerBasicInfoDto.gameName"
                      class="hidden sm:block w-8 h-8 md:w-10 md:h-10 rounded-lg"
                  />

                  <div class="flex-1 min-w-0">
                    <div class="flex items-center">
                      <span class="text-white text-xs sm:text-sm font-medium truncate max-w-[90px] sm:max-w-full">
                        {{ applicant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                      </span>
                      <span class="text-gray-400 text-[10px] sm:text-xs ml-1 truncate">
                        #{{ applicant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
                      </span>
                    </div>
                    <div class="flex gap-1 mt-0.5 flex-wrap max-w-[120px] sm:max-w-full">
                      <span
                          v-for="tag in applicant.summonerInfoDto.frequentTagDtoList.slice(0, isMobile ? 2 : 4)"
                          :key="tag.tagCode"
                          class="bg-[#2979FF]/10 text-[#2979FF] text-[8px] sm:text-[9px] md:text-xs px-1 py-0.5 rounded"
                      >
                        {{ tag.tagValue }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- 중앙: 평가 점수 -->
                <div class="flex items-center gap-2 mr-2 sm:mr-0 sm:justify-center">
                  <div class="flex flex-col items-center sm:items-center">
                    <!-- 평가 점수 (상단) -->
                    <div class="flex items-center mb-0.5 sm:mb-1">
                      <span class="text-gray-400 text-[10px] sm:text-xs mr-1">평가</span>
                      <span class="text-[#2979FF] font-medium text-[10px] sm:text-xs md:text-sm">
                        {{ applicant.summonerInfoDto.reviewStatsDto?.score?.toFixed(1) ?? 0 }}점
                      </span>
                    </div>
                    <!-- up/down 수 (하단) -->
                    <div class="flex items-center gap-2">
                      <div class="flex items-center gap-0.5 sm:gap-1">
                        <ThumbsUp class="w-2 h-2 sm:w-3 sm:h-3 md:w-4 md:h-4 text-[#4CAF50]" />
                        <span class="text-[#4CAF50] text-[8px] sm:text-xs">
                          {{ applicant.summonerInfoDto.reviewStatsDto?.upCount ?? 0 }}
                        </span>
                      </div>
                      <div class="flex items-center gap-0.5 sm:gap-1">
                        <ThumbsDown class="w-2 h-2 sm:w-3 sm:h-3 md:w-4 md:h-4 text-[#FF5252]" />
                        <span class="text-[#FF5252] text-[8px] sm:text-xs">
                          {{ applicant.summonerInfoDto.reviewStatsDto?.downCount ?? 0 }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 모스트 챔피언 ~ 버튼 컨테이너 -->
                <div class="flex items-center">
                  <!-- 모스트 챔피언 (모바일에서는 숨김, 태블릿/PC에서만 표시) -->
                  <div class="hidden sm:flex gap-1 md:gap-2 mr-2 sm:mr-4">
                    <div
                        v-for="(champion, index) in applicant.summonerInfoDto.mostChampionDto?.slice(0, 2)"
                        :key="index"
                        class="bg-[#0D0D0D] rounded-lg p-0.5 sm:p-1 flex flex-col items-center"
                    >
                      <img
                          :src="champion.iconUrl"
                          :alt="champion.nameUs"
                          class="w-4 h-4 sm:w-5 sm:h-5 md:w-6 md:h-6 rounded-full"
                      >
                      <span class="text-[7px] sm:text-[9px] text-[#4CAF50]">
                        {{ champion.winRate }}%
                      </span>
                    </div>
                  </div>

                  <!-- 오른쪽: 승인/거절 버튼 -->
                  <div class="flex gap-1 sm:gap-2">
                    <button
                        @click="handleApprove(applicant)"
                        class="bg-[#2979FF] hover:bg-[#2565D1] text-white text-[10px] sm:text-xs p-1 sm:px-2 sm:py-1.5 rounded flex items-center gap-1"
                        :title="isMobile ? '승인' : ''"
                    >
                      <UserCheck class="w-3 h-3 sm:w-4 sm:h-4" />
                      <span class="hidden sm:inline">승인</span>
                    </button>
                    <button
                        @click="handleReject(applicant)"
                        class="bg-[#FF5252] hover:bg-[#D32F2F] text-white text-[10px] sm:text-xs p-1 sm:px-2 sm:py-1.5 rounded flex items-center gap-1"
                        :title="isMobile ? '거절' : ''"
                    >
                      <UserX class="w-3 h-3 sm:w-4 sm:h-4" />
                      <span class="hidden sm:inline">거절</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="p-4 text-center text-gray-400 text-xs sm:text-sm">
            신청자가 없습니다
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, computed, onUnmounted } from 'vue'
import { X, UserCheck, UserX, ThumbsUp, ThumbsDown } from 'lucide-vue-next'
import type { PartyCommunityApplicantDetailDto } from "@/types/community"
import { useImageUrl } from "@/utils/imageUtil"

const { getPositionImage } = useImageUrl()

interface Props {
  show: boolean
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

const modalRef = ref<HTMLElement | null>(null)

// 반응형 처리를 위한 모바일 여부 감지
const isMobile = computed(() => {
  if (typeof window !== 'undefined') {
    return window.innerWidth < 640;
  }
  return false;
});

// 윈도우 크기 변경 감지
const updateWindowWidth = () => {
  // 반응형 처리를 위한 화면 크기 업데이트
  // (계산된 속성이 자동으로 업데이트됨)
};

// Methods
const handleClickOutside = (event: MouseEvent) => {
  emit('close')
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

// 포지션 이름 변환 함수
const getPositionName = (position: string) => {
  const positionMap: Record<string, string> = {
    'TOP': '탑',
    'JG': '정글',
    'MID': '미드',
    'AD': '원딜',
    'SUP': '서포터'
  }
  return positionMap[position] || position
}

// 모달 열릴 때 스크롤 방지 및 이벤트 리스너 등록
watch(() => props.show, (newVal) => {
  if (newVal) {
    document.body.style.overflow = 'hidden'
    window.addEventListener('resize', updateWindowWidth)
    nextTick(() => {
      if (modalRef.value) {
        modalRef.value.focus()
      }
    })
  } else {
    document.body.style.overflow = ''
    window.removeEventListener('resize', updateWindowWidth)
  }
}, { immediate: true })

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener('resize', updateWindowWidth)
})
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 스크롤바 스타일링 */
.max-h-\[40vh\]::-webkit-scrollbar,
.sm\:max-h-\[50vh\]::-webkit-scrollbar,
.md\:max-h-\[60vh\]::-webkit-scrollbar {
  width: 3px;
}

.max-h-\[40vh\]::-webkit-scrollbar-track,
.sm\:max-h-\[50vh\]::-webkit-scrollbar-track,
.md\:max-h-\[60vh\]::-webkit-scrollbar-track {
  background: #141414;
}

.max-h-\[40vh\]::-webkit-scrollbar-thumb,
.sm\:max-h-\[50vh\]::-webkit-scrollbar-thumb,
.md\:max-h-\[60vh\]::-webkit-scrollbar-thumb {
  background-color: #333;
  border-radius: 3px;
}

.max-h-\[40vh\]::-webkit-scrollbar-thumb:hover,
.sm\:max-h-\[50vh\]::-webkit-scrollbar-thumb:hover,
.md\:max-h-\[60vh\]::-webkit-scrollbar-thumb:hover {
  background-color: #444;
}

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>