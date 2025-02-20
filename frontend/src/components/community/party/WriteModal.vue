<template>
  <div class="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
    <div class="bg-[#141414] w-full max-w-xl rounded-xl p-6">
      <!-- 헤더 -->
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-white text-lg font-bold">파티 모집하기</h2>
        <button
            @click="$emit('close')"
            class="text-gray-400 hover:text-white transition-colors"
        >
          <XIcon class="w-5 h-5" />
        </button>
      </div>

      <!-- 파티장 포지션 선택 -->
      <div class="space-y-3 mb-6">
        <label class="text-sm text-gray-300 font-medium">파티장 포지션</label>
        <div class="grid grid-cols-5 gap-2">
          <button
              v-for="position in positions"
              :key="position.value"
              @click="partyLeaderPosition = position.value"
              :class="[
              'flex flex-col items-center p-3 rounded-lg border transition-all',
              partyLeaderPosition === position.value
                ? 'bg-[#1A1A1A] border-[#2979FF] text-white'
                : 'border-[#333] text-gray-400 hover:border-gray-500'
            ]"
          >
            <img
                :src="getPositionImage(position.value)"
                :alt="position.label"
                class="w-6 h-6 mb-1"
            />
            <span class="text-xs">{{ position.label }}</span>
          </button>
        </div>
      </div>

      <!-- 포지션 모집 설정 -->
      <div class="space-y-3">
        <label class="text-sm text-gray-300 font-medium">포지션 모집 설정</label>
        <div class="grid grid-cols-5 gap-2">
          <button
              v-for="position in positions"
              :key="position.value"
              @click="togglePositionOpen(position.value)"
              :disabled="position.value === partyLeaderPosition"
              :class="[
              'flex flex-col items-center p-3 rounded-lg border transition-all',
              position.value === partyLeaderPosition
                ? 'opacity-50 cursor-not-allowed'
                : (positionsOpen[position.value]
                  ? 'bg-[#1A1A1A] border-[#2979FF] text-white'
                  : 'border-[#333] text-gray-400 hover:border-gray-500')
            ]"
          >
            <img
                :src="getPositionImage(position.value)"
                :alt="position.label"
                class="w-6 h-6 mb-1"
            />
            <span class="text-xs">{{ position.label }}</span>
            <span
                v-if="positionsOpen[position.value]"
                class="text-[10px] text-[#2979FF] mt-1"
            >
              모집 중
            </span>
          </button>
        </div>
      </div>

      <!-- 나머지 기존 코드 유지 -->
      <div class="grid grid-cols-2 gap-4 mt-6">
        <!-- 큐 타입 선택 -->
        <div class="space-y-3">
          <label class="text-sm text-gray-300 font-medium">큐 타입</label>
          <select
              v-model="queueType"
              class="w-full bg-[#1A1A1A] text-white text-sm px-3 py-2.5 rounded-lg border border-[#333] focus:outline-none focus:border-[#2979FF] transition-colors"
          >
            <option value="SOLO_RANK">솔로랭크</option>
            <option value="FLEX_RANK">자유랭크</option>
            <option value="NORMAL">일반게임</option>
            <option value="ARAM">칼바람나락</option>
          </select>
        </div>

        <!-- 마이크 토글 -->
        <div class="space-y-3">
          <label class="text-sm text-gray-300 font-medium">음성 채팅</label>
          <button
              @click="hasMic = !hasMic"
              :class="[
        'w-full px-3 py-2.5 rounded-lg border text-sm font-medium transition-colors flex items-center justify-center gap-2',
        hasMic
          ? 'bg-[#1A1A1A] border-[#2979FF] text-white'
          : 'border-[#333] text-gray-400 hover:border-gray-500'
      ]"
          >
            <MicIcon v-if="hasMic" class="w-4 h-4" />
            <MicOffIcon v-else class="w-4 h-4" />
            {{ hasMic ? '마이크 ON' : '마이크 OFF' }}
          </button>
        </div>
      </div>

      <!-- 내용 입력 -->
      <div class="space-y-3 mt-6">
        <div class="flex justify-between">
          <label class="text-sm text-gray-300 font-medium">내용</label>
          <span class="text-sm" :class="remainingChars >= 0 ? 'text-gray-400' : 'text-red-500'">
      {{ content.length }}/{{ maxLength }}
    </span>
        </div>
        <textarea
            v-model="content"
            rows="4"
            placeholder="파티 모집 내용을 입력해주세요."
            maxlength="50"
            class="w-full bg-[#1A1A1A] text-white text-sm px-3 py-2.5 rounded-lg border border-[#333] focus:outline-none focus:border-[#2979FF] transition-colors resize-none whitespace-pre-wrap break-words"
        ></textarea>
      </div>

      <!-- 하단 버튼 -->
      <div class="flex justify-end mt-6">
        <button
            @click="handleSubmit"
            class="bg-[#2979FF] text-white px-6 py-2 rounded-lg hover:bg-[#2565D1] transition-colors text-sm font-medium"
        >
          등록하기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { XIcon, MicIcon, MicOffIcon } from 'lucide-vue-next'
import { useToast } from "vue-toastification"
import { useImageUrl } from "@/utils/imageUtil.ts"

const positions = [
  { value: 'TOP', label: '탑' },
  { value: 'JG', label: '정글' },
  { value: 'MID', label: '미드' },
  { value: 'AD', label: '원딜' },
  { value: 'SUP', label: '서포터' }
]

const partyLeaderPosition = ref('')
const positionsOpen = ref({
  TOP: false,
  JG: false,
  MID: false,
  AD: false,
  SUP: false
})

const togglePositionOpen = (position: string) => {
  // 파티장 포지션은 자동으로 비활성화
  if (position !== partyLeaderPosition.value) {
    positionsOpen.value[position] = !positionsOpen.value[position]
  }
}

const queueType = ref('SOLO_RANK')
const hasMic = ref(false)
const content = ref('')
const maxLength = 50
const remainingChars = computed(() => maxLength - content.value.length)
const { getPositionImage } = useImageUrl()

watch(content, (newValue) => {
  if (newValue.length > maxLength) {
    content.value = newValue.slice(0, maxLength)
  }
})

const emit = defineEmits(['close', 'submit'])

const handleSubmit = () => {
  const toast = useToast()

  // 파티장 포지션 선택 필수
  if (!partyLeaderPosition.value) {
    toast.error('파티장의 포지션을 선택해주세요.')
    return
  }

  // 최소한 한 포지션은 열려있어야 함 (파티장 포지션 제외)
  const hasOpenPosition = Object.entries(positionsOpen.value)
      .filter(([position]) => position !== partyLeaderPosition.value)
      .some(([, isOpen]) => isOpen)

  if (!hasOpenPosition) {
    toast.error('파티장 포지션을 제외하고 최소 한 개의 포지션을 모집해야 합니다.')
    return
  }

  if (!content.value) {
    toast.error('내용을 입력해주세요.')
    return
  }

  const formData = {
    communityCode: 'party',
    content: content.value,
    partyCommunityDto: {
      gameMode: queueType.value,
      isUseMic: hasMic.value,
      positionSelf: partyLeaderPosition.value,
      isOpenTop: positionsOpen.value.TOP,
      isOpenJungle: positionsOpen.value.JG,
      isOpenMid: positionsOpen.value.MID,
      isOpenAd: positionsOpen.value.AD,
      isOpenSup: positionsOpen.value.SUP
    }
  }

  console.log('writemodal == ' + JSON.stringify(formData))

  emit('submit', formData)
}
</script>