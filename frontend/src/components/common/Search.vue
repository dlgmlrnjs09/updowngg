<!-- src/components/common/Search.vue -->
<template>
  <div class="search-box">
    <input
        type="text"
        class="search-input"
        placeholder="소환사 이름을 입력하세요"
        v-model="searchQuery"
        @keyup.enter="handleSearch"
    >
    <div class="search-icon" @click="handleSearch">
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
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { summonerApi } from '@/api/summoner'

const router = useRouter()
const searchQuery = ref('')
const isLoading = ref(false)

const handleSearch = async () => {
  const query = searchQuery.value.trim()
  if (!query) return

  // '#' 기준으로 소환사 이름과 태그 분리
  const [summonerId, tagLine] = query.split('#')
  if (!summonerId || !tagLine) {
    alert('소환사#태그 형식으로 입력해주세요')
    return
  }

  try {
    isLoading.value = true
    const response = await summonerApi.getInfo(summonerId, tagLine)

    // 검색 결과가 있으면 소환사 페이지로 이동
    if (response.data) {
      router.push({
        name: 'summoner',
        params: {
          name: summonerId,
          tag: tagLine
        },
        // 소환사 정보를 state로 전달
        state: {
          summonerInfo: response.data
        }
      })
    }
  } catch (error) {
    console.error('Failed to fetch summoner:', error)
    alert('소환사를 찾을 수 없습니다')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.search-box {
  position: relative;
  width: 100%;
}

.search-input {
  width: 100%;
  padding: 16px 20px;
  font-size: 16px;
  background: #141414;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
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
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #888;
  cursor: pointer;
  transition: color 0.2s;
  padding: 8px;
}

.search-icon:hover {
  color: #2979FF;
}

.search-icon svg {
  width: 20px;
  height: 20px;
}

.search-box.loading .search-input {
  background-color: rgba(20, 20, 20, 0.8);
}

.search-box.loading .search-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  100% { transform: translateY(-50%) rotate(360deg); }
}
</style>