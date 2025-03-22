<template>
  <div class="fixed inset-0 bg-black/70 flex items-center justify-center z-50 px-4">
    <div class="bg-[#141414] w-full max-w-xl rounded-xl p-4 sm:p-6 max-h-[90vh] overflow-y-auto">
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
        <label class="text-sm text-gray-300 font-medium">내 포지션</label>
        <div class="grid grid-cols-5 gap-2">
          <button
              v-for="position in positions"
              :key="position.value"
              @click="selectLeaderPosition(position.value)"
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
        <label class="text-sm text-gray-300 font-medium">모집할 포지션</label>
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

      <!-- 티어 범위 설정 -->
      <div v-if="isRankedMode" class="space-y-3 mt-6">
        <div class="flex justify-between items-center">
          <label class="text-sm text-gray-300 font-medium">모집할 티어 범위</label>
          <div v-if="showTierRange" class="text-sm text-gray-300 font-medium">
            {{ getTierRangeDisplay() }}
          </div>
        </div>
        <div class="flex items-center gap-2">
          <div class="flex-1">
            <select
                v-model="minTier"
                class="w-full bg-[#1A1A1A] text-white text-sm px-3 py-2.5 rounded-lg border border-[#333] focus:outline-none focus:border-[#2979FF] transition-colors"
                @change="validateTierRange"
            >
              <option v-for="tier in tiers" :key="tier.code" :value="tier.code">
                {{ tier.name }}
              </option>
            </select>
          </div>
          <div class="text-white text-sm font-medium">~</div>
          <div class="flex-1">
            <select
                v-model="maxTier"
                class="w-full bg-[#1A1A1A] text-white text-sm px-3 py-2.5 rounded-lg border border-[#333] focus:outline-none focus:border-[#2979FF] transition-colors"
                @change="validateTierRange"
            >
              <option v-for="tier in tiers" :key="tier.code" :value="tier.code">
                {{ tier.name }}
              </option>
            </select>
          </div>
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

// 티어 정보 (BE와 일치하도록 설정)
const tiers = [
  { code: '전체', name: '전체' },
  { code: 'IRON', name: '아이언' },
  { code: 'BRONZE', name: '브론즈' },
  { code: 'SILVER', name: '실버' },
  { code: 'GOLD', name: '골드' },
  { code: 'PLATINUM', name: '플래티넘' },
  { code: 'EMERALD', name: '에메랄드' },
  { code: 'DIAMOND', name: '다이아몬드' },
  { code: 'MASTER', name: '마스터' },
  { code: 'GRANDMASTER', name: '그랜드마스터' },
  { code: 'CHALLENGER', name: '챌린저' }
]

const partyLeaderPosition = ref('')
const positionsOpen = ref({
  TOP: false,
  JG: false,
  MID: false,
  AD: false,
  SUP: false
})

// 티어 범위 설정
const minTier = ref('전체')
const maxTier = ref('전체')
const showTierRange = computed(() => minTier.value !== '전체' || maxTier.value !== '전체')
const isRankedMode = computed(() =>
    queueType.value === 'SOLO_RANK' || queueType.value === 'FLEX_RANK'
)

const getTierRangeDisplay = () => {
  if (minTier.value === '전체' && maxTier.value === '전체') {
    return '전체 티어'
  }

  if (minTier.value === '전체') {
    return getTierName(maxTier.value) + ' 이하'
  }

  if (maxTier.value === '전체') {
    return getTierName(minTier.value) + ' 이상'
  }

  return getTierName(minTier.value) + ' ~ ' + getTierName(maxTier.value)
}

const getTierName = (tierCode: string) => {
  const tier = tiers.find(t => t.code === tierCode)
  return tier ? tier.name : tierCode
}

const togglePositionOpen = (position: string) => {
  // 현재 파티장 포지션과 같은 포지션이면 토글하지 않음
  if (position === partyLeaderPosition.value) {
    return
  }

  // 토글 로직은 그대로 유지
  positionsOpen.value[position] = !positionsOpen.value[position]
}

const validateTierRange = () => {
  // '전체'인 경우는 항상 유효
  if (minTier.value === '전체' || maxTier.value === '전체') {
    return true
  }

  const minTierIndex = tiers.findIndex(t => t.code === minTier.value)
  const maxTierIndex = tiers.findIndex(t => t.code === maxTier.value)

  // 최소 티어가 최대 티어보다 높은 경우 (인덱스가 클수록 높은 티어)
  if (minTierIndex > maxTierIndex) {
    // 자동으로 최대 티어를 최소 티어로 설정
    maxTier.value = minTier.value
    return false
  }

  return true
}

const selectLeaderPosition = (position: string) => {
  // 이미 해당 포지션을 모집 중이었다면 모집 상태 해제
  if (positionsOpen.value[position]) {
    positionsOpen.value[position] = false
  }

  // 파티장 포지션 설정
  partyLeaderPosition.value = position
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

watch(partyLeaderPosition, (newPosition, oldPosition) => {
  if (newPosition) {
    // 새로 선택된 파티장 포지션은 모집 상태를 false로 설정
    positionsOpen.value[newPosition] = false

    // 이전에 선택된 파티장 포지션이 있었다면, 모집 상태를 그대로 유지 (원래대로 돌림)
    // 이렇게 하면 파티장 포지션을 변경할 때 이전 포지션의 모집 상태를 원래대로 되돌릴 수 있음
  }
})

const emit = defineEmits(['close', 'submit'])

const handleSubmit = () => {
  const toast = useToast()

  // 파티장 포지션 선택 필수
  if (!partyLeaderPosition.value) {
    toast.error('내 포지션을 선택해주세요.')
    return
  }

  // 최소한 한 포지션은 열려있어야 함 (파티장 포지션 제외)
  const hasOpenPosition = Object.entries(positionsOpen.value)
      .filter(([position]) => position !== partyLeaderPosition.value)
      .some(([, isOpen]) => isOpen)

  if (!hasOpenPosition) {
    toast.error('내 포지션을 제외하고 최소 한 개의 포지션을 모집해야 합니다.')
    return
  }

  // 티어 범위 검증
  if (!validateTierRange()) {
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
      isOpenSup: positionsOpen.value.SUP,
      tierMin: minTier.value,
      tierMax: maxTier.value
    }
  }

  emit('submit', formData)
}
</script>