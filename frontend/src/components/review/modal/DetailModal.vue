<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <button class="modal-close" @click="$emit('close')">×</button>

      <div class="modal-header">
        <div class="modal-title">{{ name }}님의 상세 평가</div>
      </div>

      <!-- 실력/기술 섹션 -->
      <div class="modal-section">
        <div class="section-title">
          실력/기술
          <span class="section-score">4.8</span>
        </div>
        <div class="tags-container">
          <div class="tag" v-for="tag in skillTags" :key="tag.name">
            {{ tag.name }}
            <span class="tag-count">{{ tag.count }}</span>
          </div>
        </div>
      </div>

      <!-- 팀워크 섹션 -->
      <div class="modal-section">
        <div class="section-title">
          팀워크
          <span class="section-score">4.6</span>
        </div>
        <div class="tags-container">
          <div class="tag" v-for="tag in teamworkTags" :key="tag.name">
            {{ tag.name }}
            <span class="tag-count">{{ tag.count }}</span>
          </div>
        </div>
      </div>

      <!-- 매너/태도 섹션 -->
      <div class="modal-section">
        <div class="section-title">
          매너/태도
          <span class="section-score">4.9</span>
        </div>
        <div class="tags-container">
          <div class="tag" v-for="tag in mannerTags" :key="tag.name">
            {{ tag.name }}
            <span class="tag-count">{{ tag.count }}</span>
          </div>
        </div>
      </div>

      <!-- 최근 평가 섹션 -->
      <div class="modal-section">
        <div class="section-title">최근 평가</div>
        <div class="review-list">
          <div class="review-item" v-for="review in recentReviews" :key="review.id">
            <div class="review-meta">{{ review.gameType }} · {{ review.date }}</div>
            <div class="review-text">{{ review.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  name: string
}>()

defineEmits<(e: 'close') => void>()

// 임시 데이터
const skillTags = ref([
  { name: '라인전 압도적', count: 124 },
  { name: 'CS 관리 완벽', count: 98 },
  { name: '오브젝트 컨트롤 우수', count: 87 },
  { name: '교전 능력 탁월', count: 156 }
])

const teamworkTags = ref([
  { name: '소통이 원활해요', count: 145 },
  { name: '갱 요청에 잘 반응해요', count: 92 },
  { name: '로밍이 완벽해요', count: 78 }
])

const mannerTags = ref([
  { name: '긍정적인 태도', count: 167 },
  { name: '피드백 수용적', count: 134 },
  { name: '팀을 독려해요', count: 156 }
])

const recentReviews = ref([
  {
    id: 1,
    gameType: '솔로 랭크',
    date: '12월 10일',
    content: '라인전부터 한번도 밀리지 않고 팀에도 도움을 많이 주셨습니다. 매너도 좋으시고 팀원들과의 소통도 원활했어요!'
  },
  {
    id: 2,
    gameType: '솔로 랭크',
    date: '12월 9일',
    content: '오브젝트 타이밍을 잘 맞추시고 팀 플레이가 훌륭했습니다. 정글러와의 호흡이 특히 인상적이었어요.'
  }
])
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #141414;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  color: #888;
  cursor: pointer;
  padding: 8px;
  font-size: 24px;
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-title {
  font-size: 20px;
  font-weight: 600;
}

.modal-section {
  padding: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-score {
  color: #2979FF;
  font-size: 24px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  background: rgba(41, 121, 255, 0.1);
  border: 1px solid rgba(41, 121, 255, 0.2);
  color: #2979FF;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tag-count {
  background: rgba(41, 121, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 16px;
}

.review-meta {
  color: #888;
  font-size: 14px;
  margin-bottom: 8px;
}

.review-text {
  color: #CCC;
  font-size: 14px;
  line-height: 1.6;
}
</style>