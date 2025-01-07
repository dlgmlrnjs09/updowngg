<template>
  <div class="min-h-screen bg-[#0A0A0A] p-6">
    <div class="max-w-6xl mx-auto mb-8">
      <!-- 상단 영역 -->
      <div class="flex justify-between items-center mt-20 mb-5">
        <div class="bg-[#141414] p-3 rounded-xl flex-1 mr-4">
          <div class="flex gap-3 items-center">
            <div class="relative flex-1">
              <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                  v-model="searchQuery"
                  type="text"
                  placeholder="듀오 검색하기"
                  class="w-full bg-[#1A1A1A] text-white text-sm pl-9 pr-4 py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
              />
            </div>
            <select
                v-model="selectedPosition"
                class="bg-[#1A1A1A] text-white text-sm px-3 py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
            >
              <option value="">모든 포지션</option>
              <option value="TOP">탑</option>
              <option value="JG">정글</option>
              <option value="MID">미드</option>
              <option value="AD">원딜</option>
              <option value="SUP">서포터</option>
            </select>
            <select
                v-model="selectedTier"
                class="bg-[#1A1A1A] text-white text-sm px-3 py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
            >
              <option value="">모든 티어</option>
              <option value="IRON">아이언</option>
              <option value="BRONZE">브론즈</option>
              <option value="SILVER">실버</option>
              <option value="GOLD">골드</option>
              <option value="PLATINUM">플래티넘</option>
            </select>
          </div>
        </div>
        <button
            @click="showWriteModal = true"
            class="bg-[#2979FF] text-white px-4 py-1.5 rounded-lg hover:bg-[#2565D1] transition-colors text-sm whitespace-nowrap h-12"
        >
          듀오 등록하기
        </button>
      </div>

      <!-- 글쓰기 모달 -->
      <WriteModal
          v-if="showWriteModal"
          @close="showWriteModal = false"
          @submit="handleDuoSubmit"
      />

      <!-- 듀오 카드 그리드 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
            v-for="card in duoCards"
            :key="card.id"
            class="bg-[#141414] rounded-xl p-4 hover:bg-[#1A1A1A] transition-colors h-[300px] flex flex-col"
        >
          <!-- 내용 -->
          <p class="text-gray-200 text-base mb-3 line-clamp-3">{{ card.content }}</p>

          <div class="mt-auto"></div>

          <!-- 소환사 프로필 섹션 -->
          <div class="bg-[#1A1A1A] rounded-lg rounded-b-none p-3">
            <div class="flex gap-3">
              <img
                  :src="card.gameProfile.icon"
                  alt="Game Profile"
                  class="w-12 h-12 rounded-lg"
              />
              <div class="flex-1">
                <div class="flex items-center justify-between mb-1">
                  <div class="flex items-center gap-2">
                    <div class="text-white text-sm font-medium">{{ card.gameProfile.name }}</div>
                    <img
                        :src="`/src/assets/icon/position/position_${card.position}.svg`"
                        :alt="card.position"
                        class="w-4 h-4"
                    />
                  </div>
                  <button class="bg-[#2979FF] text-white text-xs px-3 py-1.5 rounded-lg hover:bg-[#2565D1] transition-colors">
                    메시지
                  </button>
                </div>
                <div class="flex items-center gap-3 mt-1">
                  <div class="flex items-center gap-1">
                    <ThumbsUp class="w-3 h-3 text-[#4CAF50]" />
                    <span class="text-[#4CAF50] text-xs">{{ card.reviews.up }}</span>
                  </div>
                  <div class="flex items-center gap-1">
                    <ThumbsDown class="w-3 h-3 text-[#FF5252]" />
                    <span class="text-[#FF5252] text-xs">{{ card.reviews.down }}</span>
                  </div>
                </div>
                <div class="flex gap-1.5 flex-wrap mt-1">
                  <span
                      v-for="(tag, index) in card.tags"
                      :key="index"
                      class="bg-[#141414] text-gray-300 text-[10px] px-2 py-0.5 rounded-full"
                  >
                    {{ tag }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- 게임 정보 섹션 -->
          <div class="bg-[#1A1A1A] rounded-lg rounded-t-none divide-y divide-[#232323]">
            <!-- 챔피언 정보 -->
            <div class="px-3 py-2">
              <div class="grid grid-cols-3 gap-2">
                <div
                    v-for="(champion, index) in card.champions"
                    :key="index"
                    class="group relative bg-[#141414] rounded-lg p-2"
                >
                  <div class="flex items-start gap-2">
                    <div class="relative">
                      <img
                          :src="champion.icon"
                          :alt="champion.name"
                          class="w-8 h-8 rounded"
                      />
                      <div class="absolute -bottom-1 -right-1 bg-black text-[10px] text-white px-1 rounded font-medium">
                        {{ champion.games }}
                      </div>
                    </div>
                    <div class="flex-1">
                      <div class="text-[10px] text-gray-200 font-medium">{{ champion.name }}</div>
                      <div class="flex items-center gap-1 mt-0.5">
                        <span class="text-[#4CAF50] text-[10px] font-medium">{{ champion.kda.toFixed(1) }}</span>
                        <span class="text-gray-500 text-[10px]">평점</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search, ThumbsUp, ThumbsDown } from 'lucide-vue-next'
import WriteModal from '@/components/community/duo/WriteModal.vue'
import type {CommunityPostDto} from "@/types/community.ts";
import {communityApi} from "@/api/community.ts";

interface Champion {
  name: string
  icon: string
  games: number
  kda: number
}

interface GameProfile {
  name: string
  icon: string
}

interface Reviews {
  up: number
  down: number
}

interface Tier {
  name: string
  icon: string
}

interface DuoCard {
  id: number
  content: string
  gameProfile: GameProfile
  champions: Champion[]
  reviews: Reviews
  tags: string[]
  tier: Tier
  winRate: number
  position: string
}

const searchQuery = ref('')
const selectedPosition = ref('')
const selectedTier = ref('')
const showWriteModal = ref(false)

const handleDuoSubmit = (formData: CommunityPostDto) => {
  console.log('듀오 등록 데이터:', formData)
  const data = communityApi.insertPost('duo', formData);
  console.log(data)
}

const duoCards = ref<DuoCard[]>([
  {
    id: 1,
    content: "탑 듀오 구합니다! 골드 이상, 음성채팅 가능하신 분! 친목보다는 실력 위주로 찾고 있습니다. 평일 저녁 시간대 가능하신 분이면 좋겠습니다.",
    gameProfile: {
      name: "Hide on bush",
      icon: "/api/placeholder/40/40"
    },
    champions: [
      { name: "Darius", icon: "/api/placeholder/32/32", games: 45, kda: 3.21 },
      { name: "Garen", icon: "/api/placeholder/32/32", games: 32, kda: 2.88 },
      { name: "Sett", icon: "/api/placeholder/32/32", games: 28, kda: 3.45 }
    ],
    reviews: {
      up: 45,
      down: 5
    },
    tags: ["친절한", "실력자", "긍정적"],
    tier: {
      name: "플래티넘 4",
      icon: "/api/placeholder/16/16"
    },
    winRate: 58,
    position: "top"
  },
  {
    id: 2,
    content: "정글 듀오 찾습니다~ 플레티넘 이하 편하게 신청해주세요. 탑 정글 듀오 환영! 즐겜러 환영합니다!",
    gameProfile: {
      name: "Jungle King",
      icon: "/api/placeholder/40/40"
    },
    champions: [
      { name: "Darius", icon: "/api/placeholder/32/32", games: 45, kda: 3.21 },
      { name: "Garen", icon: "/api/placeholder/32/32", games: 32, kda: 2.88 },
      { name: "Sett", icon: "/api/placeholder/32/32", games: 28, kda: 3.45 }
    ],
    reviews: {
      up: 32,
      down: 3
    },
    tags: ["소통왕", "메타픽", "적극적"],
    tier: {
      name: "골드 2",
      icon: "/api/placeholder/16/16"
    },
    winRate: 52,
    position: "jungle"
  }
])
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>