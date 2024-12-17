<template>
  <div class="profile-section">
    <div class="main-profile">
      <BaseSkeleton width="84px" height="84px" rounded />
      <div class="flex-1">
        <BaseSkeleton width="200px" height="24px" class="mb-2" />
        <BaseSkeleton width="150px" height="16px" class="mb-4" />
        <div class="flex gap-6">
          <BaseSkeleton width="60px" height="20px" />
          <BaseSkeleton width="60px" height="20px" />
          <BaseSkeleton width="60px" height="20px" />
        </div>
      </div>
      <BaseSkeleton width="100px" height="36px" rounded />
    </div>
  </div>
</template>
<script setup lang="ts">
import BaseSkeleton from "@/components/common/BaseSkeleton.vue";
</script>