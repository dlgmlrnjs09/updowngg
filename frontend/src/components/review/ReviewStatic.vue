<template>
  <div class="recent-reviews">
    <div class="reviewer-badge">
      <UserCircle class="reviewer-icon" size="14"/>
      <span>내 리뷰</span>
    </div>
    <div class="reviews-container">
      <div class="reviews-list">
        <div class="review-item">
          <div class="review-left">
            <ThumbsUp
                class="thumb-icon"
                :class="{
               'up': review.isUp,
               'down': !review.isUp
             }"
            />
          </div>
          <div class="review-content-wrapper">
            <div class="review-main" :class="{ 'center-content': !review.tagDtoList?.length }">
              <div v-if="review.tagDtoList?.length" class="review-tags">
                <TagList :tags="review.tagDtoList" size="small" :is-show-count="false"/>
              </div>
              <div class="review-content">
                {{ review.comment }}
              </div>
            </div>
            <button class="edit-button" @click="$emit('edit-review', review)">
              수정
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ThumbsUp, UserCircle } from 'lucide-vue-next';
import type { ReviewRequestDto } from "@/types/review.ts";
import TagList from "@/components/common/TagList.vue";

defineProps<{
  review: ReviewRequestDto;
  title?: string;
  showProfile?: boolean;
}>();

defineEmits<{
  (e: 'edit-review', review: ReviewRequestDto): void;
}>();
</script>

<style scoped>
.recent-reviews {
  margin-top: 16px;
  background: #1A1A1A;
  border-radius: 8px;
  border: 1px solid rgba(41, 121, 255, 0.2);
  padding: 32px 20px 20px;
  position: relative;
}

.reviewer-badge {
  position: absolute;
  top: 12px;
  left: 20px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: #2979FF;
  padding: 4px 8px;
  border-radius: 4px;
  color: #FFFFFF;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

.reviewer-icon {
  color: #FFFFFF;
}

.reviews-container {
  position: relative;
  margin-bottom: 2px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.review-item {
  width: 100%;
  background: rgba(41, 121, 255, 0.05);
  border-radius: 8px;
  padding: 20px;
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 16px;
  border: 1px solid rgba(41, 121, 255, 0.1);
}

.review-left {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
}

.thumb-icon {
  width: 40px;
  height: 40px;
}

.thumb-icon.up {
  color: #4CAF50;
}

.thumb-icon.down {
  color: #FF5252;
  transform: rotate(180deg);
}

.review-content-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  min-width: 0;
  height: 100%;
}

.review-main {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.review-main.center-content {
  justify-content: center;
}

.review-tags {
  display: flex;
  align-items: center;
}

.review-content {
  color: #ffffff;
  font-size: 14px;
  line-height: 1.6;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  text-align: left;
  word-break: break-word;
}

.edit-button {
  background: rgba(41, 121, 255, 0.1);
  border: 1px solid #2979FF;
  color: #2979FF;
  padding: 8px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-button:hover {
  background: rgba(41, 121, 255, 0.2);
}

@media (max-width: 768px) {
  .review-content-wrapper {
    flex-direction: column;
    align-items: flex-start;
  }

  .edit-button {
    width: 100%;
    margin-top: 8px;
  }
}
</style>