<!-- src/components/summoner/modal/PreviousReviewModal.vue -->
<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <button class="modal-close" @click="$emit('close')">×</button>

      <div class="modal-body">
        <!-- Review Notice -->
        <div class="notice-banner">
          <div class="notice-icon">!</div>
          <div class="notice-text">이미 해당 소환사에게 작성한 리뷰가 있습니다. <br/>내역은 하단 게임상세정보를 통해 확인 가능합니다. 수정하시겠습니까?</div>
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
<!--            <div v-for="tag in player.reviewDto.tagDtoList"-->
<!--                 :key="tag.tagCode"-->
<!--                 class="tag">-->
<!--              {{ tag.tagValue }}-->
<!--            </div>-->
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
<!--          <button class="btn-cancel" @click="$emit('close')">취소</button>-->
          <button class="btn-rewrite" @click="$emit('rewrite')">리뷰 수정</button>
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
      <div class="game-details" :class="{ 'open': isDetailsOpen }">
        <div class="game-info">
          <div class="game-meta">
            <div class="game-type">{{ reviewedMatch.matchInfo.gameModeName }}</div>
            <div class="game-date">
              {{ formatDate(reviewedMatch.matchInfo.gameStartDt) }}
              <span class="bullet">•</span>
              {{ formatDuration(reviewedMatch.matchInfo.gameDuration) }}
            </div>
          </div>
        </div>

        <div class="teams-container">
          <!-- Blue Team -->
          <div class="team blue-team">
            <div class="team-header">블루팀</div>
            <div class="team-players">
              <div v-for="player in blueTeam"
                   :key="player.puuid"
                   class="player-card"
                   :class="{
                      'reviewed': player.puuid === props.player.puuid,
                      'me': player.puuid === authStore.user?.puuid
                   }"
              >
                <div class="player-champion">
                  <div class="champion-portrait">
                    <img :src="player.champProfileIconUrl" :alt="player.champName">
                    <div class="champion-level">{{ player.champLevel }}</div>
                  </div>
                </div>
                <div class="player-info">
                  <div class="player-identity">
                    <span class="name">{{ player.riotIdGameName }}</span>
                    <span class="tagline">#{{ player.riotIdTagline }}</span>
                  </div>
                  <div class="player-stats">
                    <div class="kda">
                      <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                      <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                      <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
                    </div>
                    <div v-if="calculateKDA(player) > 0" class="kda-ratio">
                      {{ calculateKDA(player).toFixed(2) }} KDA
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Red Team -->
          <div class="team red-team">
            <div class="team-header">레드팀</div>
            <div class="team-players">
              <div v-for="player in redTeam"
                   :key="player.puuid"
                   class="player-card"
                   :class="{
                      'reviewed': player.puuid === props.player.puuid,
                      'me': player.puuid === authStore.user?.puuid
                   }"
              >
                <div class="player-champion">
                  <div class="champion-portrait">
                    <img :src="player.champProfileIconUrl" :alt="player.champName">
                    <div class="champion-level">{{ player.champLevel }}</div>
                  </div>
                </div>
                <div class="player-info">
                  <div class="player-identity">
                    <span class="name">{{ player.riotIdGameName }}</span>
                    <span class="tagline">#{{ player.riotIdTagline }}</span>
                  </div>
                  <div class="player-stats">
                    <div class="kda">
                      <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                      <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                      <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
                    </div>
                    <div v-if="calculateKDA(player) > 0" class="kda-ratio">
                      {{ calculateKDA(player).toFixed(2) }} KDA
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ChevronDown, ThumbsUp, ThumbsDown } from 'lucide-vue-next'
import type { LolMatchInfoRes, LolMatchParticipant } from '@/types/match.ts'
import TagList from "@/components/common/TagList.vue";
import {useAuthStore} from "@/stores/auth.ts";

const props = defineProps<{
  reviewedMatch: LolMatchInfoRes
  player: LolMatchParticipant
}>()

const authStore = useAuthStore();

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'rewrite'): void
}>()

const isDetailsOpen = ref(false)

const blueTeam = computed(() =>
    props.reviewedMatch?.participantList.filter(p => p.teamId === 100)
)

const redTeam = computed(() =>
    props.reviewedMatch?.participantList.filter(p => p.teamId === 200)
)

const calculateKDA = (player: LolMatchParticipant) => {
  return player.deaths === 0
      ? player.kills + player.assists
      : (player.kills + player.assists) / player.deaths
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString()
}

const formatDuration = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
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
  margin: 20px 0; /* 상하 여백 추가 */
  scrollbar-width: thin;
  scrollbar-color: rgba(41, 121, 255, 0.3) transparent;
}

/* Webkit 스크롤바 스타일링 */
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

/* Game Info Section */
.game-info-section {
  margin-bottom: 24px;
}

.game-meta {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.game-type {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 6px;
}

.game-date {
  color: #888;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.bullet {
  color: #444;
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

/* Review Section */
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

/* Tags Section */
.tags-section {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.tag {
  background: rgba(41, 121, 255, 0.1);
  color: #2979FF;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  border: 1px solid rgba(41, 121, 255, 0.2);
}

/* Comment Section */
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

/* Action Buttons */
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

.btn-cancel {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #fff;
}

.btn-cancel:hover {
  background: rgba(255, 255, 255, 0.1);
}

.btn-rewrite {
  background: #2979FF;
  border: none;
  color: white;
}

.btn-rewrite:hover {
  background: #2262CC;
}

.game-details {
  max-height: none; /* max-height 제거 */
  height: auto; /* 자동 높이 */
  overflow: visible; /* overflow 제거 */
  background: rgba(255, 255, 255, 0.02);
  display: none; /* 초기에는 숨김 */
}

.game-info {
  padding: 24px 24px 0;
}

.game-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.game-type {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  padding: 6px 12px;
  border-radius: 6px;
}

.game-date {
  color: #888;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.game-details.open {
  display: block; /* 열릴 때 보이게 */
}

.teams-container {
  padding: 24px;
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  height: auto; /* 자동 높이 */
}

.team {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  padding: 20px;
}

.team-header {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-left: 8px;
}

.blue-team .team-header {
  color: #2979FF;
}

.red-team .team-header {
  color: #FF5252;
}

.team-players {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.player-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background: rgba(30, 30, 30, 0.95);
  transition: all 0.2s ease;
}

.player-card.reviewed {
  background: rgba(76, 175, 80, 0.1);
  border: 1px solid rgba(76, 175, 80, 0.2);
}

.player-card.me {
  background: rgba(41, 121, 255, 0.1);
  border: 1px solid rgba(41, 121, 255, 0.2);
}

.player-champion {
  position: relative;
}

.champion-portrait {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  overflow: hidden;
  background: #000;
  border: 2px solid rgba(255, 255, 255, 0.1);
}

.champion-portrait img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.champion-level {
  position: absolute;
  bottom: -4px;
  right: -4px;
  background: #000;
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  padding: 1px 4px;
  font-size: 11px;
  font-weight: 600;
}

.player-info {
  flex: 1;
  min-width: 0;
}

.player-identity {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.player-identity .name {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
}

.player-identity .tagline {
  font-size: 12px;
  color: #666;
}

.player-stats {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  justify-content: space-between;
}

.kda {
  font-size: 14px;
  color: #888;
  white-space: nowrap;
}

.kda .highlight {
  color: #4CAF50;
}

.kda .highlight-death {
  color: #FF5252;
}

.kda-ratio {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
}

@media (min-width: 768px) {
  .teams-container {
    grid-template-columns: 1fr 1fr;
  }
}

/* Details Toggle */
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
  }

  .details-toggle {
    position: sticky;
    bottom: 0;
    background: #141414;
  }
}

</style>