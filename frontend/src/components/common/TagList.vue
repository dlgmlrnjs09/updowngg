<template>
  <div v-if="tags && tags.length > 0 && tags[0].tagValue" class="tags-wrapper" :class="{
    'flex-wrap': isArrangeRow
  }">
    <div
        v-for="tag in tags"
        :key="tag.tagCode"
        class="tag relative"
        :class="[
          sizeClasses[size],
          tag.tagUpdown ? 'tag-up' : 'tag-down',
          {
            'clickable': isClickable,
            'selected': isClickable && selectedTags?.includes(tag.tagCode)
          }
        ]"
        @mouseenter="handleMouseEnter($event, tag)"
        @mouseleave="hoveredTag = null"
        @click="isClickable && $emit('tagClick', tag.tagCode)"
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

      <Tooltip
          :is-visible="tag === hoveredTag"
          :position-style="tooltipStyle"
          :content="tag.tagDescription"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { ReviewTagDto } from "@/types/review"
import Tooltip from '@/components/common/Tooltip.vue'  // Tooltip 컴포넌트 import

defineProps<{
  tags: ReviewTagDto[]
  size: 'xxSmallReactive' | 'xSmall' | 'small' | 'medium' | 'large'
  isShowCount: boolean
  isArrangeRow?: boolean
  isClickable?: boolean
  selectedTags?: string[]
}>()

const emit = defineEmits<{
  (e: 'tagClick', tagCode: string): void
}>()

const hoveredTag = ref<ReviewTagDto | null>(null)
const tooltipStyle = ref({})

const sizeClasses = {
  xxSmallReactive: 'text-[9px] sm:text-[9px] md:text-[10px] px-1 py-0.5 rounded',
  xSmall: 'text-[10px] px-2 py-0.5',
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
  display: flex;
  gap: 8px;
  overflow-x: auto;
  scrollbar-width: none;
  position: relative;
}

.tags-wrapper::-webkit-scrollbar {
  display: none;
}

.tag {
  position: relative;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
  z-index: 1;
  transition: all 0.2s ease;
}

.tag:hover {
  z-index: 20;
}

.tag.clickable {
  cursor: pointer;
}

.tag-up {
  background: rgba(41, 121, 255, 0.1);
  border: 1px solid rgba(41, 121, 255, 0.1);
}

.tag-down {
  background: rgba(235, 87, 87, 0.1);
  border: 1px solid rgba(235, 87, 87, 0.1);
}

.tag.clickable.tag-up:hover:not(.selected) {
  background: rgba(41, 121, 255, 0.15);
  border-color: rgba(41, 121, 255, 0.3);
}

.tag.clickable.tag-down:hover:not(.selected) {
  background: rgba(235, 87, 87, 0.15);
  border-color: rgba(235, 87, 87, 0.3);
}

.tag-text-up {
  color: #2979FF;
}

.tag-text-down {
  color: #EB5757;
}

.tag.selected.tag-up {
  background: rgba(41, 121, 255, 0.25) !important;
  border-color: #2979FF !important;
}

.tag.selected.tag-down {
  background: rgba(235, 87, 87, 0.25) !important;
  border-color: #EB5757 !important;
}

.tag-count {
  color: #9e9e9e;
  font-size: 11px;
}

.tag:not(.clickable) {
  cursor: default;
}

.flex-wrap {
  flex-wrap: wrap;
}
</style>