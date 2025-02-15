<template>
  <div class="current-game-container">
    <div class="current-game-title">
      <div class="title-left">
        <GamepadIcon class="game-icon" />
        <span class="game-status">진행 중인 게임</span>
        <span class="separator">·</span>
        <span class="game-duration">{{formattedGameDuration}}</span>
      </div>
      <div class="title-right">
        <span class="game-mode">{{currentMatchInfoDto.matchInfoDto.gameModeName}}</span>
      </div>
    </div>

    <div class="current-game-teams">
      <!-- Blue Team -->
      <div class="current-game-team blue">
        <div class="team-header">
          블루팀
        </div>
        <div class="team-players">
          <div
              v-for="player in blueTeamPlayers"
              :key="player.summonerInfoDto.puuid"
              class="current-game-player"
          >
            <div class="player-champion">
              <div class="champion-icon">
                <img :src="player.playerDto.championIconUrl" :alt="player.playerDto.championId.toString()">
              </div>
            </div>

            <div class="player-info">
              <div class="player-name-wrapper">
                <div v-if="player.playerDto.leagueDto" class="player-tier-tag" :style="{
                  background: `${player.playerDto.leagueDto.tierColor}1A`,
                  color: player.playerDto.leagueDto.tierColor
                }">
                  {{ player.playerDto.leagueDto.acronymTier }}{{ convertTierRomanToNumber(player.playerDto.leagueDto.rank) }}
                </div>
                <span
                    class="player-name"
                    @click.stop="goSelectedSummonerProfile(player.summonerInfoDto.gameName, player.summonerInfoDto.tagLine)"
                >
                  {{ player.summonerInfoDto.gameName }}
                </span>
                <span class="player-tag">#{{ player.summonerInfoDto.tagLine }}</span>
              </div>
            </div>

            <div class="player-reviews" :class="{ 'no-tags': !player.frequentTagDtoList?.length }">
              <div class="review-stats">
                <div class="review-up">
                  <ThumbsUp class="thumb-icon up" />
                  <span class="count">{{ player.reviewStatsDto?.upCount || 0 }}</span>
                </div>
                <div class="review-down">
                  <ThumbsDown class="thumb-icon down" />
                  <span class="count">{{ player.reviewStatsDto?.downCount || 0 }}</span>
                </div>
              </div>

              <div class="top-tags" v-if="player.frequentTagDtoList?.length">
                <TagList class="match-tag-list" :tags="player.frequentTagDtoList || null" size="xSmall" is-show-count/>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Red Team -->
      <div class="current-game-team red">
        <div class="team-header">
          레드팀
        </div>
        <div class="team-players">
          <div
              v-for="player in redTeamPlayers"
              :key="player.summonerInfoDto.puuid"
              class="current-game-player"
          >
            <div class="player-champion">
              <div class="champion-icon">
                <img :src="player.playerDto.championIconUrl" :alt="player.playerDto.championId.toString()">
              </div>
            </div>

            <div class="player-info">
              <div class="player-name-wrapper">
                <div v-if="player.playerDto.leagueDto" class="player-tier-tag" :style="{
                  background: `${player.playerDto.leagueDto.tierColor}1A`,
                  color: player.playerDto.leagueDto.tierColor
                }">
                  {{ player.playerDto.leagueDto.acronymTier }}{{ convertTierRomanToNumber(player.playerDto.leagueDto.rank) }}
                </div>
                <span
                    class="player-name"
                    @click.stop="goSelectedSummonerProfile(player.summonerInfoDto.gameName, player.summonerInfoDto.tagLine)"
                >
                  {{ player.summonerInfoDto.gameName }}
                </span>
                <span class="player-tag">#{{ player.summonerInfoDto.tagLine }}</span>
              </div>
            </div>

            <div class="player-reviews" :class="{ 'no-tags': !player.frequentTagDtoList?.length }">
              <div class="review-stats">
                <div class="review-up">
                  <ThumbsUp class="thumb-icon up" />
                  <span class="count">{{ player.reviewStatsDto?.upCount || 0 }}</span>
                </div>
                <div class="review-down">
                  <ThumbsDown class="thumb-icon down" />
                  <span class="count">{{ player.reviewStatsDto?.downCount || 0 }}</span>
                </div>
              </div>

              <div class="top-tags" v-if="player.frequentTagDtoList?.length">
                <TagList class="match-tag-list" :tags="player.frequentTagDtoList || null" size="xSmall" is-show-count/>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ThumbsUp, ThumbsDown, GamepadIcon } from 'lucide-vue-next';
import {computed, ref, onMounted, onUnmounted} from 'vue';
import { useRouter } from 'vue-router';
import type {CurrentMatchInfoDto} from "@/types/match.ts";
import {goSelectedSummonerProfile} from "@/utils/common.ts";
import TagList from "@/components/common/TagList.vue";
import {convertTierRomanToNumber} from "@/utils/common.ts";

const props = defineProps<{
  currentMatchInfoDto: CurrentMatchInfoDto;
}>();

const blueTeamPlayers = computed(() =>
    props.currentMatchInfoDto.participantDtoList.filter((p) => p.playerDto.teamId === 100)
);

const redTeamPlayers = computed(() =>
    props.currentMatchInfoDto.participantDtoList.filter((p) => p.playerDto.teamId === 200)
);

const gameStartTime = new Date(props.currentMatchInfoDto.matchInfoDto.gameStartDt).getTime();
const currentGameDuration = ref(Math.floor((Date.now() - gameStartTime) / 1000));
let timer: ReturnType<typeof setTimeout> | null = null;

// 시간 포맷팅 함수
const formattedGameDuration = computed(() => {
  const minutes = Math.floor(currentGameDuration.value / 60);
  const seconds = currentGameDuration.value % 60;
  return `${minutes}분 ${seconds.toString().padStart(2, '0')}초`;
});

// 컴포넌트가 마운트될 때 타이머 시작
onMounted(() => {
  timer = setInterval(() => {
    currentGameDuration.value = Math.floor((Date.now() - gameStartTime) / 1000);
  }, 1000);
});

// 컴포넌트가 언마운트될 때 타이머 정리
onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
});


</script>

<style scoped>
.current-game-container {
  background: #141414;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,.05);
  margin-top: 15px;
}

.current-game-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  font-size: 14px;
}

.title-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.game-icon {
  width: 18px;
  height: 18px;
  color: #2979FF;
}

.game-status {
  color: #9e9e9e;
  font-weight: 500;
}

.separator {
  color: rgba(255,255,255,0.2);
}

.game-mode {
  color: #2979FF;
  font-weight: 500;
}

.game-duration {
  color: #9e9e9e;
}

.game-icon {
  width: 18px;
  height: 18px;
  color: #2979FF;
}

.current-game-teams {
  display: grid;
  grid-template-columns: 1fr 1fr;
}

.current-game-team {
  padding: 12px;
}

.current-game-team.blue .team-header {
  color: #2979FF;
}

.current-game-team.red .team-header {
  color: #FF5252;
}

.team-header {
  font-weight: 600;
  margin-bottom: 12px;
  font-size: 14px;
}

.current-game-player {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 8px;
  margin-bottom: 8px;
  min-height: 70px;
}

.player-champion {
  flex-shrink: 0;
}

.champion-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  overflow: hidden;
  background: #1a1a1a;
  border: 2px solid rgba(255, 255, 255, 0.1);
}

.champion-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.player-info {
  flex-grow: 1;
  min-width: 0;
}

.player-tier-tag {
  /*background: rgba(41, 121, 255, 0.1);
  color: #2979FF;*/
  font-size: 11px;
  padding: 1px 4px;
  border-radius: 4px;
  margin-right: 4px;
}

.player-name-wrapper {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.player-name {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  transition: color 0.2s;
}

.player-name:hover {
  color: #2979FF;
}

.player-tag {
  font-size: 12px;
  color: #666;
}

.player-reviews {
  display: flex;
  flex-direction: column;
  /*align-items: flex-end;*/
  gap: 4px;
}

.player-reviews.no-tags {
  justify-content: center;
}

.review-stats {
  display: flex;
  gap: 12px;
  align-self: flex-end;
}

.review-up,
.review-down {
  display: flex;
  align-items: center;
  gap: 4px;
}

.thumb-icon {
  width: 14px;
  height: 14px;
}

.thumb-icon.up {
  color: #4CAF50;
}

.thumb-icon.down {
  color: #FF5252;
}

.count {
  font-size: 14px;
  font-weight: 600;
}

.review-up .count {
  color: #4CAF50;
}

.review-down .count {
  color: #FF5252;
}

.top-tags {
  display: flex;
  gap: 4px;
  margin-top: 4px;
  min-height: 21px;
}

.top-tag {
  background: rgba(41, 121, 255, 0.1);
  color: #2979FF;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
}

:deep(.match-tag-list .tag) {
  background: rgba(41, 121, 255, 0.1);
  color: #2979FF;
  border: none;
}

:deep(.match-tag-list .tag-count) {
  font-size: 11px;
}

@media (max-width: 768px) {
  .top-tags {
    max-width: 100%;
    min-height: 16px;
  }

  .top-tags .top-tag:nth-child(n+3) {
    display: none;
  }

  .current-game-teams {
    grid-template-columns: 1fr 1fr;
    gap: 4px;
  }

  .current-game-team {
    padding: 6px;
    overflow: hidden;
  }

  .current-game-player {
    display: grid;
    grid-template-columns: auto 1fr;
    grid-template-areas:
      "champion player-details"
      "champion player-tags";
    gap: 6px;
    padding: 6px;
    background: rgba(255, 255, 255, 0.04);
    border-radius: 8px;
    margin-bottom: 4px;
    align-items: end;
    min-height: 80px;
  }

  .player-champion {
    grid-area: champion;
    align-self: center;
  }

  .champion-icon {
    width: 36px;
    height: 36px;
    border-radius: 6px;
    overflow: hidden;
  }

  .player-info {
    grid-area: player-details;
    display: flex;
    flex-direction: column;
  }

  .player-name-wrapper {
    display: flex;
    align-items: center;
    gap: 4px;
    margin-bottom: 2px;
  }

  .player-tier-tag {
    font-size: 9px;
    padding: 1px 4px;
  }

  .player-name {
    font-size: 11px;
    font-weight: 500;
    color: #fff;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 100%;
  }

  .player-tag {
    font-size: 9px;
    color: #666;
    display: none;
  }

  .player-reviews {
    grid-area: player-tags;
    display: flex;
    flex-direction: column;
    gap: 4px;
    align-items: stretch;
  }

  .current-game-player .player-reviews.no-tags {
    grid-area: player-tags;
    align-self: flex-start;
  }

  .review-stats {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 10px;
    align-self: flex-start;
  }

  .review-up,
  .review-down {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .thumb-icon {
    width: 12px;
    height: 12px;
  }

  .count {
    font-size: 10px;
    font-weight: 600;
  }

  .top-tags {
    display: flex;
    gap: 4px;
    margin-top: 2px;
    overflow: hidden;
  }

  .top-tag {
    background: rgba(41, 121, 255, 0.1);
    color: #2979FF;
    font-size: 9px;
    padding: 1px 4px;
    border-radius: 4px;
    white-space: nowrap;
  }

  :deep(.match-tag-list) {
    width: 100%;
    overflow: hidden; /* 추가 */
  }

  :deep(.match-tag-list .tags-wrapper) {
    width: 100%;
    flex-wrap: nowrap; /* 추가 */
    overflow: hidden; /* 추가 */
  }

  :deep(.match-tag-list .tag) {
    font-size: 9px;
    padding: 1px 4px;
  }

  :deep(.match-tag-list .tag-count) {
    font-size: 9px;
  }

  :deep(.match-tag-list .tag:nth-child(n+3)) {
    display: none;
  }
}
</style>