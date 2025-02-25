<!-- MyActivePartyParticipant.vue -->
<template>
  <div
      class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between text-center h-[52px] w-full relative applicant-button"
      :class="[
      {
        'border-2 border-[#2979FF]': isWriter,
        'border border-[#333]': participant.isOpenPosition && !participant.summonerInfoDto,
        'border border-gray-700': participant.summonerInfoDto && !isWriter,
        'border border-[#383838]': !isWriter && !participant.isOpenPosition,
        'opacity-50': !isWriter && !participant.isOpenPosition,
        'cursor-pointer': hasApplicants && participant.isOpenPosition && !participant.summonerInfoDto
      },
      isMobile ? '' : 'px-2 py-1'
    ]"
      @click="hasApplicants && participant.isOpenPosition && !participant.summonerInfoDto ? toggleApplicants() : null"
  >
    <!-- PC/태블릿 뷰 -->
    <template v-if="!isMobile">
      <div class="flex items-center gap-2">
        <img :src="getPositionImage(participant.position)" :alt="participant.position" class="w-4 h-4">
      </div>
      <div v-if="participant.summonerInfoDto" class="flex-1 text-left ml-2 min-w-0">
        <div class="flex items-center gap-1">
          <span class="text-white text-xs truncate max-w-[80px]">
            {{ participant.summonerInfoDto.summonerBasicInfoDto.gameName }}
          </span>
        </div>
      </div>

      <template v-else>
        <div
            v-if="hasApplicants"
            class="flex items-center gap-2 z-10 applicant-button"
            @click.stop="toggleApplicants()"
        >
          <span class="text-gray-500 text-xs">대기 중</span>
          <span class="text-[#2979FF] text-xs flex items-center gap-1">
            ({{ applicantsCount }})
            <component :is="isApplicantsVisible ? ChevronUp : ChevronDown" class="w-3 h-3" />
          </span>
        </div>
        <span v-else class="text-gray-500 text-xs ml-1">대기 중</span>
      </template>
    </template>

    <!-- 모바일 뷰 -->
    <template v-else>
      <div class="flex items-center gap-2 w-full">
        <img :src="getPositionImage(participant.position)" :alt="participant.position" class="w-5 h-5">

        <template v-if="participant.summonerInfoDto">
          <div class="flex-1 text-left min-w-0">
            <div class="flex items-center gap-1">
              <div class="text-xs font-medium text-white hover:text-[#2979FF] cursor-pointer truncate max-w-full">
                {{ participant.summonerInfoDto.summonerBasicInfoDto.gameName }}
              </div>
              <div class="text-[10px] text-gray-400 truncate">
                #{{ participant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
              </div>
            </div>

            <div class="flex gap-1 mt-0.5">
              <span
                  v-for="tag in participant.summonerInfoDto.frequentTagDtoList"
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
                {{ participant.summonerInfoDto.reviewStatsDto.score?.toFixed(1) ?? 0 }}점
              </span>
            </div>

            <div class="flex items-center gap-2">
              <div class="flex items-center gap-1">
                <ThumbsUp class="w-3 h-3 text-[#4CAF50]" />
                <span class="text-[#4CAF50] text-[10px]">
                  {{ participant.summonerInfoDto.reviewStatsDto.upCount }}
                </span>
              </div>
              <div class="flex items-center gap-1">
                <ThumbsDown class="w-3 h-3 text-[#FF5252]" />
                <span class="text-[#FF5252] text-[10px]">
                  {{ participant.summonerInfoDto.reviewStatsDto.downCount }}
                </span>
              </div>
            </div>
          </div>

          <div class="flex gap-1 ml-2">
            <div
                v-for="(champion, index) in participant.summonerInfoDto.mostChampionDto.slice(0, 2)"
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

        <template v-else>
          <div class="flex flex-1 items-center">
            <div class="text-gray-500 text-xs">
              {{ participant.isOpenPosition ? '대기 중' : '-' }}
            </div>
            <template v-if="hasApplicants">
              <div
                  class="flex items-center gap-1 text-[#2979FF] text-xs ml-2 applicant-button"
                  @click.stop="toggleApplicants()"
              >
                ({{ applicantsCount }})
                <component :is="isApplicantsVisible ? ChevronUp : ChevronDown" class="w-3 h-3" />
              </div>
            </template>
          </div>
        </template>
      </div>
    </template>

    <!-- 전체 영역 클릭을 위한 오버레이 (신청자가 있고, 현재 대기 중인 셀에만 표시) -->
    <div
        v-if="hasApplicants && participant.isOpenPosition && !participant.summonerInfoDto"
        class="absolute inset-0 bg-transparent z-0"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { ThumbsUp, ThumbsDown, ChevronUp, ChevronDown } from 'lucide-vue-next'
import { useImageUrl } from "@/utils/imageUtil"

const { getPositionImage } = useImageUrl()

type ParticipantDto = {
  position: string;
  isOpenPosition: boolean;
  summonerInfoDto?: any;
};

interface Props {
  participant: ParticipantDto
  isWriter: boolean
  position: string
  isApplicantsVisible: boolean
  applicantsCount: number
  isMobile?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isMobile: false
})

const emit = defineEmits<{
  (e: 'toggle-applicants', position: string): void
}>()

// Computed
const hasApplicants = computed(() => props.applicantsCount > 0)

// Methods
const toggleApplicants = () => {
  emit('toggle-applicants', props.position);
}
</script>

<style scoped>
.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.applicant-button {
  position: relative;
  cursor: pointer;
  transition: all 0.2s ease;
}

.applicant-button:hover {
  background-color: rgba(41, 121, 255, 0.1);
}
</style>