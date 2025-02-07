<template>
  <div class="search-container relative" data-v-inspector="src/components/common/Search.vue:2:3">
    <div class="search-box" data-v-inspector="src/components/common/Search.vue:3:5">
      <input
          type="text"
          class="search-input"
          placeholder="소환사 이름을 입력하세요"
          v-model="searchQuery"
          @keyup.enter="handleSearch"
          @input="handleInput"
          @keydown.down="handleKeyNavigation(1)"
          @keydown.up="handleKeyNavigation(-1)"
          data-v-inspector="src/components/common/Search.vue:4:7"
      >
      <div class="search-icon" v-throttle-click:1000="handleSearch" data-v-inspector="src/components/common/Search.vue:14:7">
        <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            data-v-inspector="src/components/common/Search.vue:15:9"
        >
          <circle cx="11" cy="11" r="8" data-v-inspector="src/components/common/Search.vue:24:11"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65" data-v-inspector="src/components/common/Search.vue:25:11"></line>
        </svg>
      </div>
    </div>

    <!-- 자동완성 목록 -->
    <div
        v-if="suggestions.length > 0"
        class="suggestions-container"
        ref="searchContainerRef"
        data-v-inspector="src/components/common/Search.vue:31:5"
    >
      <div
          v-for="(summoner, index) in suggestions"
          :key="index"
          class="suggestion-item"
          :class="{ 'selected': selectedIndex === index }"
          @click="selectSuggestion(summoner)"
          @mouseenter="selectedIndex = index"
          data-v-inspector="src/components/common/Search.vue:35:7"
      >
        <div class="flex items-center gap-3" data-v-inspector="src/components/common/Search.vue:43:9">
          <img
              :src="summoner.profileIconUrl"
              :alt="summoner.gameName"
              class="w-8 h-8 rounded-full"
              data-v-inspector="src/components/common/Search.vue:44:11"
          />
          <div class="flex flex-col" data-v-inspector="src/components/common/Search.vue:49:11">
            <div class="flex items-center gap-1" data-v-inspector="src/components/common/Search.vue:50:13">
              <span class="font-medium" data-v-inspector="src/components/common/Search.vue:51:15">{{ summoner.gameName }}</span>
              <span class="text-gray-400 text-sm" data-v-inspector="src/components/common/Search.vue:52:15">#{{ summoner.tagLine }}</span>
            </div>
            <div class="flex items-center gap-3 text-sm" data-v-inspector="src/components/common/Search.vue:54:13">
              <div class="flex items-center gap-1" data-v-inspector="src/components/common/Search.vue:55:15">
                <ThumbsUp class="w-4 h-4 text-green-500" data-v-inspector="src/components/common/Search.vue:56:17" />
                <span class="text-green-500" data-v-inspector="src/components/common/Search.vue:57:17">{{ summoner.upCount }}</span>
              </div>
              <div class="flex items-center gap-1" data-v-inspector="src/components/common/Search.vue:59:15">
                <ThumbsDown class="w-4 h-4 text-red-500" data-v-inspector="src/components/common/Search.vue:60:17" />
                <span class="text-red-500" data-v-inspector="src/components/common/Search.vue:61:17">{{ summoner.downCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { summonerApi } from '@/api/summoner'
import debounce from 'lodash/debounce'
import type { SearchSummonerDto } from "@/types/search.ts"
import { searchApi } from "@/api/search.ts"
import { ThumbsUp, ThumbsDown } from "lucide-vue-next"

const router = useRouter()
const searchQuery = ref('')
const isLoading = ref(false)
const suggestions = ref<SearchSummonerDto[]>([])
const selectedIndex = ref(-1)
const searchContainerRef = ref<HTMLElement | null>(null)
const isComponentMounted = ref(true)

// 검색어 입력 시 자동완성 API 호출을 위한 debounce 함수
const debouncedFetch = debounce(async (query: string) => {
  if (!query || !isComponentMounted.value) {
    suggestions.value = []
    return
  }

  try {
    const response = await searchApi.searchSummoner(query)
    if (isComponentMounted.value) {
      suggestions.value = response.data
    }
  } catch (error) {
    console.error('Failed to fetch suggestions:', error)
    if (isComponentMounted.value) {
      suggestions.value = []
    }
  }
}, 100)

const handleInput = () => {
  if (isComponentMounted.value) {
    selectedIndex.value = -1
    debouncedFetch(searchQuery.value)
  }
}

const selectSuggestion = (summoner: SearchSummonerDto) => {
  if (isComponentMounted.value) {
    searchQuery.value = `${summoner.gameName}#${summoner.tagLine}`
    suggestions.value = []
    handleSearch()
  }
}

const handleKeyNavigation = (direction: number) => {
  if (!isComponentMounted.value || suggestions.value.length === 0) return

  const newIndex = selectedIndex.value + direction
  if (newIndex >= -1 && newIndex < suggestions.value.length) {
    selectedIndex.value = newIndex
  }

  if (selectedIndex.value !== -1) {
    const selected = suggestions.value[selectedIndex.value]
    searchQuery.value = `${selected.gameName}#${selected.tagLine}`
  }
}

const handleSearch = async () => {
  if (!isComponentMounted.value) return

  const query = searchQuery.value.trim()
  if (!query) return

  const [summonerId, tagLine] = query.split('#')

  try {
    isLoading.value = true
    const response = await summonerApi.getInfoBySummonerId(summonerId, tagLine)

    if (response.data && isComponentMounted.value) {
      router.push({
        name: 'summoner',
        params: {
          name: summonerId,
          tag: tagLine
        },
      })
    }
  } catch (error) {
    console.error('Failed to search summoner:', error)
  } finally {
    if (isComponentMounted.value) {
      isLoading.value = false
    }
  }
}

const handleClickOutside = (event: MouseEvent) => {
  if (
      isComponentMounted.value &&
      searchContainerRef.value &&
      !searchContainerRef.value.contains(event.target as Node)
  ) {
    suggestions.value = []
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  isComponentMounted.value = true
})

onUnmounted(() => {
  isComponentMounted.value = false
  // debounce 함수 취소
  debouncedFetch.cancel()
  // 이벤트 리스너 제거
  document.removeEventListener('click', handleClickOutside)
  // ref 정리
  suggestions.value = []
  searchContainerRef.value = null
  searchQuery.value = ''
  selectedIndex.value = -1
  isLoading.value = false
})
</script>

<style scoped>
.search-box {
  position: relative;
  width: 100%;
}

.search-input {
  width: 100%;
  padding: 8px 16px;
  font-size: 14px;
  background: #141414;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: white;
  outline: none;
  transition: all 0.2s ease;
  font-weight: 500;
}

.search-input:focus {
  border-color: #2979FF;
  box-shadow: 0 0 0 4px rgba(41, 121, 255, 0.1);
}

.search-input::placeholder {
  color: #666;
  font-weight: 400;
}

.search-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #888;
  cursor: pointer;
  transition: color 0.2s;
  padding: 4px;
}

.search-icon:hover {
  color: #2979FF;
}

.search-icon svg {
  width: 16px;
  height: 16px;
}

.search-box.loading .search-input {
  background-color: rgba(20, 20, 20, 0.8);
}

.search-box.loading .search-icon {
  animation: spin 1s linear infinite;
}

.suggestions-container {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #1a1a1a;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  margin-top: 4px;
  max-height: 400px;
  overflow-y: auto;
  z-index: 50;
}

.suggestion-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.suggestion-item:hover,
.suggestion-item.selected {
  background: rgba(41, 121, 255, 0.1);
}

.suggestion-item:not(:last-child) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

/* 스크롤바 스타일링 */
.suggestions-container::-webkit-scrollbar {
  width: 6px;
}

.suggestions-container::-webkit-scrollbar-track {
  background: #1a1a1a;
}

.suggestions-container::-webkit-scrollbar-thumb {
  background: #333;
  border-radius: 3px;
}

.suggestions-container::-webkit-scrollbar-thumb:hover {
  background: #444;
}

@keyframes spin {
  100% { transform: translateY(-50%) rotate(360deg); }
}
</style>