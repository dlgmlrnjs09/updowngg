# StatsPage.vue
<template>
  <div class="stats-page max-w-[1200px] mx-auto px-6 mt-[75px]">
    <!-- 포지션별 통계 -->
    <div class="stats-card">
      <h2 class="stats-title">포지션별 평가 통계</h2>
      <div class="grid grid-cols-5 gap-5">
        <div v-for="stat in positionStats" :key="stat.position" class="position-stat">
          <div class="position-icon">
            <img :src="`/src/assets/icon/position/position_${stat.position.toLowerCase()}.svg`" :alt="stat.position">
          </div>
          <div class="stat-scores">
            <div class="score-group">
              <ThumbsUp class="thumb-icon up" />
              <span class="up-count">{{ stat.likeRatio }}%</span>
            </div>
            <div class="score-group">
              <ThumbsDown class="thumb-icon down" />
              <span class="down-count">{{ stat.dislikeRatio }}%</span>
            </div>
          </div>
          <div class="tags">
            <span v-for="tag in stat.topTags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 챔피언별 포지션 평가 통계 -->
    <div class="stats-card">
      <h2 class="stats-title">챔피언별 포지션 평가 통계</h2>
      <div class="filter-section">
        <!-- 포지션 필터 -->
        <div class="filter-row">
          <div class="position-filters">
            <button v-for="pos in positions"
                    :key="pos"
                    :class="['position-btn', { active: selectedPosition === pos }]"
                    @click="selectedPosition = pos">
              <img :src="`/src/assets/icon/position/position_${pos.toLowerCase()}.svg`" :alt="pos">
            </button>
          </div>
          <!-- 티어 필터-->
          <div class="tier-filters">
            <button v-for="tier in tiers"
                    :key="tier"
                    :class="['tier-btn', { active: selectedTier === tier }]"
                    @click="selectedTier = tier">
              <img :src="`/src/assets/icon/tier/tier_${tier.toLowerCase()}.png`" :alt="tier">
            </button>
          </div>
        </div>



        <!-- 챔피언 검색 -->
        <div class="champion-search">
          <input type="text"
                 v-model="championSearch"
                 placeholder="챔피언 검색"
                 class="search-input">
          <div class="consonant-filters">
            <button v-for="consonant in consonants"
                    :key="consonant"
                    :class="['consonant-btn', { active: selectedConsonant === consonant }]"
                    @click="selectedConsonant = consonant === selectedConsonant ? '' : consonant">
              {{ consonant }}
            </button>
          </div>
        </div>

        <!-- 챔피언 그리드 -->
        <div class="champion-scroll">
          <div class="champion-grid">
            <button v-for="champion in championList"
                    :key="champion.nameUs"
                    :class="['champion-btn', { active: selectedChampion === champion.nameUs }]"
                    @click="selectedChampion = champion.nameUs">
              <img :src="champion.iconUrl" :alt="champion.iconUrl">
              <span class="champion-name">{{ champion.nameKr }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 시간대별 통계 -->
    <div class="stats-card">
      <h2 class="stats-title">시간대별 평가 통계</h2>
      <div class="h-96">
        <TimeChart :data="timeStats" />
      </div>
    </div>

    <!-- 티어별 평가 -->
<!--    <div class="stats-card">-->
<!--      <h2 class="stats-title">티어별 평가</h2>-->
<!--      <div class="grid grid-cols-3 gap-5">-->
<!--        <div v-for="tier in tierStats" :key="tier.name" class="tier-stat">-->
<!--          <div class="tier-header">-->
<!--            <div class="tier-icon">-->
<!--              <img :src="`/src/assets/icon/tier/tier_${tier.name.toLowerCase()}.png`" :alt="tier.name">-->
<!--            </div>-->
<!--            <div class="tier-scores">-->
<!--              <div class="score-group">-->
<!--                <ThumbsUp class="thumb-icon up" />-->
<!--                <span class="up-count">{{ tier.likeRatio }}%</span>-->
<!--              </div>-->
<!--              <div class="score-group">-->
<!--                <ThumbsDown class="thumb-icon down" />-->
<!--                <span class="down-count">{{ tier.dislikeRatio }}%</span>-->
<!--              </div>-->
<!--              <div class="review-count">평가 {{ tier.totalReviews }}회</div>-->
<!--            </div>-->
<!--          </div>-->
<!--          <div class="tags">-->
<!--            <span v-for="tag in tier.topTags" :key="tag.name" class="tag">-->
<!--              {{ tag.name }}-->
<!--              <span class="tag-count">{{ tag.count }}</span>-->
<!--            </span>-->
<!--          </div>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
  </div>
</template>

<script setup lang="ts">
import {ref, computed, onMounted} from 'vue'
import { ThumbsUp, ThumbsDown } from 'lucide-vue-next'
import TimeChart from '@/components/stats/TimeChart.vue'
import type {ChampionResDto} from "@/types/stats.ts";
import {statsApi} from "@/api/stats.ts";

// 예시 데이터
const positionStats = ref([
  { position: "TOP", likeRatio: 65, dislikeRatio: 35, topTags: ["cs압도", "1인분", "캐리"] },
  { position: "JUNGLE", likeRatio: 58, dislikeRatio: 42, topTags: ["오브젝트", "갱잘함"] },
  { position: "MID", likeRatio: 72, dislikeRatio: 28, topTags: ["cs압도", "캐리", "로밍"] },
  { position: "AD", likeRatio: 61, dislikeRatio: 39, topTags: ["포지션이해도", "cs압도"] },
  { position: "SUPPORT", likeRatio: 69, dislikeRatio: 31, topTags: ["비전", "라인전", "로밍"] }
])

const positions = ["TOP", "JUNGLE", "MID", "AD", "SUPPORT"]

const championList = ref<ChampionResDto[]>()

const tierStats = ref([
  {
    name: "IRON",
    likeRatio: 45,
    dislikeRatio: 55,
    totalReviews: 1200,
    topTags: [
      { name: "성장중", count: 350 },
      { name: "메카닉스부족", count: 280 },
      { name: "포지션이해도부족", count: 220 }
    ]
  },
  {
    name: "BRONZE",
    likeRatio: 52,
    dislikeRatio: 48,
    totalReviews: 2400,
    topTags: [
      { name: "성장중", count: 580 },
      { name: "라인전", count: 420 },
      { name: "메카닉스", count: 380 }
    ]
  },
  // ... SILVER, GOLD, PLATINUM, DIAMOND 데이터는 이미 정의되어 있음
])

onMounted(() =>{
  fetchChampions();
})

const fetchChampions = async () => {
  const response = await statsApi.getChampionList()
  championList.value = response.data;
}


const consonants = ['ㄱ', 'ㄴ', 'ㄷ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅅ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ']

const selectedPosition = ref("TOP")
const selectedChampion = ref(null)
const championSearch = ref('')
const selectedConsonant = ref('')

// 초성 구하기 함수
const getInitialConsonant = (str: string) => {
  const code = str.charCodeAt(0) - 0xAC00
  if (code > -1 && code < 11172) return consonants[Math.floor(code / 28 / 21)]
  return null
}

// 챔피언 필터링
const filteredChampions = computed(() => {
  let result = championList.value

  if (championSearch.value) {
    result = result.filter(champ =>
        champ.name.toLowerCase().includes(championSearch.value.toLowerCase())
    )
  }

  if (selectedConsonant.value) {
    result = result.filter(champ =>
        getInitialConsonant(champ.name) === selectedConsonant.value
    )
  }

  return result
})
</script>

<style scoped>
.stats-card {
  @apply bg-[#141414] rounded-xl border border-[#ffffff1a] p-6 mb-6;
}

.stats-title {
  @apply text-white text-xl font-semibold mb-5;
}

.position-stat {
  @apply bg-[#1a1a1a] rounded-lg p-4;
}

.position-icon {
  @apply w-[48px] h-[48px] mx-auto mb-4;
}

.position-icon img {
  @apply w-full h-full;
}

.stat-scores {
  @apply flex justify-center gap-4 mb-4;
}

.score-group {
  @apply flex items-center gap-2;
}

.thumb-icon {
  @apply w-4 h-4;
}

.thumb-icon.up {
  @apply text-[#4CAF50];
}

.thumb-icon.down {
  @apply text-[#FF5252];
}

.up-count {
  @apply text-[#4CAF50] font-semibold;
}

.down-count {
  @apply text-[#FF5252] font-semibold;
}

.tags {
  @apply flex flex-wrap gap-2 justify-center;
}

.tag {
  @apply bg-[#2979FF1A] text-[#2979FF] px-3 py-1 rounded text-sm;
}

.tag-count {
  @apply text-[#9e9e9e] ml-1;
}

/* 챔피언 선택 영역 */
.filter-section {
  @apply space-y-4;
}

.position-filters {
  @apply flex gap-3;
}

.position-btn {
  @apply w-10 h-10 bg-[#1a1a1a] rounded-lg p-2 transition-all;
}

.position-btn.active {
  @apply bg-[#2979FF1A] border border-[#2979FF];
}

.champion-search {
  @apply flex items-center gap-4;
}

.search-input {
  @apply bg-[#1a1a1a] border border-[#ffffff1a] text-white px-4 py-2 rounded-lg w-60;
}

.consonant-filters {
  @apply flex gap-2;
}

.consonant-btn {
  @apply px-3 py-1 bg-[#1a1a1a] text-[#666] rounded transition-all text-sm;
}

.consonant-btn.active {
  @apply bg-[#2979FF1A] text-[#2979FF];
}

.champion-scroll {
  @apply h-[400px] overflow-y-auto bg-[#1a1a1a] rounded-lg p-4;
}

.champion-grid {
  @apply grid grid-cols-10 gap-2;
}

.champion-btn {
  @apply relative bg-[#1a1a1a] rounded-lg overflow-hidden transition-all;
}

.champion-btn.active {
  @apply border-2 border-[#2979FF];
}

.champion-btn img {
  @apply w-full aspect-square object-cover;
}

.champion-name {
  @apply absolute bottom-0 left-0 right-0 bg-black/50 text-white text-xs py-1 text-center;
}

/* 티어 영역 */
.tier-stat {
  @apply bg-[#1a1a1a] rounded-lg p-4;
}

.tier-header {
  @apply flex gap-4 mb-4;
}

.tier-icon {
  @apply w-16 h-16 rounded-full overflow-hidden;
}

.tier-scores {
  @apply flex-1 space-y-2;
}

.review-count {
  @apply text-[#666] text-sm;
}

/* 스크롤바 스타일 */
.champion-scroll::-webkit-scrollbar {
  @apply w-2;
}

.champion-scroll::-webkit-scrollbar-track {
  @apply bg-[#1a1a1a] rounded-full;
}

.champion-scroll::-webkit-scrollbar-thumb {
  @apply bg-[#333] rounded-full;
}

@media (max-width: 768px) {
  .champion-grid {
    @apply grid-cols-5;
  }
}
</style>