<!-- src/components/summoner/DamageBar.vue -->
<template>
  <div class="damage-bar">
    <div class="progress-bg">
      <div class="progress-fill"
           :class="type"
           :style="{ width: percentage + '%' }">
      </div>
    </div>
    <span class="damage-text">{{ formatNumber(value) }}</span>
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
  align-items: center;
  gap: 6px;
  height: 16px;
}

.progress-bg {
  flex: 1;
  height: 4px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-fill.damage {
  background: #FF6B6B;
}

.progress-fill.taken {
  background: #339AF0;
}

.damage-text {
  min-width: 40px;
  text-align: right;
  font-size: 10px;
  color: #888;
}
</style>