<template>
  <div class="search-container relative">
    <div class="search-box">
      <input
          type="text"
          class="search-input"
          placeholder="소환사 이름을 입력하세요"
          v-model="searchQuery"
          @keyup.enter="handleSearch"
          @input="handleInput"
          @keydown.down="handleKeyNavigation(1)"
          @keydown.up="handleKeyNavigation(-1)"
      >
      <div class="search-icon" v-throttle-click:1000="handleSearch">
        <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
        >
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
      </div>
    </div>

    <!-- 자동완성 목록 -->
    <div
        v-if="suggestions.length > 0"
        class="suggestions-container" ref="searchContainerRef"
    >
      <div
          v-for="(summoner, index) in suggestions"
          :key="index"
          class="suggestion-item"
          :class="{ 'selected': selectedIndex === index }"
          @click="selectSuggestion(summoner)"
          @mouseenter="selectedIndex = index"
      >
        <div class="flex items-center gap-3">
          <img
              :src="summoner.profileIconUrl"
              :alt="summoner.gameName"
              class="w-8 h-8 rounded-full"
          />
          <div class="flex flex-col">
            <div class="flex items-center gap-1">
              <span class="font-medium">{{ summoner.gameName }}</span>
              <span class="text-gray-400 text-sm">#{{ summoner.tagLine }}</span>
            </div>
            <div class="flex items-center gap-3 text-sm">
              <div class="flex items-center gap-1">
                <ThumbsUp class="w-4 h-4 text-green-500" />
                <span class="text-green-500">{{ summoner.upCount }}</span>
              </div>
              <div class="flex items-center gap-1">
                <ThumbsDown class="w-4 h-4 text-red-500" />
                <span class="text-red-500">{{ summoner.downCount }}</span>
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
import type {SearchSummonerDto} from "@/types/search.ts";
import {searchApi} from "@/api/search.ts";
import {ThumbsUp, ThumbsDown} from "lucide-vue-next";

const router = useRouter()
const searchQuery = ref('')
const isLoading = ref(false)
const suggestions = ref<SearchSummonerDto[]>([])
const selectedIndex = ref(-1)
const searchContainerRef = ref<HTMLElement | null>(null)

// 검색어 입력 시 자동완성 API 호출
const fetchSuggestions = debounce(async (query: string) => {
  if (!query) {
    suggestions.value = []
    return
  }

  try {
    const response = await searchApi.searchSummoner(query)
    suggestions.value = response.data
  } catch (error) {
    console.error('Failed to fetch suggestions:', error)
    suggestions.value = []
  }
}, 100)

const handleInput = () => {
  selectedIndex.value = -1
  fetchSuggestions(searchQuery.value)
}

const selectSuggestion = (summoner: SearchSummonerDto) => {
  searchQuery.value = `${summoner.gameName}#${summoner.tagLine}`
  suggestions.value = []
  handleSearch()
}

const handleKeyNavigation = (direction: number) => {
  if (suggestions.value.length === 0) return

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
  const query = searchQuery.value.trim()
  if (!query) return

  const [summonerId, tagLine] = query.split('#')

  isLoading.value = true
  const response = await summonerApi.getInfoBySummonerId(summonerId, tagLine)

  if (response.data) {
    router.push({
      name: 'summoner',
      params: {
        name: summonerId,
        tag: tagLine
      },
    })
  }
  isLoading.value = false
}

const handleClickOutside = (event: MouseEvent) => {
  if (
      searchContainerRef.value &&
      !searchContainerRef.value.contains(event.target as Node)
  ) {
    suggestions.value = []
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
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