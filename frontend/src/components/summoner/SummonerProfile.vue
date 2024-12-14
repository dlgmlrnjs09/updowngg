<template>
  <div class="profile-section">
    <div class="profile-icon">
      <img :src="profileData.lolSummonerDto.profileIconUrl" alt="">
    </div>
    <div class="profile-info">
      <div class="profile-header">
        <div class="name-section">
          <div class="summoner-name">{{ profileData.riotAccountInfoEntity.gameName }}</div>
          <div class="profile-stats">평가 312회 · 최근 30일 동안 89회 평가됨</div>
        </div>
        <button class="detail-button" @click="$emit('showDetail')">상세 평가 보기</button>
        <button class="update-button" @click="$emit('updateMatches')">전적 갱신</button>
      </div>
      <div class="rating-stats">
        <div class="rating-item">
          <span class="rating-value">4.8</span>
          <span class="rating-label">실력</span>
        </div>
        <div class="rating-item">
          <span class="rating-value">4.6</span>
          <span class="rating-label">팀워크</span>
        </div>
        <div class="rating-item">
          <span class="rating-value">4.9</span>
          <span class="rating-label">매너</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { LolSummonerProfileResDto } from '@/types/summoner.ts'

defineProps<{
  profileData: LolSummonerProfileResDto
}>()

defineEmits<{
  (e: 'showDetail'): void
  (e: 'updateMatches'): void
}>()
</script>

<style scoped>
.profile-section {
  background: #141414;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.profile-icon {
  width: 80px;
  height: 80px;
  background: #2979FF;
  border-radius: 12px;
}

.profile-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-header {
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.summoner-name {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
}

.profile-stats {
  color: #888;
  font-size: 14px;
}

.detail-button, .update-button {
  background: rgba(41, 121, 255, 0.1);
  border: 1px solid rgba(41, 121, 255, 0.2);
  color: #2979FF;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.detail-button:hover, .update-button:hover {
  background: rgba(41, 121, 255, 0.2);
}

.rating-stats {
  display: flex;
  gap: 24px;
}

.rating-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-value {
  font-weight: 600;
  color: #2979FF;
}

.rating-label {
  color: #888;
  font-size: 14px;
}
</style>