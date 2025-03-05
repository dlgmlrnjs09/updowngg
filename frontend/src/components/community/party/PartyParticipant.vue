<!-- PartyParticipant.vue -->
<template>
  <div
      class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between text-center h-[52px] w-full"
      :class="{
      'border-2 border-[#2979FF]': isWriter,
      'border border-[#333]': participant.isOpenPosition && !participant.summonerInfoDto,
      'border border-gray-700': participant.summonerInfoDto && !isWriter,
      'border border-[#383838]': !isWriter && !participant.isOpenPosition,
      'opacity-50': !isWriter && !participant.isOpenPosition
    }"
  >
    <!-- 포지션 채워진 경우 -->
    <template v-if="participant.summonerInfoDto">
      <div class="flex items-center gap-2">
        <img
            :src="getPositionImage(participant.position)"
            :alt="participant.position"
            class="w-5 h-5"
        >
      </div>

      <div class="flex-1 text-left ml-2 min-w-0">
        <div class="flex items-center gap-1">
          <div
              class="text-xs font-medium text-white hover:text-[#2979FF] cursor-pointer truncate max-w-full"
              @click="$emit('profile-click', participant.summonerInfoDto.summonerBasicInfoDto)"
          >
            {{ participant.summonerInfoDto.summonerBasicInfoDto.gameName }}
          </div>
          <div class="text-[10px] text-gray-400 truncate">
            #{{ participant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
          </div>
        </div>

        <div class="flex gap-1 mt-0.5">
<!--          <span-->
<!--              v-for="tag in participant.summonerInfoDto.frequentTagDtoList"-->
<!--              :key="tag.tagCode"-->
<!--              class="bg-[#2979FF]/10 text-[#2979FF] text-[9px] px-1 py-0.5 rounded"-->
<!--          >-->
<!--            {{ tag.tagValue }}-->
<!--          </span>-->
          <TagList :tags="participant.summonerInfoDto.frequentTagDtoList.slice(0, 2)" size="xxSmallReactive" :is-show-count="true"/>
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

    <!-- 대기 중인 경우 -->
    <template v-else>
      <img
          :src="getPositionImage(participant.position)"
          :alt="participant.position"
          class="w-5 h-5"
      >
      <div class="text-gray-500 text-xs flex-1">
        {{ participant.isOpenPosition ? '모집 중' : '-' }}
      </div>
      <div v-if="showApplyButton" class="apply-btn-container position-buttons">
        <button
            @click="handleApply"
            :class="[
            'text-[10px] px-2 py-0.5 rounded button-transition',
            isApplied ? 'bg-gray-500 cursor-not-allowed' : 'bg-[#2979FF]'
          ]"
            :disabled="isApplied"
        >
          {{ isApplied ? '신청완료' : '신청' }}
        </button>
      </div>
      <div v-else class="w-[60px]"></div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ThumbsUp, ThumbsDown } from 'lucide-vue-next'
import { useImageUrl } from "@/utils/imageUtil"
import TagList from "@/components/common/TagList.vue";

const { getPositionImage } = useImageUrl()

interface Props {
  participant: ParticipantDto
  isWriter: boolean
  showApplyButton: boolean
  isApplied: boolean
  postId: number
  position: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'apply', postId: number, position: string): void
  (e: 'profile-click', summoner: SummonerBasicInfoDto): void
}>()

// Methods
const handleApply = () => {
  emit('apply', props.postId, props.position)
}
</script>

<style scoped>
.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 버튼 트랜지션 추가 */
.button-transition {
  transition: background-color 0.2s ease, opacity 0.2s ease;
  will-change: background-color, opacity;
}

/* 애니메이션 적용을 위한 컨테이너 */
.apply-btn-container {
  width: 60px;
  display: flex;
  justify-content: center;
}
</style>