// components/skeletons/MatchTeamSkeleton.vue
<template>
  <div class="p-3">
    <BaseSkeleton width="80px" height="20px" class="mb-3" />
    <div class="space-y-2">
      <div v-for="n in 5" :key="n" class="flex items-center gap-3 p-3">
        <BaseSkeleton width="40px" height="40px" rounded />
        <div class="flex-1">
          <BaseSkeleton width="120px" height="16px" class="mb-2" />
          <BaseSkeleton width="100%" height="8px" class="mb-1" />
          <BaseSkeleton width="80%" height="8px" />
        </div>
        <div class="flex gap-4">
          <BaseSkeleton width="40px" height="32px" />
          <BaseSkeleton width="40px" height="32px" />
          <BaseSkeleton width="40px" height="32px" />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import BaseSkeleton from "@/components/common/BaseSkeleton.vue";
</script>