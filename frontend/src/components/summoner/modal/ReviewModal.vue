<!-- src/components/modals/ReviewModal.vue -->
<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <button class="modal-close" @click="$emit('close')">×</button>

      <div class="modal-header">
        <div class="modal-title">{{ player.riotIdGameName }} 평가하기</div>
      </div>

      <!-- 실력/기술 평가 -->
      <div class="modal-section">
        <h2 class="section-title">실력/기술</h2>
        <div class="rating-group">
          <div class="stars">
            <div class="star-rating">
              <span
                  v-for="i in 10"
                  :key="i"
                  class="star-half"
                  :class="{ active: skillRating >= i/2 }"
                  @click="skillRating = i/2"
                  @mouseover="hoverRating.skill = i/2"
                  @mouseleave="hoverRating.skill = 0"
              ></span>
            </div>
            <span class="rating-value">{{ skillRating }}점</span>
          </div>
        </div>
      </div>

      <!-- 팀워크 평가 -->
      <div class="modal-section">
        <h2 class="section-title">팀워크</h2>
        <div class="rating-group">
          <div class="stars">
            <div class="star-rating">
              <span
                  v-for="i in 10"
                  :key="i"
                  class="star-half"
                  :class="{ active: teamworkRating >= i/2 }"
                  @click="teamworkRating = i/2"
                  @mouseover="hoverRating.teamwork = i/2"
                  @mouseleave="hoverRating.teamwork = 0"
              ></span>
            </div>
            <span class="rating-value">{{ teamworkRating }}점</span>
          </div>
        </div>
      </div>

      <!-- 매너/태도 평가 -->
      <div class="modal-section">
        <h2 class="section-title">매너/태도</h2>
        <div class="rating-group">
          <div class="stars">
            <div class="star-rating">
              <span
                  v-for="i in 10"
                  :key="i"
                  class="star-half"
                  :class="{ active: mannerRating >= i/2 }"
                  @click="mannerRating = i/2"
                  @mouseover="hoverRating.manner = i/2"
                  @mouseleave="hoverRating.manner = 0"
              ></span>
            </div>
            <span class="rating-value">{{ mannerRating}}점</span>
          </div>
        </div>
      </div>

      <!-- 태그 선택 -->
      <div class="modal-section">
        <h2 class="section-title">플레이 스타일</h2>
        <div class="tags-container">
          <button
              v-for="tag in reviewTags"
              :key="tag.tagCode"
              class="tag"
              :class="{ active: selectedStyleTags.includes(tag.tagCode) || player.reviewDto?.tagCodeList?.includes(tag.tagCode) }"
              @click="toggleTag(tag.tagCode, 'style')"
          >
            {{ tag.tagValue }}
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
import {onMounted, ref} from 'vue'
import type {ReviewTagDto} from "@/types/review.ts";
import {reviewApi} from "@/api/review.ts";
import {useAuthStore} from "@/stores/auth.ts";
import type {LolMatchParticipant} from "@/types/match.ts";
import {useToast} from "vue-toastification";

const props = defineProps<{
  player: LolMatchParticipant
  reviewTags: ReviewTagDto[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit'): void
}>()

const authStore = useAuthStore();
const toast = useToast();

// 평가 상태
const reviewSeq = ref(0)
const skillRating = ref(0)
const teamworkRating = ref(0)
const mannerRating = ref(0)
const selectedStyleTags = ref<string[]>([])
const comment = ref('')

// Init
onMounted(() => {
  init();
})

const init = () => {
  if (props.player.reviewDto) {
    reviewSeq.value = props.player.reviewDto.summonerReviewSeq
    skillRating.value = props.player.reviewDto.skillRating
    teamworkRating.value = props.player.reviewDto.teamworkRating
    mannerRating.value = props.player.reviewDto.mannerRating
    selectedStyleTags.value = props.player.reviewDto.tagCodeList || []
    comment.value = props.player.reviewDto.comment || ''
  } else {
    reviewSeq.value = 0
    skillRating.value = 0
    teamworkRating.value = 0
    mannerRating.value = 0
    selectedStyleTags.value = []
    comment.value = ''
  }
}

// 호버 상태 관리
const hoverRating = ref({
  skill: 0,
  teamwork: 0,
  manner: 0
})

// 태그 토글 함수
const toggleTag = (tag: string, type: 'style') => {
  const index = selectedStyleTags.value.indexOf(tag)
  if (index === -1) {
    if (selectedStyleTags.value.length < 3) {
      selectedStyleTags.value.push(tag)
    }
  } else {
    selectedStyleTags.value.splice(index, 1)
  }
}

// 제출 함수
const handleSubmit = async () => {
  if (!authStore.isAuthenticated) {
    toast.error('로그인이 필요합니다');
    return;
  }

  const review = {
    summonerReviewSeq: reviewSeq.value,
    reviewerSiteCode: authStore.user?.memberSiteCode,
    reviewerPuuid: authStore.user?.puuid,
    targetPuuid: props.player.puuid,
    skillRating: skillRating.value,
    teamworkRating: teamworkRating.value,
    mannerRating: mannerRating.value,
    comment: comment.value,
    tagCodeList: selectedStyleTags.value
  }

  if (!props.player.reviewDto) {
    await reviewApi.submitReview(review)
  } else {
    await reviewApi.updateReview(review)
  }

  emit('submit')
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
  color: #fff;
}

.modal-section {
  padding: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #fff;
}

.rating-group {
  margin-bottom: 16px;
}

.star-rating {
  display: inline-flex;
  gap: 2px;
}

.star-half {
  position: relative;
  width: 12px;
  height: 24px;
  background: #333;
  cursor: pointer;
  transition: all 0.2s;
}

.star-half.active {
  background: #FFD700;
}

.star-half:nth-child(odd) {
  border-top-left-radius: 12px;
  border-bottom-left-radius: 12px;
}

.star-half:nth-child(even) {
  border-top-right-radius: 12px;
  border-bottom-right-radius: 12px;
}

.rating-value {
  margin-left: 12px;
  color: #CCC;
  font-size: 14px;
  min-width: 45px;
}

.stars {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
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
  user-select: none;
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
  font-family: inherit;
}

.comment-input:focus {
  border-color: #2979FF;
  outline: none;
}

.comment-input::placeholder {
  color: #666;
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

.submit-btn:active {
  background: #1B4FA0;
}

.modal-content::-webkit-scrollbar {
  width: 8px;
}

.modal-content::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
}

.modal-content::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

.modal-content::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.2);
}

@media (max-width: 480px) {
  .modal-content {
    width: 95%;
    max-height: 85vh;
  }

  .modal-section {
    padding: 16px;
  }

  .tag {
    padding: 6px 12px;
    font-size: 13px;
  }

  .modal-title {
    font-size: 18px;
  }

  .section-title {
    font-size: 16px;
  }
}
</style>