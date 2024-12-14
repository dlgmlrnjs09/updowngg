<!-- src/components/modals/ReviewModal.vue -->
<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <button class="modal-close" @click="$emit('close')">×</button>

      <div class="modal-header">
        <div class="modal-title">{{ playerName }} 평가하기</div>
      </div>

      <!-- 실력/기술 평가 -->
      <div class="modal-section">
        <h2 class="section-title">실력/기술</h2>
        <div class="rating-group">
          <div class="rating-title">라인전 능력</div>
          <div class="stars">
            <button
                v-for="star in 5"
                :key="star"
                class="star"
                :class="{ active: skillRating >= star }"
                @click="skillRating = star"
            >★</button>
          </div>
        </div>
      </div>

      <!-- 팀워크 평가 -->
      <div class="modal-section">
        <h2 class="section-title">팀워크</h2>
        <div class="tags-container">
          <button
              v-for="tag in teamworkTags"
              :key="tag"
              class="tag"
              :class="{ active: selectedTeamworkTags.includes(tag) }"
              @click="toggleTag(tag, 'teamwork')"
          >
            {{ tag }}
          </button>
        </div>
      </div>

      <!-- 매너/태도 평가 -->
      <div class="modal-section">
        <h2 class="section-title">매너/태도</h2>
        <div class="tags-container">
          <button
              v-for="tag in mannerTags"
              :key="tag"
              class="tag"
              :class="{ active: selectedMannerTags.includes(tag) }"
              @click="toggleTag(tag, 'manner')"
          >
            {{ tag }}
          </button>
        </div>
      </div>

      <!-- 코멘트 작성 -->
      <div class="modal-section">
        <h2 class="section-title">코멘트 작성</h2>
        <textarea
            v-model="comment"
            class="comment-input"
            placeholder="이 소환사와의 게임은 어땠나요? (선택사항)"
        ></textarea>
        <button class="submit-btn" @click="handleSubmit">평가 제출하기</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  playerName: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', review: any): void
}>()

// 평가 상태
const skillRating = ref(0)
const selectedTeamworkTags = ref<string[]>([])
const selectedMannerTags = ref<string[]>([])
const comment = ref('')

// 태그 목록
const teamworkTags = [
  '소통이 원활해요',
  '갱 요청에 잘 반응해요',
  '오브젝트 싸움에 적극적이에요',
  '라인전 후 적절히 로밍해요'
]

const mannerTags = [
  '긍정적인 태도에요',
  '피드백을 잘 수용해요',
  '끝까지 최선을 다해요',
  '팀을 독려해요'
]

// 태그 토글 함수
const toggleTag = (tag: string, type: 'teamwork' | 'manner') => {
  const targetRef = type === 'teamwork' ? selectedTeamworkTags : selectedMannerTags
  const index = targetRef.value.indexOf(tag)

  if (index === -1) {
    targetRef.value.push(tag)
  } else {
    targetRef.value.splice(index, 1)
  }
}

// 제출 함수
const handleSubmit = () => {
  const review = {
    skillRating: skillRating.value,
    teamworkTags: selectedTeamworkTags.value,
    mannerTags: selectedMannerTags.value,
    comment: comment.value
  }

  emit('submit', review)
  emit('close')
}
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
  margin-bottom: 16px;
}

.rating-group {
  margin-bottom: 16px;
}

.rating-title {
  font-size: 14px;
  color: #CCC;
  margin-bottom: 8px;
}

.stars {
  display: flex;
  gap: 8px;
}

.star {
  background: none;
  border: none;
  color: #333;
  font-size: 24px;
  cursor: pointer;
  transition: color 0.2s;
}

.star.active {
  color: #FFD700;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #CCC;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.tag:hover {
  background: rgba(41, 121, 255, 0.1);
  border-color: #2979FF;
  color: #2979FF;
}

.tag.active {
  background: #2979FF;
  border-color: #2979FF;
  color: white;
}

.comment-input {
  width: 100%;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px;
  color: white;
  font-size: 14px;
  resize: vertical;
  min-height: 100px;
  margin-bottom: 16px;
}

.comment-input:focus {
  border-color: #2979FF;
  outline: none;
}

.submit-btn {
  width: 100%;
  background: #2979FF;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.submit-btn:hover {
  background: #2262CC;
}
</style>