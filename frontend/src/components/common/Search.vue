<template>
  <div class="search-container relative">
    <div class="search-box" @click="showSearchList">
      <input
          type="text"
          class="search-input"
          placeholder="소환사 이름을 입력하세요 (플레이어 이름#KR1)"
          v-model="searchQuery"
          @keyup.enter="handleSearch"
          @input="handleInput"
          @keydown.down="handleKeyNavigation(1)"
          @keydown.up="handleKeyNavigation(-1)"
          @focus="handleFocus"
          @blur="handleBlur"
          ref="searchInputRef"
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

    <!-- 검색어가 없을 때는 최근 검색 목록을, 있을 때는 자동완성 목록을 표시 -->
    <div
        v-if="showList && ((searchQuery === '' && recentSummoners.length > 0) || suggestions.length > 0)"
        class="suggestions-container"
        ref="searchContainerRef"
    >
      <!-- 검색어가 없을 때 최근 검색 목록 표시 -->
      <template v-if="searchQuery === ''">
        <div class="recent-searches-header">
          <span class="header-text">최근 검색</span>
          <button @click="clearRecentSearches" class="clear-button">전체 삭제</button>
        </div>
        <div
            v-for="(summoner, index) in recentSummoners"
            :key="index"
            class="suggestion-item"
            @click="selectRecentSummoner(summoner)"
        >
          <div class="flex items-center justify-between w-full">
            <div class="flex items-center gap-1">
              <span class="font-medium">{{ summoner.gameName }}</span>
              <span class="text-gray-400 text-sm">#{{ summoner.tagLine }}</span>
            </div>
            <button
                @click.stop="removeRecentSummoner(index)"
                class="delete-button"
            >
              <XCircle class="w-4 h-4" />
            </button>
          </div>
        </div>
      </template>

      <!-- 검색어가 있을 때 자동완성 목록 표시 -->
      <template v-else>
        <div
            v-for="(summoner, index) in suggestions"
            :key="index"
            class="suggestion-item"
            :class="{ 'selected': selectedIndex === index }"
            @click="selectSuggestion(summoner)"
            @mouseenter="selectedIndex = index"
        >
          <div class="flex items-center justify-between w-full">
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
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { summonerApi } from '@/api/summoner'
import debounce from 'lodash/debounce'
import type { SearchSummonerDto } from "@/types/search.ts"
import { searchApi } from "@/api/search.ts"
import { ThumbsUp, ThumbsDown, XCircle } from "lucide-vue-next"
import { cookieUtils, type RecentSummoner } from '@/utils/cookieUtil.ts'
import { useToast } from "vue-toastification"

const router = useRouter()
const toast = useToast()
const searchQuery = ref('')
const isLoading = ref(false)
const suggestions = ref<SearchSummonerDto[]>([])
const selectedIndex = ref(-1)
const searchContainerRef = ref<HTMLElement | null>(null)
const isComponentMounted = ref(true)
const recentSummoners = ref<RecentSummoner[]>([])
const showList = ref(false)
const searchInputRef = ref<HTMLInputElement | null>(null);
let blurTimeout: number | null = null;

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

const clearRecentSearches = () => {
  localStorage.removeItem('recent_summoners');
  recentSummoners.value = [];
}

const removeRecentSummoner = (index: number) => {
  recentSummoners.value.splice(index, 1);
  localStorage.setItem('recent_summoners', JSON.stringify(recentSummoners.value));
}

const selectSuggestion = (summoner: SearchSummonerDto) => {
  if (isComponentMounted.value) {
    searchQuery.value = `${summoner.gameName}#${summoner.tagLine}`
    suggestions.value = []
    showList.value = false;
    handleSearch()
  }
}

const selectRecentSummoner = (summoner: RecentSummoner) => {
  if (isComponentMounted.value) {
    searchQuery.value = `${summoner.gameName}#${summoner.tagLine}`
    showList.value = false;
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
  if (!summonerId || !tagLine) {
    toast.error('올바른 소환사 이름을 입력해주세요. (플레이어 이름#KR1)');
    return;
  }

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
    toast.error('소환사를 찾을 수 없습니다.');
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
      !searchContainerRef.value.contains(event.target as Node) &&
      !searchInputRef.value?.contains(event.target as Node)
  ) {
    showList.value = false;
    suggestions.value = [];
  }
};

const showSearchList = () => {
  showList.value = true;
  // 입력창에 포커스 주기
  searchInputRef.value?.focus();
  if (searchQuery.value === '') {
    recentSummoners.value = cookieUtils.getRecentSummoners();
  }
};

const handleFocus = () => {
  // blur 타임아웃이 있다면 취소
  if (blurTimeout) {
    window.clearTimeout(blurTimeout);
    blurTimeout = null;
  }
  showSearchList();
};

const handleBlur = () => {
  // 약간의 지연을 두어 클릭 이벤트가 처리될 수 있도록 함
  blurTimeout = window.setTimeout(() => {
    if (!isComponentMounted.value) return;
    showList.value = false;
    suggestions.value = [];
  }, 200);
};

onMounted(() => {
  if (blurTimeout) {
    window.clearTimeout(blurTimeout);
  }
  document.addEventListener('click', handleClickOutside)
  isComponentMounted.value = true
  recentSummoners.value = cookieUtils.getRecentSummoners()
})

onUnmounted(() => {
  isComponentMounted.value = false
  debouncedFetch.cancel()
  document.removeEventListener('click', handleClickOutside)
  suggestions.value = []
  searchContainerRef.value = null
  searchQuery.value = ''
  selectedIndex.value = -1
  isLoading.value = false
  showList.value = false
})
</script>

<style scoped>
.search-container {
  position: relative;
  width: 100%;
}

.search-box {
  position: relative;
  width: 100%;
}

.search-input {
  width: 100%;
  padding: 8px 16px;
  font-size: 14px;
  background: #ffffff30;
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

.suggestion-item .font-medium {
  font-size: 14px;         /* 소환사 이름 크기 */
  color: #fff;             /* 소환사 이름 색상 */
  font-weight: 500;        /* 소환사 이름 두께 */
}

.recent-searches-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
}

.header-text {
  font-size: 13px;  /* 글자 크기 조정 */
  color: #999;      /* 글자 색상을 더 연하게 */
  font-weight: 600; /* 약간의 두께 */
  text-transform: uppercase; /* 대문자로 변경 */
  letter-spacing: 0.5px;    /* 자간 추가 */
}

.clear-button {
  color: #666;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.clear-button:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.delete-button {
  color: #666;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.delete-button:hover {
  color: #ff4444;
  background: rgba(255, 255, 255, 0.1);
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