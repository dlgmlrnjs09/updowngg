<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <button class="modal-close" @click="handleClose">×</button>

      <div class="modal-header">
        <div class="modal-title">{{ player.riotIdGameName }} 평가하기</div>
      </div>

      <!-- 평가 버튼 섹션 -->
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
          </button>
          <button
              class="review-button dislike"
              :class="{ selected: isUp === false }"
              @click="setRating(false)"
          >
            <svg class="button-icon" viewBox="0 0 24 24">
              <path d="M9.4 16H3a2 2 0 0 1-2-2v-2.104a2 2 0 0 1 .15-.762L4.246 3.62A1 1 0 0 1 5.17 3H22a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1h-3.482a1 1 0 0 0-.817.423l-5.453 7.726a.5.5 0 0 1-.632.159L9.802 21.4A2.5 2.5 0 0 1 8.496 18.55L9.4 16zM17 14.586V6H5.84L3 12.896V14h6.4a2 2 0 0 1 1.938 2.493l-.903 3.548a.5.5 0 0 0 .261.571l.661.33 4.71-6.672c.25-.354.57-.644.933-.858zM19 13h2V5h-2v8z"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- 태그 선택 섹션 -->
      <div class="tags-section">
        <h2 class="section-title">플레이 스타일 (최대 5개)</h2>

        <!-- 태그 검색 -->
        <div class="search-container">
          <input
              v-model="searchQuery"
              type="text"
              class="tag-search"
              placeholder="태그 검색..."
          />
        </div>

        <!-- 카테고리 탭 -->
        <div class="category-tabs">
          <button
              class="category-tab"
              :class="{ active: selectedCategory === 'all' }"
              @click="selectedCategory = 'all'"
          >
            전체
          </button>
          <button
              v-for="category in reviewTagCategories"
              :key="category.categoryKey"
              class="category-tab"
              :class="{ active: selectedCategory === category.categoryKey }"
              @click="selectedCategory = category.categoryKey"
          >
            {{ category.categoryValue }}
          </button>
        </div>

        <!-- 선택된 태그 표시 -->
        <div v-if="selectedStyleTags.length > 0" class="selected-tags">
          <span
              v-for="tag in selectedStyleTags"
              :key="tag"
              class="selected-tag"
          >
            {{ getTagLabel(tag) }}
            <button class="remove-tag" @click="toggleTag(tag)">×</button>
          </span>
        </div>

        <!-- 태그 목록 -->
        <div class="tag-container">
          <button
              class="tag-button tag-suggest-button"
              @click="handleTagSuggestClick"
          >
            +
          </button>
          <button
              v-for="tag in filteredTags"
              :key="tag.tagCode"
              class="tag-button"
              :class="{ selected: selectedStyleTags.includes(tag.tagCode) }"
              :data-up="tag.tagUpdown"
              :disabled="selectedStyleTags.length >= 5 && !selectedStyleTags.includes(tag.tagCode)"
              @click="toggleTag(tag.tagCode)"
              @mouseenter="handleMouseEnter($event, tag)"
              @mouseleave="hoveredTag = null"
          >
            {{ tag.tagValue }}
            <div
                v-if="tag === hoveredTag"
                class="tooltip"
                :style="tooltipStyle"
            >
              {{ tag.tagDescription }}
              <div class="tooltip-arrow"></div>
            </div>
          </button>
        </div>

        <!-- 선택 개수 표시 -->
        <div class="tag-count">
          {{ selectedStyleTags.length }}/5 태그 선택됨
        </div>
      </div>

      <!-- 코멘트 섹션 -->
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

import { ref, computed, onMounted, watch } from 'vue'
import type {ReviewRequestDto, ReviewTagCategoryDto, ReviewTagDto, ReviewTagSuggestDto} from "@/types/review.ts"
import { reviewApi } from "@/api/review.ts"
import { useAuthStore } from "@/stores/auth.ts"
import type { LolMatchParticipant } from "@/types/match.ts"
import { useToast } from "vue-toastification"
import TagSuggestModal from "@/components/review/modal/TagSuggestModal.vue";

const props = defineProps<{
  player: LolMatchParticipant
  reviewTags: ReviewTagDto[]
  reviewTagCategories: ReviewTagCategoryDto[]
  suggestTag?: ReviewTagSuggestDto[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', reviewData: ReviewRequestDto): void
  (e: 'openTagSuggest'): void
}>()

const authStore = useAuthStore()
const toast = useToast()

// 상태 관리
const isUp = ref<boolean | null>(null)
const selectedStyleTags = ref<string[]>([])
const comment = ref('')
const reviewSeq = ref(0)
const searchQuery = ref('')
const selectedCategory = ref('all')
const hoveredTag = ref<ReviewTagDto | null>(null)
const tooltipStyle = ref({})
const localReviewTags = ref<ReviewTagDto[]>([]);

// 컴포넌트 마운트 시 초기화
onMounted(() => {
  init()
  // props의 reviewTags를 로컬 상태로 복사
  localReviewTags.value = [...props.reviewTags];
})

// suggestedTag가 있을 경우 자동으로 선택
watch(() => props.suggestTag, (newTags) => {
  if (Array.isArray(newTags) && newTags.length > 0) {
    newTags.forEach(tag => {
      if (tag?.tagValue) {
        // 제안된 태그를 위한 임시 tagCode 생성
        const tempTagCode = `temp_${tag.tagValue.replace(/\s+/g, '_')}`;
        if (!selectedStyleTags.value.includes(tempTagCode)) {
          selectedStyleTags.value.push(tempTagCode);

          // reviewTags 배열에 임시 태그 추가
          const newTagDto: ReviewTagDto = {
            tagCode: tempTagCode,
            tagValue: tag.tagValue,
            tagDescription: tag.tagDescription,
            tagCategory: tag.tagCategory?.toString() || '',
            tagUpdown: tag.isUp || false,
            frequentCount: 0
          };

          // 로컬 태그 목록에 새로운 태그 추가
          localReviewTags.value.push(newTagDto);
        }
      }
    });
  }
}, { immediate: true });


const handleTagSuggestClick = () => {
  // emit('close')  // 리뷰 모달 닫기
  emit('openTagSuggest')  // 태그 제안 모달 열기 요청
}

// 필터링된 태그 계산
const filteredTags = computed(() => {
  let filtered = localReviewTags.value;

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    const searchTerms = query.split(/\s+/).filter(term => term.length > 0);

    filtered = filtered.filter(tag => {
      const tagValue = tag.tagValue.toLowerCase();
      const tagDesc = tag.tagDescription?.toLowerCase() || '';
      return searchTerms.every(term =>
          tagValue.includes(term) || tagDesc.includes(term)
      );
    });
  }

  if (selectedCategory.value !== 'all') {
    filtered = filtered.filter(tag => tag.tagCategory === selectedCategory.value);
  }

  return filtered;
});

// 태그 라벨 가져오기
const getTagLabel = (tagCode: string) => {
  const tag = localReviewTags.value.find(t => t.tagCode === tagCode)
  return tag ? tag.tagValue : tagCode
}

// 초기화
const init = () => {
  if (props.player.reviewDto) {
    reviewSeq.value = props.player.reviewDto.summonerReviewSeq
    selectedStyleTags.value = props.player.reviewDto.tagCodeList || []
    comment.value = props.player.reviewDto.comment || ''
    isUp.value = props.player.reviewDto.isUp
  } else {
    reviewSeq.value = 0
    selectedStyleTags.value = []
    comment.value = ''
    isUp.value = null
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

const handleClose = () => {
  localReviewTags.value = [...props.reviewTags]; // 로컬 태그 목록 초기화
  emit('close');
  emit('clearSuggestTag');
}

const handleMouseEnter = (event: MouseEvent, tag: ReviewTagDto) => {
  hoveredTag.value = tag
  const target = event.currentTarget as HTMLElement
  const rect = target.getBoundingClientRect()

  tooltipStyle.value = {
    top: `${rect.top - 10}px`,
    left: `${rect.left + (rect.width / 2)}px`
  }
}

const handleSubmit = async () => {
  // 로그인 체크
  if (!authStore.isAuthenticated || !authStore.user) {
    toast.error('로그인이 필요합니다')
    return
  }

  // 평가 선택 체크
  if (isUp.value === null) {
    toast.error('평가를 선택해주세요')
    return
  }

  // 임시 태그를 제외한 태그 코드 목록 생성
  const filteredTagCodes = selectedStyleTags.value.filter(
      tagCode => !tagCode.startsWith('temp_')
  )

  // 임시 태그를 제외한 태그 DTO 목록 생성
  const filteredTagDtos = props.reviewTags
      .filter(tag =>
          filteredTagCodes.includes(tag.tagCode) &&
          !tag.tagCode.startsWith('temp_')
      )
      .map(tag => ({ ...tag }))

  // 리뷰 객체 생성
  const review: ReviewRequestDto = {
    summonerReviewSeq: props.player?.reviewDto?.summonerReviewSeq,
    reviewerSiteCode: authStore.user.memberSiteCode.toString(),
    reviewerPuuid: authStore.user.puuid,
    targetPuuid: props.player.puuid,
    isUp: isUp.value,
    comment: comment.value,
    tagCodeList: filteredTagCodes,
    matchId: props.player.matchId,
    tagDtoList: filteredTagDtos,
    reviewable: false,
    regDt: null
  }

  try {
    // 기존 리뷰가 있으면 업데이트, 없으면 새로 생성
    if (props.player.reviewDto?.summonerReviewSeq) {
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

/* 평가 버튼 스타일 */
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

/* 태그 선택 섹션 스타일 */
.tags-section {
  padding: 0 24px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.tags-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.suggest-tag-btn {
  padding: 6px 12px;
  border-radius: 8px;
  background: rgba(41, 121, 255, 0.1);
  border: 1px solid rgba(41, 121, 255, 0.3);
  color: #2979FF;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.suggest-tag-btn:hover {
  background: rgba(41, 121, 255, 0.2);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #fff;
}

.search-container {
  margin-bottom: 12px;
}

.tag-search {
  width: 100%;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: white;
  font-size: 14px;
}

.tag-search:focus {
  outline: none;
  border-color: #2979FF;
}

.category-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.category-tab {
  padding: 6px 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.05);
  border: none;
  color: #888;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s ease;
}

.category-tab:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.category-tab.active {
  background: #2979FF;
  color: white;
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
  padding: 8px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
}

.selected-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 8px;
  background: #2979FF;
  color: white;
  border-radius: 16px;
  font-size: 14px;
}

.remove-tag {
  margin-left: 6px;
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 0 2px;
}

.remove-tag:hover {
  opacity: 0.8;
}

/*.tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  height: 200px;
  overflow-x: auto;
}*/

.tag-container {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  gap: 8px;
  min-height: 200px;
  max-height: 200px;
  overflow-y: auto;
  padding: 4px;
}

.tag-container::-webkit-scrollbar {
  width: 6px;
}

.tag-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

.tag-container::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.tag-button {
  position: relative;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
  transition: all 0.2s ease;
  text-align: center;
  height: 39px;
}

.tag-button[data-up="true"] {
  background: rgba(41, 121, 255, 0.1);
  border: 1px solid rgba(41, 121, 255, 0.1);
  color: #2979FF;
}

.tag-button[data-up="false"] {
  background: rgba(235, 87, 87, 0.1);
  border: 1px solid rgba(235, 87, 87, 0.1);
  color: #EB5757;
}

.tag-button[data-up="true"]:hover:not(:disabled) {
  background: rgba(41, 121, 255, 0.15);
  border-color: rgba(41, 121, 255, 0.3);
  z-index: 20;
}

.tag-button[data-up="false"]:hover:not(:disabled) {
  background: rgba(235, 87, 87, 0.15);
  border-color: rgba(235, 87, 87, 0.3);
  z-index: 20;
}

.tag-button[data-up="true"].selected {
  background: rgba(41, 121, 255, 0.25) !important;
  border-color: #2979FF !important;
}

.tag-button[data-up="false"].selected {
  background: rgba(235, 87, 87, 0.25) !important;
  border-color: #EB5757 !important;
}

.tag-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.tag-suggest-button {
  background: rgb(185, 205, 253, 0.9);
  border: 1px solid rgba(0, 0, 0, 0.1);
  color: rgb(0, 0, 0);
  font-size: 20px;
}

.tooltip {
  position: fixed;
  transform: translate(-50%, -100%);
  background: #323232;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
  padding: 8px 12px;
  color: white;
  font-size: 14px;
  border-radius: 4px;
  white-space: normal;
  min-width: 200px;
  max-width: 300px;
  z-index: 9999;
  pointer-events: none;
}

.tooltip-arrow {
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%) rotate(45deg);
  width: 8px;
  height: 8px;
  background: #323232;
}

.tag-count {
  text-align: right;
  color: #888;
  font-size: 14px;
  margin-top: 8px;
}

/* 코멘트 섹션 스타일 */
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

  .category-tabs {
    padding-bottom: 12px;
  }

  .tag-container {
    min-height: 150px;
    max-height: 150px;
  }
}

</style>