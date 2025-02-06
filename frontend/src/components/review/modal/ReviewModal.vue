<template>
  <div class="modal-overlay" @click="handleClose">
    <div class="modal-content" @click.stop>
      <button class="modal-close" @click="handleClose">×</button>

      <div class="modal-header">
        <div class="header-content">
          <div class="profile-info">
            <img
                :src="player.champProfileIconUrl"
                class="profile-icon"
                alt="프로필"
            />
            <h2 class="modal-title">
              {{ player.riotIdGameName }}
              <span class="player-tag">#{{ player.riotIdTagline }}</span>
            </h2>
          </div>
          <div class="stepper-info">
            <span class="step-text">STEP {{ currentStep }}/{{ totalSteps }}</span>
            <div class="progress-bar">
              <div
                  class="progress-fill"
                  :style="{ width: `${(currentStep / totalSteps) * 100}%` }"
              ></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Step 1: UP/DOWN 평가 -->
      <div v-if="currentStep === 1" class="step-container">
        <h3 class="step-title">이 소환사의 플레이는 어땠나요?</h3>
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
              <span>긍정적</span>
            </button>
            <button
                class="review-button dislike"
                :class="{ selected: isUp === false }"
                @click="setRating(false)"
            >
              <svg class="button-icon" viewBox="0 0 24 24">
                <path d="M9.4 16H3a2 2 0 0 1-2-2v-2.104a2 2 0 0 1 .15-.762L4.246 3.62A1 1 0 0 1 5.17 3H22a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1h-3.482a1 1 0 0 0-.817.423l-5.453 7.726a.5.5 0 0 1-.632.159L9.802 21.4A2.5 2.5 0 0 1 8.496 18.55L9.4 16zM17 14.586V6H5.84L3 12.896V14h6.4a2 2 0 0 1 1.938 2.493l-.903 3.548a.5.5 0 0 0 .261.571l.661.33 4.71-6.672c.25-.354.57-.644.933-.858zM19 13h2V5h-2v8z"/>
              </svg>
              <span>부정적</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Step 2: 태그 선택 -->
      <div v-if="currentStep === 2" class="step-container">
<!--        <h2 class="section-title">어떤 점이 {{ isUp ? '좋았나요?' : '아쉬웠나요?' }}</h2>-->
        <h3 class="step-title">플레이 스타일은 어땠나요?</h3>
        <div class="tags-section">
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
                :disabled="isTagLimitReached"
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
      </div>

      <!-- Step 3: 익명 설정 -->
      <div v-if="currentStep === 3" class="step-container">
        <h3 class="step-title">익명으로 평가하시겠어요?</h3>
        <div class="anonymous-section">
          <div class="anonymous-toggle">
            <span class="anonymous-label">익명으로 평가하기</span>
            <button
                class="toggle-button"
                :class="{ 'toggle-on': isAnonymous }"
                @click="toggleAnonymous"
            >
              <div class="toggle-slider"></div>
            </button>
          </div>
          <p class="anonymous-description">
            {{ isAnonymous ? '다른 소환사에게 평가자의 닉네임이 표시되지 않습니다.' : '다른 소환사에게 평가자의 닉네임이 표시됩니다.' }}
          </p>
        </div>
      </div>

      <!-- Step 4: 코멘트 작성 -->
      <div v-if="currentStep === 4" class="step-container">
        <h3 class="step-title">추가로 남기고 싶은 말이 있나요?</h3>
        <div class="comment-section">
          <textarea
              v-model="comment"
              class="comment-input"
              placeholder="이 소환사와의 게임은 어땠나요? (선택사항)"
          ></textarea>
        </div>
      </div>

      <!-- 네비게이션 버튼 -->
      <div class="navigation-buttons">
        <button
            class="nav-button back"
            @click="previousStep"
            :disabled="currentStep === 1"
        >
          이전
        </button>
        <button
            class="nav-button next"
            @click="nextStep"
            :disabled="!canProceed"
        >
          {{ currentStep === totalSteps ? '완료' : '다음' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useToast } from 'vue-toastification'
import type { ReviewRequestDto, ReviewTagDto, ReviewTagCategoryDto, ReviewTagSuggestDto } from '@/types/review'
import { useAuthStore } from '@/stores/auth'
import type { LolMatchParticipant } from '@/types/match'

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
  (e: 'clearSuggestTag'): void
}>()

const authStore = useAuthStore()
const toast = useToast()

// 스텝퍼 관련 상태
const currentStep = ref(1)
const totalSteps = 4

// 상태 관리
const isUp = ref<boolean | null>(null)
const selectedStyleTags = ref<string[]>([])
const comment = ref('')
const reviewSeq = ref(0)
const searchQuery = ref('')
const selectedCategory = ref('all')
const hoveredTag = ref<ReviewTagDto | null>(null)
const tooltipStyle = ref({})
const localReviewTags = ref<ReviewTagDto[]>([])
const isAnonymous = ref(false)
const isSubmitting = ref(false)

// 컴포넌트 마운트 시 초기화
onMounted(() => {
  init()
  localReviewTags.value = [...props.reviewTags]
})

// 유효성 검사
const canProceed = computed(() => {
  switch (currentStep.value) {
    case 1:
      return isUp.value !== null
    case 2:
      return true
    case 3:
      return true // 익명 설정은 선택사항
    case 4:
      return true // 코멘트는 선택사항
    default:
      return false
  }
})

const isTagLimitReached = computed(() => {
  return selectedStyleTags.value.length >= 5
})

// 필터링된 태그
const filteredTags = computed(() => {
  let filtered = localReviewTags.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    const searchTerms = query.split(/\s+/).filter(term => term.length > 0)

    filtered = filtered.filter(tag => {
      const tagValue = tag.tagValue.toLowerCase()
      const tagDesc = tag.tagDescription?.toLowerCase() || ''
      return searchTerms.every(term =>
          tagValue.includes(term) || tagDesc.includes(term)
      )
    })
  }

  if (selectedCategory.value !== 'all') {
    filtered = filtered.filter(tag => tag.tagCategory === selectedCategory.value)
  }

  return filtered
})

// suggestTag 관련 watch
watch(() => props.suggestTag, (newTags) => {
  if (Array.isArray(newTags) && newTags.length > 0) {
    newTags.forEach(tag => {
      if (tag?.tagValue) {
        const tempTagCode = `temp_${tag.tagValue.replace(/\s+/g, '_')}`
        if (!selectedStyleTags.value.includes(tempTagCode)) {
          selectedStyleTags.value.push(tempTagCode)

          const newTagDto: ReviewTagDto = {
            tagCode: tempTagCode,
            tagValue: tag.tagValue,
            tagDescription: tag.tagDescription,
            tagCategory: tag.tagCategory?.toString() || '',
            tagUpdown: tag.isUp || false,
            frequentCount: 0
          }

          localReviewTags.value.push(newTagDto)
        }
      }
    })
  }
}, { immediate: true })

// 초기화
const init = () => {
  if (props.player.reviewDto) {
    reviewSeq.value = props.player.reviewDto.summonerReviewSeq
    selectedStyleTags.value = props.player.reviewDto.tagCodeList || []
    comment.value = props.player.reviewDto.comment || ''
    isUp.value = props.player.reviewDto.isUp
    isAnonymous.value = props.player.reviewDto.isAnonymous || false
  } else {
    reviewSeq.value = 0
    selectedStyleTags.value = []
    comment.value = ''
    isUp.value = null
    isAnonymous.value = false
  }
}

// 메서드
const setRating = (value: boolean) => {
  isUp.value = isUp.value === value ? null : value
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

const handleMouseEnter = (event: MouseEvent, tag: ReviewTagDto) => {
  hoveredTag.value = tag
  const target = event.currentTarget as HTMLElement
  const rect = target.getBoundingClientRect()

  tooltipStyle.value = {
    top: `${rect.top - 10}px`,
    left: `${rect.left + (rect.width / 2)}px`
  }
}

const getTagLabel = (tagCode: string) => {
  const tag = localReviewTags.value.find(t => t.tagCode === tagCode)
  return tag ? tag.tagValue : tagCode
}

const toggleAnonymous = () => {
  isAnonymous.value = !isAnonymous.value
}

const previousStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const nextStep = async () => {
  if (currentStep.value < totalSteps) {
    currentStep.value++
  } else {
    await handleSubmit()
  }
}

const handleClose = () => {
  localReviewTags.value = [...props.reviewTags]
  emit('close')
  emit('clearSuggestTag')
}

const handleTagSuggestClick = () => {
  emit('openTagSuggest')
}

const handleSubmit = async () => {
  if (!authStore.isAuthenticated || !authStore.user) {
    toast.error('로그인이 필요합니다')
    return
  }

  isSubmitting.value = true

  try {
    const filteredTagCodes = selectedStyleTags.value.filter(
        tagCode => !tagCode.startsWith('temp_')
    )

    const filteredTagDtos = props.reviewTags
        .filter(tag =>
            filteredTagCodes.includes(tag.tagCode) &&
            !tag.tagCode.startsWith('temp_')
        )
        .map(tag => ({ ...tag }))

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
      regDt: null,
      isAnonymous: isAnonymous.value
    }

    emit('submit', review)
    emit('close')
    toast.success('평가가 성공적으로 제출되었습니다')
  } catch (error) {
    toast.error('평가 제출 중 오류가 발생했습니다')
  } finally {
    isSubmitting.value = false
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
  position: relative;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-right: 40px;
}

.profile-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.profile-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
}

.modal-title {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  margin: 0;
}

.stepper-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.step-text {
  font-size: 14px;
  color: #888;
  font-weight: 500;
}

.modal-close {
  position: absolute;
  top: 12px;
  right: 24px;
  background: none;
  border: none;
  color: #888;
  font-size: 24px;
  cursor: pointer;
  padding: 8px;
  transition: color 0.2s ease;
  z-index: 1;
}

.modal-close:hover {
  color: #fff;
}

/* 프로그레스 바 */
.progress-bar {
  height: 4px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #2979FF;
  transition: width 0.3s ease;
  border-radius: 2px;
}

/* 스텝 컨테이너 */
.step-container {
  padding: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #fff;
}

.step-title {
  font-size: 18px;
  font-weight: 600;
  color: white;
  margin-bottom: 24px;
}

/* 리뷰 버튼 */
.review-section {
  padding: 0;
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

.review-button span {
  font-size: 14px;
  font-weight: 500;
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

/* 태그 섹션 */
.tags-section {
  padding: 0 24px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
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

.tag-container {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  gap: 8px;
  min-height: 140px;
  max-height: 140px;
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

.tag-suggest-button:disabled {
  background: rgba(185, 205, 253, 0.3);
  color: rgba(0, 0, 0, 0.5);
  cursor: not-allowed;
}

.tag-suggest-button:hover:not(:disabled) {
  transform: translateY(-1px);
  background: rgb(185, 205, 253, 1);
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

.tag-count {
  text-align: right;
  color: #888;
  font-size: 14px;
  margin-top: 8px;
}

/* 익명 섹션 */
.anonymous-section {
  padding: 24px;
}

.anonymous-toggle {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  background: rgba(255, 255, 255, 0.05);
  padding: 16px;
  border-radius: 8px;
}

.anonymous-label {
  font-size: 15px;
  color: #fff;
}

.toggle-button {
  position: relative;
  width: 48px;
  height: 24px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.toggle-button.toggle-on {
  background: #2979FF;
}

.toggle-slider {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 20px;
  height: 20px;
  background: white;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.toggle-button.toggle-on .toggle-slider {
  transform: translateX(24px);
}

.anonymous-description {
  font-size: 13px;
  color: #888;
  margin-top: 8px;
}

/* 코멘트 섹션 */
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
  min-height: 120px;
  font-family: inherit;
}

.comment-input:focus {
  outline: none;
  border-color: #2979FF;
}

/* 네비게이션 버튼 */
.navigation-buttons {
  display: flex;
  justify-content: space-between;
  padding: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  gap: 12px;
}

.nav-button {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 100px;
  border: none;
}

.nav-button.back {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-button.back:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.15);
}

.nav-button.next {
  background: #2979FF;
  color: white;
}

.nav-button.next:hover:not(:disabled) {
  background: #2262CC;
}

.nav-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.tooltip {
  position: fixed;
  transform: translate(-50%, -100%);
  background: #323232;
  padding: 8px 12px;
  color: white;
  font-size: 14px;
  border-radius: 4px;
  white-space: normal;
  min-width: 200px;
  max-width: 300px;
  z-index: 9999;
  pointer-events: none;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
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

.player-tag {
  font-size: 14px;
  color: #666;
  margin-left: 5px;
}

@media (max-width: 480px) {
  .modal-content {
    width: 95%;
  }

  .step-container {
    padding: 20px;
  }

  .review-button {
    padding: 20px;
  }

  .button-icon {
    width: 28px;
    height: 28px;
  }

  .navigation-buttons {
    padding: 20px;
  }

  .nav-button {
    min-width: 80px;
    padding: 12px 16px;
  }

  .category-tabs {
    padding-bottom: 12px;
  }

  .tag-container {
    min-height: 150px;
    max-height: 150px;
  }
}

.button-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 20px;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #ffffff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>