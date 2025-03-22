<template>
  <div
      v-if="isVisible"
      class="tooltip"
      :style="positionStyle"
      :class="customClass"
  >
    <div v-if="title" class="tooltip-title">{{ title }}</div>
    <div class="tooltip-content">
      <slot>{{ content }}</slot>
    </div>
    <div class="tooltip-arrow"></div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  isVisible: boolean
  positionStyle: Record<string, string>
  content?: string
  title?: string
  customClass?: string
}>()
</script>

<style scoped>
.tooltip {
  position: fixed;
  transform: translate(-50%, -100%);
  background: rgb(50, 50, 50) !important;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
  padding: 8px 12px;
  color: white;
  font-size: 14px;
  border-radius: 4px;
  white-space: normal;
  min-width: 200px;
  max-width: 200px;
  z-index: 9999;
  pointer-events: none;
}

.tooltip-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  color: rgba(255, 215, 0, 0.9);
}

.tooltip-content {
  font-size: 12px;
  line-height: 1.4;
}

.tooltip-arrow {
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%) rotate(45deg);
  width: 8px;
  height: 8px;
  background: rgb(50, 50, 50) !important;
}
</style>