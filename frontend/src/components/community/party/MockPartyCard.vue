<!-- MockPartyCard.vue -->
<template>
  <div class="bg-[#141414] rounded-xl p-4 border border-[#2979FF1A] mock-party" :class="{ 'highlight-effect': hasHighlight }">
    <!-- 본문 -->
    <div class="bg-[#1A1A1A] p-3 rounded-lg mb-4 border-l-4 border-l-[#2979FF] min-h-[102px]">
      <p class="text-white leading-relaxed line-clamp-3 whitespace-pre-wrap break-words">
        안녕하세요! 같이 즐겁게 플레이하실 분을 모집합니다. 서로 존중하는, 재미있는 게임을 해요. 포지션별 모집 중입니다. 마이크 사용 가능하신 분 우대!
      </p>
    </div>

    <!-- 포지션 그리드 -->
    <div class="grid grid-cols-1 gap-2 mb-4">
      <div v-for="position in positions" :key="position.code"
          class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between text-center h-[52px] w-full"
          :class="{
            'border-2 border-[#2979FF]': position.code === 'TOP',
            'border border-[#333]': position.code !== 'TOP' && position.code !== 'JG',
            'border border-gray-700': position.code === 'JG'
          }"
      >
        <!-- 포지션 아이콘 -->
        <div class="flex items-center gap-2">
          <img :src="getPositionImage(position.code)" :alt="position.code" class="w-5 h-5">
        </div>

        <!-- 소환사 정보 또는 모집 중 -->
        <template v-if="position.code === 'TOP' || position.code === 'JG'">
          <div class="flex-1 text-left ml-2 min-w-0">
            <div class="flex items-center gap-1">
              <div class="text-xs font-medium text-white hover:text-[#2979FF] cursor-pointer truncate max-w-full">
                {{ position.code === 'TOP' ? '임시파티장' : '탑티어정글러' }}
              </div>
              <div class="text-[10px] text-gray-400 truncate">
                #KR123
              </div>
            </div>
            <div class="flex gap-1 mt-0.5">
              <span class="bg-[#2979FF]/10 text-[#2979FF] text-[9px] px-1 py-0.5 rounded">
                {{ position.code === 'TOP' ? '소통왕' : '카정위' }}
              </span>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="flex items-center justify-center flex-1 ml-2">
            <span class="text-gray-500 text-xs">모집 중</span>
            <!-- 특별히 서포터에 신청자 표시 -->
            <span v-if="position.code === 'SUP'" class="text-[#2979FF] text-xs ml-1 flex items-center gap-1">
              (2)
              <ChevronDown class="w-3 h-3" />
            </span>
          </div>
        </template>

        <!-- 추가 신청 버튼 (MID, AD에 표시) -->
        <div v-if="position.code === 'MID' || position.code === 'AD'" class="flex flex-shrink-0 apply-btn-container position-buttons">
          <button 
            class="bg-[#2979FF] text-white text-xs py-1 px-2 rounded-lg min-w-12"
          >
            신청
          </button>
        </div>
      </div>
    </div>

    <!-- 추가 정보 -->
    <div class="bg-[#1A1A1A] rounded-lg p-3">
      <div class="flex flex-col gap-2">
        <!-- 큐타입 정보 -->
        <div class="flex items-center">
          <span class="text-gray-400 text-xs w-16">큐타입</span>
          <div class="flex justify-between items-center flex-1">
            <span class="text-white text-sm">
              일반게임
            </span>
            <div>
              <MicIcon class="w-4 h-4 text-[#2979FF]" />
            </div>
          </div>
        </div>

        <!-- 티어 범위 정보 -->
        <div class="flex items-center">
          <span class="text-gray-400 text-xs w-16">티어 범위</span>
          <div class="flex items-center gap-1">
            <img :src="getTierImage('GOLD')" alt="GOLD" class="w-4 h-4">
            <span class="text-white text-sm">
              골드 이상
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ChevronDown, MicIcon } from 'lucide-vue-next'
import { useImageUrl } from "@/utils/imageUtil.ts"
const { getTierImage, getPositionImage } = useImageUrl()

defineProps<{
  hasHighlight?: boolean
}>()

// 포지션 정보
const positions = [
  { code: 'TOP', name: '탑' },
  { code: 'JG', name: '정글' },
  { code: 'MID', name: '미드' },
  { code: 'AD', name: '원딜' },
  { code: 'SUP', name: '서포터' }
]
</script>

<style scoped>
.highlight-effect {
  animation: highlight 2s ease-out;
  position: relative;
  z-index: 10;
}

.mock-party {
  opacity: 0.85;
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

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 포지션 컨테이너에 호버 효과 */
.position-buttons {
  position: relative;
  cursor: pointer;
  transition: all 0.2s ease;
}

.position-buttons:hover {
  filter: brightness(1.1);
}
</style>