<!-- WriteModal.vue -->
<template>
  <div class="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
    <div class="bg-[#141414] w-full max-w-xl rounded-xl p-6">
      <!-- 헤더 -->
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-white text-lg font-bold">듀오 등록하기</h2>
        <button
            @click="$emit('close')"
            class="text-gray-400 hover:text-white transition-colors"
        >
          <XIcon class="w-5 h-5" />
        </button>
      </div>

      <!-- 포지션 선택 -->
      <div class="space-y-6">
        <div class="space-y-3">
          <label class="text-sm text-gray-300 font-medium">나의 포지션</label>
          <div class="grid grid-cols-5 gap-2">
            <button
                v-for="position in positions"
                :key="position.value"
                @click="myPosition = position.value"
                :class="[
                'flex flex-col items-center p-3 rounded-lg border transition-all',
                myPosition === position.value
                  ? 'bg-[#1A1A1A] border-[#2979FF] text-white'
                  : 'border-[#333] text-gray-400 hover:border-gray-500'
              ]"
            >
              <img :src="`/src/assets/icon/position/position_${position.value}.svg`" :alt="position.label" class="w-6 h-6 mb-1" />
              <span class="text-xs">{{ position.label }}</span>
            </button>
          </div>
        </div>

        <div class="space-y-3">
          <label class="text-sm text-gray-300 font-medium">찾는 포지션</label>
          <div class="grid grid-cols-5 gap-2">
            <button
                v-for="position in positions"
                :key="position.value"
                @click="targetPosition = position.value"
                :class="[
                'flex flex-col items-center p-3 rounded-lg border transition-all',
                targetPosition === position.value
                  ? 'bg-[#1A1A1A] border-[#2979FF] text-white'
                  : 'border-[#333] text-gray-400 hover:border-gray-500'
              ]"
            >
              <img :src="`/src/assets/icon/position/position_${position.value}.svg`" :alt="position.label" class="w-6 h-6 mb-1" />
              <span class="text-xs">{{ position.label }}</span>
            </button>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <!-- 큐 타입 -->
          <div class="space-y-3">
            <label class="text-sm text-gray-300 font-medium">큐 타입</label>
            <select
                v-model="queueType"
                class="w-full bg-[#1A1A1A] text-white text-sm px-3 py-2.5 rounded-lg border border-[#333] focus:outline-none focus:border-[#2979FF] transition-colors"
            >
              <option value="SOLO_RANK">솔로랭크</option>
              <option value="FLEX_RANK">자유랭크</option>
              <option value="NORMAL">일반게임</option>
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
        <div class="space-y-3">
          <label class="text-sm text-gray-300 font-medium">내용</label>
          <textarea
              v-model="content"
              rows="4"
              placeholder="듀오 모집 내용을 입력해주세요."
              class="w-full bg-[#1A1A1A] text-white text-sm px-3 py-2.5 rounded-lg border border-[#333] focus:outline-none focus:border-[#2979FF] transition-colors resize-none"
          ></textarea>
        </div>
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
import { ref } from 'vue'
import { XIcon, MicIcon, MicOffIcon } from 'lucide-vue-next'
import {communityApi} from "@/api/community.ts";
import type {CommunityPostDto} from "@/types/community.ts";
import Toast, {useToast} from "vue-toastification";

const positions = [
  { value: 'TOP', label: '탑' },
  { value: 'JG', label: '정글' },
  { value: 'MID', label: '미드' },
  { value: 'AD', label: '원딜' },
  { value: 'SUP', label: '서포터' }
]

const myPosition = ref('TOP')
const targetPosition = ref('TOP')
const queueType = ref('SOLO_RANK')
const hasMic = ref(false)
const content = ref('')

const emit = defineEmits(['close', 'submit'])

const handleSubmit = () => {
  const toast = useToast();
  if (!content.value) {
    toast.error('내용을 입력해주세요.')
    return
  }

  const formData: CommunityPostDto = {
    communityCode: 'duo',
    content: content.value,
    duoCommunityEntity: {
      gameMode: queueType.value,
      positionSelf: myPosition.value,
      positionFind: targetPosition.value,
      isUseMic: hasMic.value
    }
  }

  emit('submit', formData)
}
</script>