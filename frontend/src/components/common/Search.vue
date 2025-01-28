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
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { summonerApi } from '@/api/summoner'

const router = useRouter()
const searchQuery = ref('조용조용조용필#KR2')
const isLoading = ref(false)

const handleSearch = async () => {
  const query = searchQuery.value.trim()
  if (!query) return

  const [summonerId, tagLine] = query.split('#')

  try {
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

@keyframes spin {
  100% { transform: translateY(-50%) rotate(360deg); }
}
</style>