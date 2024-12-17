<template>
  <div
      class="animate-pulse"
      :class="rounded ? 'rounded-lg' : ''"
      :style="{
      width,
      height,
      backgroundColor: 'rgba(255, 255, 255, 0.06)'
    }"
  ></div>
</template>

<script setup lang="ts">
defineProps<{
  width?: string
  height?: string
  rounded?: boolean
}>()
</script>

<style scoped>
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: .5;
  }
}
</style>