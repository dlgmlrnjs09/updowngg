<!-- src/components/summoner/DamageBar.vue -->
<template>
  <div class="damage-bar">
    <div class="damage-label">{{ label }}</div>
    <div class="bar-container">
      <div class="progress-bg">
        <div class="progress-fill"
             :class="type"
             :style="{ width: percentage + '%' }">
        </div>
      </div>
      <span class="damage-text">{{ formatNumber(value) }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  value: number
  maxValue: number
  type: 'damage' | 'taken'
  label: string
}>()

const percentage = computed(() => (props.value / props.maxValue) * 100)

const formatNumber = (num: number) => {
  if (num >= 10000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toLocaleString()
}
</script>

<style scoped>
.damage-bar {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.damage-label {
  font-size: 11px;
  color: #666;
  margin-bottom: 2px;
}

.bar-container {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0; /* 추가 */
  flex: 1; /* 추가 */
  width: 100%; /* 추가 */
}

.progress-bg {
  flex: 1;
  height: 6px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.progress-fill.damage {
  background: linear-gradient(to right, #FF6B6B, #FF8787);
}

.progress-fill.taken {
  background: linear-gradient(to right, #339AF0, #4DABF7);
}

.damage-text {
  min-width: 45px;
  text-align: right;
  font-size: 11px;
  color: #888;
  font-weight: 500;
}

@media (max-width: 768px) {
  .damage-label {
    display: none;
  }
}
</style>