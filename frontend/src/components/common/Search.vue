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
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const searchQuery = ref('');

const handleSearch = () => {
  const query = searchQuery.value.trim();
  if (query) {
    router.push(`/summoner/${query}`);
  }
};
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
</style>