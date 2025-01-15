<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <button class="modal-close" @click="$emit('close')">×</button>

      <div class="modal-header">
        <div class="modal-title">{{ player.riotIdGameName }} 평가하기</div>
      </div>

      <div class="review-section">
        <div class="review-buttons">
          <button
              class="review-button like"
              :class="{ selected: isUp === true }"
              @click="setRating(true)"
          >
            <svg class="button-icon" viewBox="0 0 24 24">
              <path d="M14.6 8H21a2 2 0 0 1 2 2v2.104a2 2 0 0 1-.15.762l-3.095 7.515a1 1 0 0 1-.925.619H2a1 1 0 0 1-1-1V10a1 1 0 0 1 1-1h3.482a1 1 0 0 0 .817-.423L11.752.85a.5.5 0 0 1 .632-.159l1.814.907a2.5 2.5 0 0 1 1.305 2.853L14.6 8zM7 10.588V19h11.16L21 12.104V10h-6.4a2 2 0 0 1-1.938-2.493l.903-3.548a.5.5 0 0 0-.261-.571l-.661-.33-4.71 6.672c-.25.354-.57.644-.933.858zM5 11H3v8h2v-8z"/>
            </svg>
            <!--            <span class="button-text">좋아요</span>-->
          </button>
          <button
              class="review-button dislike"
              :class="{ selected: isUp === false }"
              @click="setRating(false)"
          >
            <svg class="button-icon" viewBox="0 0 24 24">
              <path d="M9.4 16H3a2 2 0 0 1-2-2v-2.104a2 2 0 0 1 .15-.762L4.246 3.62A1 1 0 0 1 5.17 3H22a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1h-3.482a1 1 0 0 0-.817.423l-5.453 7.726a.5.5 0 0 1-.632.159L9.802 21.4A2.5 2.5 0 0 1 8.496 18.55L9.4 16zM17 14.586V6H5.84L3 12.896V14h6.4a2 2 0 0 1 1.938 2.493l-.903 3.548a.5.5 0 0 0 .261.571l.661.33 4.71-6.672c.25-.354.57-.644.933-.858zM19 13h2V5h-2v8z"/>
            </svg>
            <!--            <span class="button-text">싫어요</span>-->
          </button>
        </div>
      </div>

      <div class="tags-section">
        <h2 class="section-title">플레이 스타일 (최대 5개)</h2>
        <TagList
            :tags="reviewTags"
            size="large"
            :is-show-count="false"
            :is-arrange-row="true"
            :is-clickable="true"
            :selected-tags="selectedStyleTags"
            @tagClick="toggleTag"
        />
      </div>

      <div class="comment-section">
        <textarea
            v-model="comment"
            class="comment-input"
            placeholder="이 소환사와의 게임은 어땠나요? (선택사항)"
        ></textarea>
        <button
            class="submit-btn"
            :disabled="isUp === null"
            @click="handleSubmit"
        >
          평가 제출하기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'
import type {ReviewRequestDto, ReviewTagDto} from "@/types/review.ts"
import { reviewApi } from "@/api/review.ts"
import { useAuthStore } from "@/stores/auth.ts"
import type { LolMatchParticipant } from "@/types/match.ts"
import { useToast } from "vue-toastification"
import TagList from "@/components/common/TagList.vue";

const props = defineProps<{
  player: LolMatchParticipant
  reviewTags: ReviewTagDto[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', reviewData: ReviewRequestDto): void
}>()

const authStore = useAuthStore()
const toast = useToast()

// 평가 상태
const isUp = ref<boolean | null>(null)
const selectedStyleTags = ref<string[]>([])
const comment = ref('')
const reviewSeq = ref(0)

// Init
onMounted(() => {
  init();
})

const init = () => {
  if (props.player.reviewDto) {
    reviewSeq.value = props.player.reviewDto.summonerReviewSeq
    selectedStyleTags.value = props.player.reviewDto.tagCodeList || []
    comment.value = props.player.reviewDto.comment || ''
    isUp.value = props.player.reviewDto.isUp;
  } else {
    reviewSeq.value = 0
    selectedStyleTags.value = []
    comment.value = ''
    isUp.value = null;
  }
}

const setRating = (isUpParam: boolean) => {
  isUp.value = isUp.value === isUpParam ? null : isUpParam
}

const toggleTag = (tagCode: string) => {
  const index = selectedStyleTags.value.indexOf(tagCode)
  if (index === -1) {
    if (selectedStyleTags.value.length < 5) {
      selectedStyleTags.value.push(tagCode)
    }
  } else {
    selectedStyleTags.value.splice(index, 1)
  }
}

const handleSubmit = async () => {
  if (!authStore.isAuthenticated || !authStore.user) {
    toast.error('로그인이 필요합니다')
    return
  }

  if (isUp.value === null) {
    toast.error('평가를 선택해주세요')
    return
  }

  const selectedTagDtoList = props.reviewTags
      .filter(tagDto => selectedStyleTags.value.includes(tagDto.tagCode))
      .map(tagDto => ({
        ...tagDto,
        // frequentCount: tagDto.frequentCount == null ? 1 : tagDto.frequentCount + 1 // 받은 태그 수 1 증가
      }));

  const review: ReviewRequestDto = {
    summonerReviewSeq: props.player?.reviewDto?.summonerReviewSeq,
    reviewerSiteCode: authStore.user.memberSiteCode.toString(),
    reviewerPuuid: authStore.user.puuid,
    targetPuuid: props.player.puuid,
    isUp: isUp.value,
    comment: comment.value,
    tagCodeList: selectedStyleTags.value,
    matchId: props.player.matchId,
    tagDtoList: selectedTagDtoList,
    reviewable: false,
    regDt: null
  }

  try {
    if (props.player.reviewDto.summonerReviewSeq) {
      await reviewApi.updateReview(review)
    } else {
      const reviewSeq = await reviewApi.submitReview(review)
      review.summonerReviewSeq = reviewSeq.data
    }
    toast.success('평가가 성공적으로 제출되었습니다')
    emit('submit', review)
    emit('close')
  } catch (error) {
    toast.error('평가 제출 중 오류가 발생했습니다')
  }
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
  max-width: 480px;
  position: relative;
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

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  color: #888;
  font-size: 24px;
  cursor: pointer;
  padding: 8px;
}

.review-section {
  padding: 32px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32px;
}

.review-buttons {
  display: flex;
  gap: 16px;
  width: 100%;
}

.review-button {
  flex: 1;
  padding: 24px;
  border-radius: 12px;
  border: 2px solid transparent;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  transition: all 0.2s ease;
  background: rgba(255, 255, 255, 0.05);
}

.review-button.like {
  color: #4CAF50;
}

.review-button.dislike {
  color: #FF5252;
}

.review-button:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.review-button.selected.like {
  background: rgba(76, 175, 80, 0.15);
  border-color: #4CAF50;
}

.review-button.selected.dislike {
  background: rgba(255, 82, 82, 0.15);
  border-color: #FF5252;
}

.button-icon {
  width: 32px;
  height: 32px;
  fill: currentColor;
}

.button-text {
  font-size: 16px;
  font-weight: 600;
}

.tags-section {
  padding: 0 24px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  width: 100%;
  box-sizing: border-box;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #fff;
}

.comment-section {
  padding: 24px;
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

.submit-btn:hover:not(:disabled) {
  background: #2262CC;
}

.submit-btn:disabled {
  background: #1e293b;
  cursor: not-allowed;
}

@media (max-width: 480px) {
  .modal-content {
    width: 95%;
  }

  .review-button {
    padding: 20px;
  }

  .button-icon {
    width: 28px;
    height: 28px;
  }

  .button-text {
    font-size: 14px;
  }
}
</style>