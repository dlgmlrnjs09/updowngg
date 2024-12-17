// components/skeletons/MatchListSkeleton.vue
<template>
  <div class="space-y-4">
    <div class="flex gap-3 mb-6">
      <BaseSkeleton width="80px" height="36px" rounded />
      <BaseSkeleton width="80px" height="36px" rounded />
      <BaseSkeleton width="80px" height="36px" rounded />
    </div>
    <div v-for="n in 3" :key="n" class="game-item p-6">
      <div class="mb-6">
        <BaseSkeleton width="150px" height="24px" class="mb-3" />
        <BaseSkeleton width="200px" height="16px" />
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <MatchTeamSkeleton />
        <MatchTeamSkeleton />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import BaseSkeleton from "@/components/common/BaseSkeleton.vue";
import MatchTeamSkeleton from "@/components/match/MatchTeamSkeleton.vue";
</script>