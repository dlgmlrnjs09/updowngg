<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <button class="modal-close" @click="$emit('close')">×</button>

      <div class="modal-header">
        <div class="modal-title">태그 신청하기</div>
      </div>

      <div class="suggest-form">
        <!-- 태그 이름 입력 -->
        <div class="form-group">
          <input
              v-model="tagName"
              type="text"
              class="tag-input"
              placeholder="예: 오브젝트 마스터"
              maxlength="10"
          />
          <div class="input-counter">{{ tagName.length }}/10</div>
        </div>

        <!-- 카테고리 선택 -->
        <div class="form-group">
          <select v-model="selectedCategory" class="category-select">
            <option value="" disabled selected>카테고리 선택</option>
            <option
                :value="category.categoryKey"
                v-for="category in reviewTagCategories"
                style="color: black"
            >
              {{category.categoryValue}}
            </option>
          </select>
        </div>

        <!-- 태그 성향 선택 -->
        <div class="review-section">
          <div class="review-buttons">
            <button
                class="review-button like"
                :class="{ selected: isPositive === true }"
                @click="setNature(true)"
            >
              <svg class="button-icon" viewBox="0 0 24 24">
                <path d="M14.6 8H21a2 2 0 0 1 2 2v2.104a2 2 0 0 1-.15.762l-3.095 7.515a1 1 0 0 1-.925.619H2a1 1 0 0 1-1-1V10a1 1 0 0 1 1-1h3.482a1 1 0 0 0 .817-.423L11.752.85a.5.5 0 0 1 .632-.159l1.814.907a2.5 2.5 0 0 1 1.305 2.853L14.6 8zM7 10.588V19h11.16L21 12.104V10h-6.4a2 2 0 0 1-1.938-2.493l.903-3.548a.5.5 0 0 0-.261-.571l-.661-.33-4.71 6.672c-.25.354-.57.644-.933.858zM5 11H3v8h2v-8z"/>
              </svg>
            </button>
            <button
                class="review-button dislike"
                :class="{ selected: isPositive === false }"
                @click="setNature(false)"
            >
              <svg class="button-icon" viewBox="0 0 24 24">
                <path d="M9.4 16H3a2 2 0 0 1-2-2v-2.104a2 2 0 0 1 .15-.762L4.246 3.62A1 1 0 0 1 5.17 3H22a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1h-3.482a1 1 0 0 0-.817.423l-5.453 7.726a.5.5 0 0 1-.632.159L9.802 21.4A2.5 2.5 0 0 1 8.496 18.55L9.4 16zM17 14.586V6H5.84L3 12.896V14h6.4a2 2 0 0 1 1.938 2.493l-.903 3.548a.5.5 0 0 0 .261.571l.661.33 4.71-6.672c.25-.354.57-.644.933-.858zM19 13h2V5h-2v8z"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- 태그 설명 -->
        <div class="form-group">
          <textarea
              v-model="description"
              class="tag-description"
              placeholder="툴팁에 노출될 간단한 설명을 입력해주세요."
              maxlength="20"
          ></textarea>
          <div class="input-counter">{{ description.length }}/20</div>
        </div>

        <!-- 안내 메시지 -->
        <div class="info-message">
          <i class="info-icon">ℹ</i>
          추가된 태그는 검토 후 24시간 이내에 반영됩니다.<br>
          승인시 작성하신 리뷰에 자동으로 추가됩니다.<br>
          리뷰를 작성을 취소하는 경우 태그가 추가되지 않습니다.<br>
          부적절한 태그는 반려될 수 있습니다.
        </div>

        <!-- 제출 버튼 -->
        <button
            class="submit-btn"
            :disabled="!isFormValid"
            @click="handleSubmit"
        >
          추가
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useToast } from 'vue-toastification'
import type {ReviewRequestDto, ReviewTagCategoryDto, ReviewTagDto, ReviewTagSuggestDto} from "@/types/review.ts";
import {useAuthStore} from "@/stores/auth.ts";
import type {LolMatchParticipant} from "@/types/match.ts";

const toast = useToast()
const authStore = useAuthStore()

const tagName = ref('')
const selectedCategory = ref('')
const isPositive = ref<boolean | null>(null)
const description = ref('')

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', tagSuggestDto: ReviewTagSuggestDto): void
}>()

const props = defineProps<{
  player: LolMatchParticipant
  reviewTagCategories: ReviewTagCategoryDto[]
}>()

const setNature = (isPositiveParam: boolean) => {
  isPositive.value = isPositive.value === isPositiveParam ? null : isPositiveParam
}

const isFormValid = computed(() => {
  return tagName.value.length > 0 &&
      selectedCategory.value &&
      isPositive.value !== null &&
      description.value.length > 0
})

const handleSubmit = async () => {
  if (!isFormValid.value) {
    toast.error('모든 필드를 입력해주세요')
    return
  }

  if (!authStore.isAuthenticated || !authStore.user) {
    toast.error('로그인이 필요합니다')
    return
  }

  try {
    const tagDto: ReviewTagSuggestDto = {
      tagValue: tagName.value,
      tagDescription: description.value,
      isUp: isPositive.value,
      targetPuuid: props.player.puuid,
      matchId: props.player.matchId,
      tagCategory: selectedCategory
    }

    emit('submit', tagDto)
  } catch (error) {
    toast.error('태그 등록중 오류가 발생했습니다')
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

.suggest-form {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  position: relative;
}

.tag-input,
.category-select,
.tag-description {
  width: 100%;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: white;
  font-size: 14px;
  transition: all 0.2s ease;
}

.tag-input:focus,
.category-select:focus,
.tag-description:focus {
  outline: none;
  border-color: #2979FF;
  background: rgba(255, 255, 255, 0.08);
}

.tag-description {
  min-height: 100px;
  resize: vertical;
}

.input-counter {
  position: absolute;
  right: 8px;
  bottom: 8px;
  font-size: 12px;
  color: #666;
}

.review-section {
  padding: 0;
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

.info-message {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: rgba(41, 121, 255, 0.1);
  border-radius: 8px;
  color: #888;
  font-size: 14px;
  line-height: 1.5;
  border: 1px solid rgba(41, 121, 255, 0.2);
}

.info-icon {
  color: #2979FF;
  font-size: 18px;
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
  margin-top: 8px;
}

.submit-btn:hover:not(:disabled) {
  background: #2262CC;
}

.submit-btn:disabled {
  background: #1e293b;
  cursor: not-allowed;
  opacity: 0.7;
}

@media (max-width: 480px) {
  .modal-content {
    width: 95%;
  }

  .suggest-form {
    padding: 20px;
  }

  .review-button {
    padding: 20px;
  }

  .button-icon {
    width: 28px;
    height: 28px;
  }
}
</style>