<template>
  <div class="min-h-screen bg-[#0A0A0A] p-6 pt-24">
    <div class="max-w-[1000px] mx-auto">
      <!-- Header -->
      <div class="flex justify-between items-center mb-8">
        <h1 class="text-2xl font-bold text-white">평가 내역</h1>
        <div class="flex gap-4">
          <button
              @click="activeTab = 'written'"
              :class="[
             'px-4 py-2 rounded-lg text-sm transition-colors',
             activeTab === 'written'
               ? 'bg-[#2979FF] text-white'
               : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
           ]"
          >작성한 리뷰</button>
          <button
              @click="activeTab = 'received'"
              :class="[
             'px-4 py-2 rounded-lg text-sm transition-colors',
             activeTab === 'received'
               ? 'bg-[#2979FF] text-white'
               : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
           ]"
          >받은 리뷰</button>
        </div>
      </div>

      <!-- Reviews List -->
      <div class="space-y-4">
        <div v-for="(review, index) in reviews"
             :key="index"
             class="bg-[#141414] rounded-xl border border-[#ffffff1a] p-4">
          <div class="flex gap-6 items-center">
            <!-- Game Info -->
            <div class="min-w-[130px]">
              <div class="text-white text-sm font-medium">{{ review.gameMode }}</div>
              <div class="text-gray-400 text-xs mt-1">{{ review.timeAgo }}</div>
              <div class="text-gray-400 text-xs">{{ review.duration }}</div>
            </div>

            <!-- Champion Icon & Level -->
            <div class="relative flex-shrink-0  mr-[20px]">
              <div class="w-12 h-12 bg-[#1a1a1a] rounded-lg overflow-hidden">
                <img :src="review.championIcon" :alt="review.championName" class="w-full h-full"/>
              </div>
              <div class="absolute -bottom-1 -right-1 bg-[#1a1a1a] w-5 h-5 rounded-full flex items-center justify-center text-xs text-gray-300">
                {{ review.level }}
              </div>
            </div>

            <!-- Review Content -->
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <component
                    :is="review.isPositive ? ThumbsUp : ThumbsDown"
                    class="w-4 h-4"
                    :class="review.isPositive ? 'text-[#4CAF50]' : 'text-[#FF5252]'"
                />
                <span class="text-white text-sm">{{ review.reviewerName }}</span>
                <span class="text-gray-400 text-xs">#{{ review.reviewerTag }}</span>
              </div>
              <div class="flex flex-wrap gap-2 mb-2">
               <span v-for="tag in review.tags"
                     :key="tag"
                     :class="[
                       'px-2 py-0.5 text-xs rounded-md',
                       review.isPositive
                         ? 'bg-[#4CAF501A] text-[#4CAF50]'
                         : 'bg-[#FF52521A] text-[#FF5252]'
                     ]"
               >{{ tag }}</span>
              </div>
              <p class="text-gray-300 text-sm">{{ review.comment }}</p>
            </div>

            <!-- Players Grid -->
            <div class="grid grid-cols-2 gap-x-4 gap-y-1 min-w-[200px]">
              <div v-for="player in review.players"
                   :key="player.name"
                   class="flex items-center gap-1.5">
                <img :src="player.iconUrl" alt="Profile" class="w-5 h-5 rounded-full"/>
                <div class="text-sm text-gray-400 truncate">{{ player.name }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div class="flex justify-center mt-6 gap-2">
        <button v-for="page in totalPages"
                :key="page"
                @click="currentPage = page"
                :class="[
                 'w-8 h-8 rounded-lg text-sm font-medium transition-colors',
                 currentPage === page
                   ? 'bg-[#2979FF] text-white'
                   : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
               ]"
        >{{ page }}</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ThumbsUp, ThumbsDown } from 'lucide-vue-next'

const activeTab = ref('written')
const currentPage = ref(1)
const totalPages = ref(5)

const reviews = ref([
  {
    gameMode: '개인/2인 랭크 게임',
    timeAgo: '17분 전',
    duration: '4.00:1 평점',
    championIcon: '/api/placeholder/48/48',
    championName: 'Ahri',
    level: 15,
    reviewerName: 'Hide on bush',
    reviewerTag: 'KR1',
    isPositive: true,
    tags: ['라인전 압도적', 'CS 관리 우수'],
    comment: '라인전부터 한번도 밀리지 않고 팀에도 도움을 많이 주셨습니다. 매너도 좋으시고 팀원들과의 소통도 원활했어요! 다음에도 같이 게임하고 싶네요.',
    players: [
      { iconUrl: '/api/placeholder/20/20', name: 'HLE Zeus' },
      { iconUrl: '/api/placeholder/20/20', name: '귀멸의칼날' },
      { iconUrl: '/api/placeholder/20/20', name: '조선의검' },
      { iconUrl: '/api/placeholder/20/20', name: 'Reve' },
      { iconUrl: '/api/placeholder/20/20', name: '한국 서폿' },
      { iconUrl: '/api/placeholder/20/20', name: 'JG gap' },
      { iconUrl: '/api/placeholder/20/20', name: '미드차이' },
      { iconUrl: '/api/placeholder/20/20', name: '정글이왕' },
      { iconUrl: '/api/placeholder/20/20', name: 'ADC 킹' },
      { iconUrl: '/api/placeholder/20/20', name: '서폿장인' }
    ]
  },
  {
    gameMode: '개인/2인 랭크 게임',
    timeAgo: '35분 전',
    duration: '3.50:1 평점',
    championIcon: '/api/placeholder/48/48',
    championName: 'Yasuo',
    level: 14,
    reviewerName: 'Faker',
    reviewerTag: 'T1',
    isPositive: false,
    tags: ['라인전 불안정', '갱 대처 미숙'],
    comment: '초반에 갱을 많이 당했는데 대처가 아쉬웠어요. 와드 위치나 라인 관리에 좀 더 신경 쓰면 좋을 것 같습니다.',
    players: [
      { iconUrl: '/api/placeholder/20/20', name: 'T1 Faker' },
      { iconUrl: '/api/placeholder/20/20', name: 'Canyon' },
      { iconUrl: '/api/placeholder/20/20', name: 'Chovy' },
      { iconUrl: '/api/placeholder/20/20', name: 'Deft' },
      { iconUrl: '/api/placeholder/20/20', name: 'Keria' },
      { iconUrl: '/api/placeholder/20/20', name: 'Rookie' },
      { iconUrl: '/api/placeholder/20/20', name: 'Kanavi' },
      { iconUrl: '/api/placeholder/20/20', name: 'Scout' },
      { iconUrl: '/api/placeholder/20/20', name: 'Ruler' },
      { iconUrl: '/api/placeholder/20/20', name: 'Ming' }
    ]
  }
])
</script>