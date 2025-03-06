<template>
  <div class="no-match-container" ref="container">
    <div v-if="onboardingStore.isOnboardingActive" class="match-sample-wrapper">
      <!-- 온보딩용 샘플 매치 데이터 -->
      <div class="sample-match win">
        <div class="match-left">
          <div class="match-result-badge">승리</div>
          <div class="match-time">22분 전</div>
          <div class="match-type">솔로랭크</div>
        </div>
        
        <div class="match-champion">
          <div class="champion-icon"></div>
          <div class="champion-name">이즈리얼</div>
        </div>
        
        <div class="match-stats">
          <div class="kda">10/2/8</div>
          <div class="kda-ratio">9.0:1 평점</div>
        </div>
        
        <div class="match-items">
          <div class="item-box"></div>
          <div class="item-box"></div>
          <div class="item-box"></div>
          <div class="item-box"></div>
          <div class="item-box"></div>
          <div class="item-box"></div>
        </div>
      </div>
      
      <div class="sample-match loss">
        <div class="match-left">
          <div class="match-result-badge">패배</div>
          <div class="match-time">1시간 전</div>
          <div class="match-type">솔로랭크</div>
        </div>
        
        <div class="match-champion">
          <div class="champion-icon"></div>
          <div class="champion-name">야스오</div>
        </div>
        
        <div class="match-stats">
          <div class="kda">3/5/7</div>
          <div class="kda-ratio">2.0:1 평점</div>
        </div>
        
        <div class="match-items">
          <div class="item-box"></div>
          <div class="item-box"></div>
          <div class="item-box"></div>
          <div class="item-box"></div>
          <div class="item-box"></div>
          <div class="item-box"></div>
        </div>
      </div>
    </div>
    
    <div v-else class="content-wrapper">
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
import { ref } from 'vue';
import { useOnboardingStore } from '@/stores/onboarding';

const container = ref(null);
const onboardingStore = useOnboardingStore();
</script>

<style scoped>
.no-match-container {
  background: #141414;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(255,255,255,.05);
  margin-top: 15px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.5rem;
  padding: 40px 0;
}

.history-icon {
  width: 80px;
  height: 80px;
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

/* 온보딩용 샘플 매치 스타일 */
.match-sample-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sample-match {
  display: flex;
  align-items: center;
  padding: 16px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.03);
  gap: 20px;
}

.sample-match.win {
  border-left: 4px solid #4CAF50;
}

.sample-match.loss {
  border-left: 4px solid #FF5252;
}

.match-left {
  width: 70px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.match-result-badge {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 2px;
}

.sample-match.win .match-result-badge {
  color: #4CAF50;
}

.sample-match.loss .match-result-badge {
  color: #FF5252;
}

.match-time, .match-type {
  font-size: 12px;
  color: #777;
}

.match-champion {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  width: 60px;
}

.champion-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #333;
}

.champion-name {
  font-size: 12px;
  color: #ccc;
  white-space: nowrap;
}

.match-stats {
  display: flex;
  flex-direction: column;
  gap: 2px;
  width: 100px;
}

.kda {
  font-size: 14px;
  color: #eee;
}

.kda-ratio {
  font-size: 12px;
  color: #aaa;
}

.match-items {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
  justify-content: flex-end;
  flex: 1;
}

.item-box {
  width: 22px;
  height: 22px;
  background: #333;
  border-radius: 4px;
}

@media (max-width: 600px) {
  .sample-match {
    flex-wrap: wrap;
    padding: 12px;
    gap: 10px;
  }
  
  .match-left {
    width: 60px;
  }
  
  .match-items {
    width: 100%;
    justify-content: flex-start;
    margin-top: 6px;
  }
}
</style>