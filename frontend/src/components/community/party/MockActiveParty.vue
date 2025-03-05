<template>
  <div
      id="active-party"
      class="max-w-6xl mx-auto mb-8 mock-party"
      :class="{ 'highlight-effect': hasHighlight }"
  >
    <div class="bg-[#141414] rounded-xl border border-[#2979FF] p-4">
      <!-- PC/태블릿 뷰 -->
      <div class="hidden sm:flex flex-col gap-4">
        <!-- 첫 번째 줄: 기본 정보 및 액션 버튼 -->
        <div class="flex items-center justify-between">
          <!-- 좌측: 기본 정보 -->
          <div class="flex items-center gap-4">
            <div class="flex items-center gap-4">
              <span class="text-[#2979FF] text-sm font-medium">모집중</span>
              <div class="h-4 w-[1px] bg-gray-700"></div>
              <div class="flex items-center gap-2">
                <Users class="w-4 h-4 text-gray-400" />
                <span class="text-white text-sm">2 / 5</span>
              </div>
              <div class="h-4 w-[1px] bg-gray-700"></div>
              <MicIcon class="text-[#2979FF] w-4 h-4" />
              <div class="h-4 w-[1px] bg-gray-700"></div>
              <span class="text-white text-sm">일반게임</span>
            </div>
            <div class="h-4 w-[1px] bg-gray-700"></div>
            <div class="flex items-center gap-2">
              <div class="flex items-center gap-1">
                <img :src="getTierImage('GOLD')" alt="GOLD" class="w-4 h-4">
                <span class="text-white text-sm">골드 이상</span>
              </div>
            </div>
          </div>

          <!-- 우측: 액션 버튼 -->
          <div class="flex items-center gap-2">
            <button class="bg-[#1A1A1A] hover:bg-[#242424] text-white px-3 py-1.5 rounded-lg text-xs">
              마감
            </button>
            <button class="bg-[#1A1A1A] hover:bg-[#242424] text-[#FF5252] px-3 py-1.5 rounded-lg text-xs">
              취소
            </button>
          </div>
        </div>

        <!-- 구분선 추가 -->
        <div class="h-[1px] w-full bg-gray-700"></div>

        <!-- 두 번째 줄: 포지션별 참가자 정보 -->
        <div class="flex items-center gap-3">
          <div class="relative" v-for="position in positions" :key="position.code">
            <div
                class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between text-center h-[52px] w-full relative applicant-button"
                :class="{
                  'border-2 border-[#2979FF]': position.code === 'TOP',
                  'border border-[#333]': position.code !== 'TOP' && position.code !== 'MID',
                  'border border-gray-700': position.code === 'MID',
                }"
            >
              <div class="flex items-center gap-2">
                <img :src="getPositionImage(position.code)" :alt="position.code" class="w-4 h-4">
              </div>

              <template v-if="position.code === 'TOP' || position.code === 'MID'">
                <div class="flex-1 text-left ml-2 min-w-0">
                  <div class="flex flex-col">
                    <div class="flex items-center gap-1">
                      <span class="text-white text-xs truncate">{{ position.code === 'TOP' ? '임시파티장' : '탑티어정글러' }}</span>
                    </div>
                    <div class="text-gray-400 text-[10px] truncate">
                      #KR123
                    </div>
                  </div>
                </div>
              </template>

              <template v-else>
                <div v-if="position.code === 'SUP'" class="flex items-center gap-2 z-10 applicant-button">
                  <span class="text-gray-500 text-xs">모집 중</span>
                  <span class="text-[#2979FF] text-xs flex items-center gap-1">
                    (2)
                    <ChevronDown class="w-3 h-3" />
                  </span>
                </div>
                <span v-else class="text-gray-500 text-xs ml-1">모집 중</span>
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- 모바일 뷰 -->
      <div class="sm:hidden space-y-4">
        <!-- 첫 번째 줄: 상태 및 기본 정보 -->
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-[#2979FF] text-sm font-medium">모집중</span>
            <span class="text-white text-sm">일반게임</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="flex items-center gap-1">
              <Users class="w-4 h-4 text-gray-400" />
              <span class="text-white text-sm">2 / 5</span>
            </div>
            <MicIcon class="text-[#2979FF] w-4 h-4" />
          </div>
        </div>

        <!-- 구분선 -->
        <div class="h-[1px] w-full bg-gray-700"></div>

        <!-- 모바일 포지션 정보 -->
        <div class="grid grid-cols-1 gap-2">
          <div
              v-for="position in positions"
              :key="position.code"
              class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between text-center h-[52px] w-full relative applicant-button"
              :class="{
                'border-2 border-[#2979FF]': position.code === 'TOP',
                'border border-[#333]': position.code !== 'TOP' && position.code !== 'MID',
                'border border-gray-700': position.code === 'MID',
              }"
          >
            <div class="flex items-center gap-2 w-full">
              <img :src="getPositionImage(position.code)" :alt="position.code" class="w-5 h-5">

              <template v-if="position.code === 'TOP' || position.code === 'MID'">
                <div class="flex-1 text-left min-w-0">
                  <div class="flex items-center gap-1">
                    <div class="text-xs font-medium text-white hover:text-[#2979FF] cursor-pointer truncate max-w-full">
                      {{ position.code === 'TOP' ? '임시파티장' : '탑티어정글러' }}
                    </div>
                    <div class="text-[10px] text-gray-400 truncate">
                      #KR123
                    </div>
                  </div>
                </div>
              </template>

              <template v-else>
                <div class="flex flex-1 items-center">
                  <div class="text-gray-500 text-xs">
                    모집 중
                  </div>
                  <template v-if="position.code === 'SUP'">
                    <div class="flex items-center gap-1 text-[#2979FF] text-xs ml-2 applicant-button">
                      (2)
                      <ChevronDown class="w-3 h-3" />
                    </div>
                  </template>
                </div>
              </template>
            </div>
          </div>
        </div>

        <!-- 모바일 액션 버튼 -->
        <div class="flex justify-end gap-2">
          <button class="bg-[#1A1A1A] hover:bg-[#242424] text-white px-3 py-1.5 rounded-lg text-xs">
            마감
          </button>
          <button class="bg-[#1A1A1A] hover:bg-[#242424] text-[#FF5252] px-3 py-1.5 rounded-lg text-xs">
            취소
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { MicIcon, Users, ChevronDown } from 'lucide-vue-next'
import { useImageUrl } from "@/utils/imageUtil"

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

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.applicant-button {
  position: relative;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 80px;
}

.applicant-button:hover {
  background-color: rgba(41, 121, 255, 0.1);
}
</style>