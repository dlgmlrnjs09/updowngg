<template>
  <div class="tags-wrapper">
    <div
        v-for="tag in tags"
        :key="tag.tagCode"
        class="tag"
        :class="[
            sizeClasses[size],
            tag.tagUpdown ? 'tag-up' : 'tag-down'
        ]"
    >
      <span
          class="tag-text"
          :class="{
          'tag-text-up': tag.tagUpdown,
          'tag-text-down': !tag.tagUpdown
        }"
      >
        {{ tag.tagValue }}</span>
      <span class="tag-count">{{ tag.frequentCount }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ReviewTagDto } from "@/types/review.ts";

defineProps<{
  tags: ReviewTagDto[] | null
  size: 'small' | 'medium' | 'large'
}>();

const sizeClasses = {
  small: 'px-2 py-1 text-xs',
  medium: 'px-3 py-1 text-sm',
  large: 'px-4 py-2 text-base',
};
</script>

<style scoped>
.tags-wrapper {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  /*padding-bottom: 4px;*/
  scrollbar-width: none;
}

.tags-wrapper::-webkit-scrollbar {
  display: none;
}

.tag {
  border-radius: 4px;
  /*padding: 6px 10px;*/
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.tag-up {
  background: rgba(41, 121, 255, 0.1);
}

.tag-down {
  background: rgba(235, 87, 87, 0.1);
}

.tag-text {
  font-size: 13px;
}

.tag-text-up {
  color: #2979FF;
}

.tag-text-down {
  color: #EB5757;
}

.tag-count {
  color: #9e9e9e;
  font-size: 12px;
}
</style>