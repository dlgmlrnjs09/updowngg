<!-- src/views/SummonerPage.vue -->
<template>
  <div class="container">
    <!-- 프로필 섹션 -->
    <div class="profile-section">
      <div class="profile-icon">
        <img :src="summonerInfo?.lolSummonerDto.profileIconUrl" alt="">
      </div>
      <div class="profile-info">
        <div class="profile-header">
          <div class="name-section">
            <div class="summoner-name">{{ summonerInfo?.riotAccountInfoEntity.gameName }}</div>
            <div class="profile-stats">평가 312회 · 최근 30일 동안 89회 평가됨</div>
          </div>
          <button class="detail-button" @click="showDetailModal = true">상세 평가 보기</button>
        </div>
        <div class="rating-stats">
          <div class="rating-item">
            <span class="rating-value">4.8</span>
            <span class="rating-label">실력</span>
          </div>
          <div class="rating-item">
            <span class="rating-value">4.6</span>
            <span class="rating-label">팀워크</span>
          </div>
          <div class="rating-item">
            <span class="rating-value">4.9</span>
            <span class="rating-label">매너</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 게임 리스트 -->
    <div class="games-section">
      <div v-for="game in games" :key="game.gameId" class="game-item">
        <div class="game-header">
          <div class="game-info">
            <div class="game-type">{{ game.gameType }}</div>
            <div class="game-date">{{ game.gameDate }}</div>
            <div class="game-duration">{{ game.gameDuration }}</div>
          </div>
          <div class="game-kda">{{ game.kda }}</div>
        </div>
        <div class="teams-container">
          <div class="team">
            <div class="team-title">아군 팀</div>
            <div class="team-members">
              <div v-for="player in getTeamPlayers(game.players, 'blue')"
                   :key="player.summonerName"
                   class="player"
                   @click="openReviewModal(player)">
                <div class="champion-icon"></div>
                <div class="player-name">{{ player.summonerName }}</div>
              </div>
            </div>
          </div>
          <div class="team">
            <div class="team-title">상대 팀</div>
            <div class="team-members">
              <div v-for="player in getTeamPlayers(game.players, 'red')"
                   :key="player.summonerName"
                   class="player"
                   @click="openReviewModal(player)">
                <div class="champion-icon"></div>
                <div class="player-name">{{ player.summonerName }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 상세 평가 모달 -->
    <div v-if="showDetailModal" class="modal-overlay" @click.self="showDetailModal = false">
      <div class="modal-content">
        <!-- 모달 내용은 별도 컴포넌트로 분리 예정 -->
        <DetailModal
            v-if="showDetailModal"
            :name="summonerName"
            @close="showDetailModal = false"
        />
      </div>
    </div>

    <!-- 평가 작성 모달 -->
    <div v-if="showReviewModal" class="modal-overlay" @click.self="showReviewModal = false">
      <div class="modal-content">
        <!-- 모달 내용은 별도 컴포넌트로 분리 예정 -->
        <ReviewModal
            v-if="showReviewModal"
            :player-name="selectedPlayer!"
            @close="showReviewModal = false"
            @submit="handleReviewSubmit"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import DetailModal from "@/components/summoner/main/modal/DetailModal.vue";
import ReviewModal from "@/components/summoner/main/modal/ReviewModal.vue";
import type {LolSummonerProfileResDto } from "@/types/summoner.ts";
import {summonerApi} from "@/api/summoner.ts";

// 라우터에서 소환사 이름 가져오기
const route = useRoute()
const summonerInfo = ref<LolSummonerProfileResDto | null>(null)

// 모달 상태 관리
const showDetailModal = ref(false)
const showReviewModal = ref(false)

// 임시 데이터
const games = ref([
  {
    gameId: '1',
    gameType: '솔로 랭크',
    gameDate: '12월 10일 15:30',
    gameDuration: '32:15',
    kda: '8/2/6',
    players: [
      { summonerName: 'Hide on bush', team: 'blue', championId: 1 },
      { summonerName: 'Faker', team: 'blue', championId: 2 },
      { summonerName: 'Chovy', team: 'blue', championId: 3 },
      { summonerName: 'Deft', team: 'blue', championId: 4 },
      { summonerName: 'Keria', team: 'blue', championId: 5 },
      { summonerName: 'Zeus', team: 'red', championId: 6 },
      { summonerName: 'Oner', team: 'red', championId: 7 },
      { summonerName: 'Gumayusi', team: 'red', championId: 8 },
      { summonerName: 'Ruler', team: 'red', championId: 9 },
      { summonerName: 'Lehends', team: 'red', championId: 10 },
    ]
  }
  // 추가 게임 데이터...
])

// 팀별 플레이어 필터링
const getTeamPlayers = (players: any[], team: string) => {
  return players.filter(player => player.team === team)
}

// 평가 모달 열기
const openReviewModal = (player: any) => {
  // 자기 자신은 평가할 수 없음
  if (player.summonerName === summonerName) return
  showReviewModal.value = true
  // 선택된 플레이어 정보 저장...
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(async () => {
  try {
    const summonerId = route.params.name as string
    const tagLine = route.params.tag as string

    const response = await summonerApi.getInfo(summonerId, tagLine)
    summonerInfo.value = response.data
  } catch (error) {
    console.error('Failed to fetch summoner info:', error)
  }
})
</script>

<style scoped>
.container {
  max-width: 1000px;
  margin: 80px auto 0;  /* 헤더 높이만큼 상단 여백 */
  padding: 20px;
}

.profile-section {
  background: #141414;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.profile-icon {
  width: 80px;
  height: 80px;
  background: #2979FF;
  border-radius: 12px;
}

.profile-header {
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.summoner-name {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
}

.profile-stats {
  color: #888;
  font-size: 14px;
}

.detail-button {
  background: rgba(41, 121, 255, 0.1);
  border: 1px solid rgba(41, 121, 255, 0.2);
  color: #2979FF;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.detail-button:hover {
  background: rgba(41, 121, 255, 0.2);
}

.rating-stats {
  display: flex;
  gap: 24px;
}

.rating-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-value {
  font-weight: 600;
  color: #2979FF;
}

.rating-label {
  color: #888;
  font-size: 14px;
}

.games-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.game-item {
  background: #141414;
  border-radius: 8px;
  padding: 20px;
}

.game-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.game-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.game-type, .game-date, .game-duration {
  color: #888;
  font-size: 14px;
}

.teams-container {
  display: flex;
  gap: 40px;
}

.team {
  flex: 1;
}

.team-title {
  color: #666;
  font-size: 13px;
  margin-bottom: 8px;
}

.player {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.player:hover {
  background: rgba(255, 255, 255, 0.05);
}

.champion-icon {
  width: 24px;
  height: 24px;
  background: #2979FF;
  border-radius: 4px;
}

.player-name {
  font-size: 14px;
  color: #888;
  transition: color 0.2s;
}

.player:hover .player-name {
  color: white;
}

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
}

.profile-icon img {
  width:100%;
  height:100%;
  object-fit:cover;
}
</style>