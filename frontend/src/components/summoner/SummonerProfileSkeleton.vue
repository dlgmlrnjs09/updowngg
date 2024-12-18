<template>
  <div class="p-4 bg-[#141414] rounded-lg">
    <!-- 상단 프로필 섹션 -->
    <div class="flex items-center gap-4">
      <!-- 프로필 이미지 -->
      <BaseSkeleton width="64px" height="64px" rounded />

      <!-- 유저 정보 -->
      <div class="flex-1">
        <BaseSkeleton width="120px" height="20px" class="mb-2" />
        <div class="flex gap-4 mb-4">
          <BaseSkeleton width="40px" height="16px" />
          <BaseSkeleton width="40px" height="16px" />
          <BaseSkeleton width="40px" height="16px" />
        </div>
      </div>

      <!-- 전적 갱신 버튼 -->
      <BaseSkeleton width="80px" height="32px" rounded />
    </div>

    <!-- 평가 통계 섹션 -->
    <div class="mt-6">
      <!-- 평가자 신뢰도 -->
      <div class="flex items-center justify-between mb-4">
        <BaseSkeleton width="100px" height="16px" />
        <BaseSkeleton width="60px" height="24px" />
      </div>

      <!-- 긍정적 평가 -->
      <div class="flex items-center justify-between mb-4">
        <BaseSkeleton width="100px" height="16px" />
        <BaseSkeleton width="60px" height="24px" />
      </div>

      <!-- 평가 참여율 -->
      <div class="flex items-center justify-between mb-4">
        <BaseSkeleton width="100px" height="16px" />
        <BaseSkeleton width="60px" height="24px" />
      </div>

      <!-- 태그 섹션 -->
      <div class="flex gap-3 mt-4">
        <BaseSkeleton width="80px" height="28px" rounded />
        <BaseSkeleton width="80px" height="28px" rounded />
        <BaseSkeleton width="80px" height="28px" rounded />
        <BaseSkeleton width="80px" height="28px" rounded />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import BaseSkeleton from "@/components/common/BaseSkeleton.vue";
</script>