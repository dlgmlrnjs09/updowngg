<template>
  <div class="no-match-container" ref="container">
    <div class="content-wrapper">
      <img src="@/assets/icon/emoji/towa.webp" alt="no-match" class="history-icon"/>
      <div class="text-wrapper">
        <h3 class="title">매치 기록이 없습니다</h3>
        <p class="description">
          최근 진행한 게임이 없거나 전적이 갱신되지 않았습니다.
        </p>
      </div>
      
      <!-- 샘플 매치 표시 (온보딩 모드에서만) -->
      <div v-if="onboardingStore.isOnboardingActive" class="sample-match-container">
        <div class="sample-label">샘플 매치 데이터 (온보딩용)</div>
        <div class="sample-match">
          <div class="sample-champion">
            <img src="/images/onboarding/champion_sample.png" alt="챔피언 샘플" class="champion-icon"/>
          </div>
          <div class="sample-info">
            <div class="match-result win">승리</div>
            <div class="match-mode">솔로랭크</div>
            <div class="match-time">22분 전</div>
          </div>
          <div class="sample-stats">
            <div class="kda">10/2/8</div>
            <div class="cs">CS 205</div>
          </div>
          <div class="sample-items">
            <div class="item-grid">
              <div class="item-box"></div>
              <div class="item-box"></div>
              <div class="item-box"></div>
              <div class="item-box"></div>
              <div class="item-box"></div>
              <div class="item-box"></div>
            </div>
          </div>
        </div>
        <div class="sample-match">
          <div class="sample-champion">
            <img src="/images/onboarding/champion_sample.png" alt="챔피언 샘플" class="champion-icon"/>
          </div>
          <div class="sample-info">
            <div class="match-result loss">패배</div>
            <div class="match-mode">솔로랭크</div>
            <div class="match-time">1시간 전</div>
          </div>
          <div class="sample-stats">
            <div class="kda">3/5/7</div>
            <div class="cs">CS 178</div>
          </div>
          <div class="sample-items">
            <div class="item-grid">
              <div class="item-box"></div>
              <div class="item-box"></div>
              <div class="item-box"></div>
              <div class="item-box"></div>
              <div class="item-box"></div>
              <div class="item-box"></div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="action-area">
        <button 
          v-if="!onboardingStore.isOnboardingActive" 
          class="guide-button"
          @click="showGuide"
        >
          가이드 보기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useOnboardingStore } from '@/stores/onboarding';

const container = ref(null);
const onboardingStore = useOnboardingStore();

// 가이드 보기 버튼 클릭 시 온보딩 활성화
const showGuide = () => {
  onboardingStore.toggleOnboarding(true);
  onboardingStore.currentStep = 3; // 매치 섹션으로 바로 이동
};
</script>

<style scoped>
.no-match-container {
  background: #141414;
  border-radius: 12px;
  padding: 40px 2rem;
  border: 1px solid rgba(255,255,255,.05);
  margin-top: 15px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.5rem;
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

.sample-match-container {
  width: 100%;
  max-width: 600px;
  margin-top: 20px;
  border: 1px dashed #444;
  border-radius: 8px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.03);
}

.sample-label {
  font-size: 12px;
  color: #888;
  margin-bottom: 10px;
  text-align: center;
  font-style: italic;
}

.sample-match {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 10px;
}

.sample-champion {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  background: #222;
}

.champion-icon {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.sample-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 80px;
}

.match-result {
  font-weight: 600;
  font-size: 14px;
}

.match-result.win {
  color: #4CAF50;
}

.match-result.loss {
  color: #FF5252;
}

.match-mode, .match-time {
  font-size: 12px;
  color: #777;
}

.sample-stats {
  display: flex;
  flex-direction: column;
  gap: 2px;
  width: 60px;
}

.kda {
  font-size: 14px;
  color: #eee;
}

.cs {
  font-size: 12px;
  color: #777;
}

.sample-items {
  flex: 1;
}

.item-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 4px;
}

.item-box {
  width: 24px;
  height: 24px;
  background: #333;
  border-radius: 4px;
}

.action-area {
  margin-top: 10px;
}

.guide-button {
  background: #2979FF;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.guide-button:hover {
  background: #1c68e3;
}

@media (max-width: 600px) {
  .sample-match {
    flex-wrap: wrap;
  }
  
  .sample-items {
    width: 100%;
    margin-top: 8px;
  }
  
  .item-grid {
    justify-content: center;
  }
}
</style>