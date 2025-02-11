<!-- src/components/summoner/modal/PreviousReviewModal.vue -->
<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <button class="modal-close" @click="$emit('close')">×</button>

      <div class="modal-body">
        <!-- Review Notice -->
        <div class="notice-banner">
          <div class="notice-icon">!</div>
          <div class="notice-text">이미 해당 소환사에게 작성한 리뷰가 있습니다.<br/>리뷰를 수정하거나 삭제할 수 있습니다.</div>
        </div>

        <!-- Previous Review Section -->
        <div class="review-section">
          <div class="review-header">
            <div class="reviewed-player">
              <div class="champion-icon">
                <img
                    :src="player.champProfileIconUrl"
                    :alt="player.champName"
                >
              </div>
              <div class="player-details">
                <div class="player-name">{{ player.riotIdGameName }}</div>
                <div class="player-tag">#{{ player.riotIdTagline }}</div>
              </div>
            </div>

            <div class="review-rating" :class="{ 'up': player.reviewDto.isUp, 'down': !player.reviewDto.isUp }">
              <component
                  :is="player.reviewDto.isUp ? ThumbsUp : ThumbsDown"
                  class="rating-icon"
                  :size="20"
              />
            </div>
          </div>

          <!-- Tags -->
          <div class="tags-section">
            <TagList :tags="player.reviewDto.tagDtoList || []" size="medium" :is-show-count="false"/>
          </div>

          <!-- Comment -->
          <div v-if="player.reviewDto.comment" class="comment-section">
            <div class="comment-text">
              {{ player.reviewDto.comment }}
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="action-buttons">
          <button class="btn-rewrite" @click="$emit('rewrite')">리뷰 수정</button>
          <button class="btn-delete" @click="handleDelete">리뷰 삭제</button>
        </div>
      </div>

      <!-- Game Details Toggle -->
      <button class="details-toggle" @click="isDetailsOpen = !isDetailsOpen">
        게임 상세 정보 {{ isDetailsOpen ? '닫기' : '보기' }}
        <ChevronDown
            class="toggle-icon"
            :class="{ 'rotate': isDetailsOpen }"
        />
      </button>

      <!-- Game Details Section -->
      <GameDetailSection
          :match="reviewedMatch"
          :is-open="isDetailsOpen"
          :reviewed-puuid="player.puuid"
          :current-user-puuid="authStore.user?.puuid"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ChevronDown, ThumbsUp, ThumbsDown } from 'lucide-vue-next'
import type { LolMatchInfoRes, LolMatchParticipant } from '@/types/match.ts'
import TagList from "@/components/common/TagList.vue"
import GameDetailSection from '@/components/match/GameDetailSection.vue'
import { useAuthStore } from "@/stores/auth.ts"
import {useToast} from "vue-toastification";

const props = defineProps<{
  reviewedMatch: LolMatchInfoRes
  player: LolMatchParticipant
}>()

const authStore = useAuthStore()
const toast = useToast()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'rewrite'): void
  (e: 'delete', reviewSeq: number): void
}>()

const isDetailsOpen = ref(false)

const handleDelete = () => {
  if (!props.player.reviewDto?.summonerReviewSeq) {
    toast.error('리뷰가 존재하지 않습니다.')
    return
  }

  if (confirm('정말로 이 리뷰를 삭제하시겠습니까?')) {
    emit('delete', props.player.reviewDto.summonerReviewSeq)
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  display: grid;
  place-items: center;
  z-index: 1000;
}

.modal-content {
  background: #141414;
  border-radius: 20px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  position: relative;
  color: white;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  margin: 20px 0;
  scrollbar-width: thin;
  scrollbar-color: rgba(41, 121, 255, 0.3) transparent;
}

.modal-content::-webkit-scrollbar {
  width: 6px;
}

.modal-content::-webkit-scrollbar-track {
  background: transparent;
}

.modal-content::-webkit-scrollbar-thumb {
  background-color: rgba(41, 121, 255, 0.3);
  border-radius: 3px;
}

.modal-content::-webkit-scrollbar-thumb:hover {
  background-color: rgba(41, 121, 255, 0.5);
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  color: #666;
  font-size: 24px;
  cursor: pointer;
  z-index: 10;
  width: 32px;
  height: 32px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  transition: all 0.2s;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.modal-body {
  padding: 24px;
}

.notice-banner {
  background: #1B2838;
  border: 1px solid #2979FF;
  padding: 16px;
  border-radius: 12px;
  font-size: 15px;
  margin-top: 40px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.notice-icon {
  width: 24px;
  height: 24px;
  background: rgba(41, 121, 255, 0.1);
  border: 2px solid #2979FF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2979FF;
  font-weight: bold;
  font-size: 16px;
}

.notice-text {
  color: #2979FF;
  font-weight: 500;
  flex: 1;
}

.review-section {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.reviewed-player {
  display: flex;
  align-items: center;
  gap: 12px;
}

.champion-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  overflow: hidden;
  background: #000;
  border: 2px solid rgba(255, 255, 255, 0.1);
}

.champion-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.player-details {
  display: flex;
  flex-direction: column;
}

.player-name {
  font-size: 16px;
  font-weight: 500;
  color: #fff;
}

.player-tag {
  font-size: 13px;
  color: #888;
}

.review-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.review-rating.up {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

.review-rating.down {
  background: rgba(255, 82, 82, 0.1);
  color: #FF5252;
}

.rating-icon {
  opacity: 0.9;
}

.tags-section {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.comment-section {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  padding: 16px;
}

.comment-text {
  color: #fff;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.action-buttons button {
  flex: 1;
  padding: 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-rewrite {
  background: #2979FF;
  border: none;
  color: white;
}

.btn-rewrite:hover {
  background: #2262CC;
}

.details-toggle {
  width: 100%;
  padding: 16px;
  background: rgba(255, 255, 255, 0.02);
  border: none;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: #2979FF;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.details-toggle:hover {
  background: rgba(41, 121, 255, 0.05);
}

.toggle-icon {
  transition: transform 0.3s ease;
}

.toggle-icon.rotate {
  transform: rotate(180deg);
}

.btn-delete {
  background: rgba(255, 82, 82, 0.1);
  border: 1px solid #FF5252;
  color: #FF5252;
}

.btn-delete:hover {
  background: rgba(255, 82, 82, 0.2);
}

@media (max-width: 640px) {
  .modal-content {
    width: 95%;
    margin: 16px;
    max-height: 85vh;
  }

  .modal-body {
    padding: 16px;
    flex: 1;
    overflow-y: auto;
  }

  .review-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .review-rating {
    align-self: flex-start;
  }

  .action-buttons {
    flex-direction: column;
    position: sticky;
    bottom: 0;
    background: #141414;
    padding-top: 16px;
    gap: 8px;
  }

  .details-toggle {
    position: sticky;
    bottom: 0;
    background: #141414;
  }
}
</style>