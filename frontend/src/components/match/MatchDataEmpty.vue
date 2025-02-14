<template>
  <div class="no-match-container">
    <div class="content-wrapper">
      <img src="@/assets/icon/emoji/towa.webp" alt="no-match" class="history-icon"/>
      <div class="text-wrapper">
        <h3 class="title">매치 기록이 없습니다</h3>
        <p class="description">
          최근 진행한 게임이 없거나 전적이 갱신되지 않았습니다.
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.no-match-container {
  background: #141414;
  border-radius: 12px;
  padding: 200px 2rem;
  border: 1px solid rgba(255,255,255,.05);
  margin-top: 15px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.history-icon {
  width: 100px;
  height: 100px;
  color: #666;
}

.text-wrapper {
  text-align: center;
}

.title {
  font-size: 1.125rem;
  font-weight: 500;
  color: #e5e5e5;
  margin-bottom: 0.5rem;
}

.description {
  font-size: 0.875rem;
  color: #666;
}
</style>