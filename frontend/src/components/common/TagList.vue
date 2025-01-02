<template>
  <div class="tags-wrapper">
    <div
        v-for="tag in tags"
        :key="tag.tagCode"
        class="tag relative"
        :class="[sizeClasses[size], tag.tagUpdown ? 'tag-up' : 'tag-down']"
        @mouseenter="handleMouseEnter($event, tag)"
        @mouseleave="hoveredTag = null"
    >
     <span
         class="tag-text"
         :class="{
         'tag-text-up': tag.tagUpdown,
         'tag-text-down': !tag.tagUpdown
       }"
     >
       {{ tag.tagValue }}
     </span>
      <span class="tag-count" v-if="isShowCount">{{ tag.frequentCount }}</span>

      <div
          v-if="tag === hoveredTag"
          class="tooltip"
          :style="tooltipStyle"
      >
        {{ tag.tagDescription }}
        <div class="tooltip-arrow"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { ReviewTagDto } from "@/types/review"

defineProps<{
  tags: ReviewTagDto[] | null
  size: 'small' | 'medium' | 'large'
  isShowCount: boolean
}>()

const hoveredTag = ref<ReviewTagDto | null>(null)
const tooltipStyle = ref({})

const sizeClasses = {
  small: 'px-2 py-1 text-xs',
  medium: 'px-3 py-1 text-sm',
  large: 'px-4 py-2 text-base'
}

const handleMouseEnter = (event: MouseEvent, tag: ReviewTagDto) => {
  hoveredTag.value = tag
  const target = event.currentTarget as HTMLElement
  const rect = target.getBoundingClientRect()

  tooltipStyle.value = {
    top: `${rect.top - 10}px`,
    left: `${rect.left + (rect.width / 2)}px`
  }
}
</script>

<style scoped>
.tags-wrapper {
  display: flex ;
  gap: 8px ;
  overflow-x: auto ;
  scrollbar-width: none ;
  position: relative ;
}

.tags-wrapper::-webkit-scrollbar {
  display: none ;
}

.tag {
  position: relative ;
  border-radius: 4px ;
  display: flex ;
  align-items: center ;
  gap: 6px ;
  white-space: nowrap ;
  cursor: pointer ;
  z-index: 1 ;
}

.tag:hover {
  z-index: 20 ;
}

.tag-up {
  background: rgba(41, 121, 255, 0.1) ;
}

.tag-down {
  background: rgba(235, 87, 87, 0.1) ;
}

.tag-text-up {
  color: #2979FF ;
}

.tag-text-down {
  color: #EB5757 ;
}

.tag-count {
  color: #9e9e9e ;
  font-size: 12px ;
}

.tooltip {
  position: fixed ;
  transform: translate(-50%, -100%) ;
  background: #323232 ;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3) ;
  padding: 8px 12px ;
  color: white ;
  font-size: 14px ;
  border-radius: 4px ;
  white-space: normal ;
  min-width: 200px ;
  max-width: 300px ;
  z-index: 9999 ;
  pointer-events: none ;
}

.tooltip-arrow {
  position: absolute ;
  bottom: -4px ;
  left: 50% ;
  transform: translateX(-50%) rotate(45deg) ;
  width: 8px ;
  height: 8px ;
  background: #323232 ;
}
</style>