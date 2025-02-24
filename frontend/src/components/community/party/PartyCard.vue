<!-- PartyCard.vue -->
<template>
  <div class="bg-[#141414] rounded-xl p-4 border border-[#2979FF1A]">
    <!-- 본문 -->
    <div class="bg-[#1A1A1A] p-3 rounded-lg mb-4 border-l-4 border-l-[#2979FF] min-h-[102px]">
      <p class="text-white leading-relaxed line-clamp-3 whitespace-pre-wrap break-words">
        {{ card.content }}
      </p>
    </div>

    <!-- 포지션 그리드 -->
    <div class="grid grid-cols-1 gap-2 mb-4">
      <PartyParticipant
          v-for="participant in card.participantDtoList"
          :key="participant.position"
          :participant="participant"
          :is-writer="card.writerPuuid === participant.summonerInfoDto?.summonerBasicInfoDto?.puuid"
          :show-apply-button="shouldShowApplyButton(participant)"
          :is-applied="isApplied(card.postId, participant.position)"
          :post-id="card.postId"
          :position="participant.position"
          @apply="handleApply"
          @profile-click="handleProfileClick"
      />
    </div>

    <!-- 추가 정보 -->
    <div class="bg-[#1A1A1A] rounded-lg p-3 flex justify-between items-center">
      <div class="flex items-center gap-2">
        <span class="text-gray-400 text-xs">큐타입</span>
        <span class="text-white text-sm">
          {{ getGameModeName(card.gameMode) }}
        </span>
      </div>
      <div>
        <MicIcon
            v-if="card.isUseMic"
            class="w-4 h-4 text-[#2979FF]"
        />
        <MicOffIcon
            v-else
            class="w-4 h-4 text-gray-500"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { MicIcon, MicOffIcon } from 'lucide-vue-next'
import type { PartyPostCardDto } from "@/types/community"
import type {SummonerBasicInfoDto} from "@/types/ranking.ts";
import PartyParticipant from './PartyParticipant.vue'

type ParticipantDto = {
  position: string;
  isOpenPosition: boolean;
  summonerInfoDto?: any;
};

interface Props {
  card: PartyPostCardDto
  myPuuid: string
  appliedPositions: Map<string, boolean>
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'apply', postId: number, position: string): void
  (e: 'profile-click', summoner: SummonerBasicInfoDto): void
}>()

// Methods
const shouldShowApplyButton = (participant: ParticipantDto) => {
  if (props.card.writerPuuid === props.myPuuid) {
    return false
  }

  const isAlreadyParticipant = props.card.participantDtoList.some(p =>
      p.summonerInfoDto?.summonerBasicInfoDto?.puuid === props.myPuuid
  )
  if (isAlreadyParticipant) {
    return false
  }

  return participant.isOpenPosition && !participant.summonerInfoDto
}

const isApplied = (postId: number, position: string) => {
  return props.appliedPositions.get(`${postId}-${position}`) || false
}

const handleApply = (postId: number, position: string) => {
  emit('apply', postId, position)
}

const handleProfileClick = (summoner: SummonerBasicInfoDto) => {
  emit('profile-click', summoner)
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
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>