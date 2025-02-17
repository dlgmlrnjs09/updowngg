<template>
  <div class="tooltip-wrapper">
    <HelpCircle
        class="help-icon"
        :size="iconSize"
        @mouseenter="showTooltip"
        @mouseleave="hideTooltip"
    />
    <div v-if="tooltipInfo.show"
         class="tooltip"
         :style="tooltipStyle">
      {{ message }}
      <div class="tooltip-arrow"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { HelpCircle } from 'lucide-vue-next'

interface Props {
  iconSize?: number
  message: string
}

const props = withDefaults(defineProps<Props>(), {
  iconSize: 14
})

const tooltipInfo = ref({
  show: false
})

const tooltipStyle = ref({})

const showTooltip = (event: MouseEvent) => {
  const target = event.currentTarget as HTMLElement
  const rect = target.getBoundingClientRect()

  tooltipStyle.value = {
    top: `${rect.top - 10}px`,
    left: `${rect.left + (rect.width / 2)}px`
  }

  tooltipInfo.value.show = true
}

const hideTooltip = () => {
  tooltipInfo.value.show = false
}
</script>

<style scoped>
.tooltip-wrapper {
  position: relative;
  display: inline-block;
}

.help-icon {
  color: #9e9e9e;
  cursor: help;
}

.tooltip {
  position: fixed;
  transform: translate(-50%, -120%);
  background: #323232;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
  padding: 6px 10px;
  color: white;
  font-size: 11px;
  border-radius: 4px;
  white-space: pre-line;
  min-width: 140px;
  max-width: 200px;
  z-index: 9999;
  pointer-events: none;
  line-height: 1.4;
}

.tooltip-arrow {
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%) rotate(45deg);
  width: 8px;
  height: 8px;
  background: #323232;
}

@media (max-width: 360px) {
  .tooltip {
    min-width: 120px;
    max-width: 180px;
    font-size: 10px;
    padding: 6px 8px;
  }
}
</style>