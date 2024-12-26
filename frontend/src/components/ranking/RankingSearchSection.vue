<template>
  <div class="filter-section">
    <div class="filter-controls">
      <select
          :value="selectedPeriod"
          @input="$emit('update:selectedPeriod', ($event.target as HTMLSelectElement).value)"
          class="period-select"
      >
        <option value="all">전체 기간</option>
        <option value="season">시즌</option>
        <option value="month">이번 달</option>
        <option value="week">이번 주</option>
      </select>
    </div>

    <div class="category-tabs">
      <button
          v-for="tab in positionTabs"
          :key="tab.value"
          class="tab-button"
          :class="{ active: selectedPosition === tab.value }"
          @click="$emit('update:selectedPosition', tab.value)"
      >
        <div class="tab-icon" v-if="tab.value !== 'all'">
          <img :src="tab.iconPath" :alt="tab.label">
        </div>
        {{ tab.label }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Tab } from '@/types/ranking'
import topIcon from '@/assets/icon/position/position_top.svg'
import jungleIcon from '@/assets/icon/position/position_jungle.svg'
import midIcon from '@/assets/icon/position/position_mid.svg'
import adIcon from '@/assets/icon/position/position_ad.svg'
import supportIcon from '@/assets/icon/position/position_support.svg'

defineProps<{
  selectedPeriod: string
  selectedPosition: string
}>()

defineEmits<{
  'update:selectedPeriod': [value: string]
  'update:selectedPosition': [value: string]
}>()

const positionTabs: Tab[] = [
  { label: '전체', value: 'all', iconPath: '' },
  { label: '탑', value: 'top', iconPath: topIcon },
  { label: '정글', value: 'jungle', iconPath: jungleIcon },
  { label: '미드', value: 'mid', iconPath: midIcon },
  { label: '원딜', value: 'ad', iconPath: adIcon },
  { label: '서폿', value: 'support', iconPath: supportIcon }
]
</script>

<style scoped>
.filter-section {
  background: #141414;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.period-select {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
}

.category-tabs {
  display: flex;
  gap: 8px;
}

.tab-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: #888;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-button:hover {
  background: rgba(41, 121, 255, 0.1);
  border-color: rgba(41, 121, 255, 0.2);
  color: white;
}

.tab-button.active {
  background: rgba(41, 121, 255, 0.2);
  border-color: #2979FF;
  color: white;
}

.tab-icon {
  width: 16px;
  height: 16px;
}

@media (max-width: 768px) {
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }

  .category-tabs {
    overflow-x: auto;
    padding-bottom: 8px;
  }
}
</style>