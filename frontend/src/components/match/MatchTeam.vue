<<template>
  <div class="team">
    <!-- PC/태블릿 레이아웃 -->
    <div class="hidden md:block" v-if="!isArena">
      <div class="team-title" :class="[teamType, participants[0]?.isWin ? 'win' : 'lose']">
        {{ teamType === 'blue' ? '블루팀' : '레드팀' }}
        <span class="result-text">{{ participants[0]?.isWin ? '승리' : '패배' }}</span>
      </div>
      <div class="team-members">
        <div v-for="player in participants"
             :key="player.puuid"
             class="player"
             :class="[
               teamType,
               auth.user?.puuid != profileData.riotAccountInfoEntity.puuid
               ? 'someone-else'
               : {
                 'reviewed': !player.reviewDto?.reviewable,
                 'not-reviewed': player.reviewDto?.reviewable && player.puuid !== auth.user?.puuid,
                 'self-profile': player.puuid === auth.user?.puuid,
               }
             ]"
             @click="handlePlayerClick(player)"
        >
          <div class="review-scores">
            <div class="review-counts">
              <div class="review-up">
                <ThumbsUp class="thumb-icon up" />
                <span class="count">{{ player.reviewStatsDto?.upCount || 0 }}</span>
              </div>
              <div class="review-down">
                <ThumbsDown class="thumb-icon down" />
                <span class="count">{{ player.reviewStatsDto?.downCount || 0 }}</span>
              </div>
            </div>
            <!--            <div class="review-ratio" v-if="player.reviewStatsDto.totalReviewCnt > 0">-->
            <!--              {{ calculateRatio(player.reviewStatsDto.upCount, player.reviewStatsDto.totalReviewCnt) }}%-->
            <!--            </div>-->
          </div>
          <div class="champion-wrapper">
            <div class="champion-icon">
              <img :src="player.champProfileIconUrl" :alt="player.champName">
            </div>
            <div class="champion-level">{{ player.champLevel }}</div>
          </div>
          <div class="player-info">
            <div class="player-name-wrapper">
              <div class="player-name" @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
                {{ player.riotIdGameName }}
              </div>
              <div class="player-tag">#{{ player.riotIdTagline }}</div>
            </div>
            <div class="player-stats">
              <div class="kda-wrapper">
                <span class="kda">
                  <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                  <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                  <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
                </span>
                <span class="kda-ratio" v-if="calculateKDA(player) > 0">
                  KDA {{ calculateKDA(player).toFixed(1) }}
                </span>
              </div>
              <div class="damage-bars">
                <damage-bar
                    :value="player.totalDamageToChampion"
                    :max-value="maxDamage"
                    type="damage"
                    label="챔피언에게 가한 피해량"
                />
                <damage-bar
                    :value="player.totalDamageTaken"
                    :max-value="maxDamageTaken"
                    type="taken"
                    label="받은 피해량"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="hidden md:block" v-else>
      <div class="arena-teams-container">
        <div v-for="(team, index) in arenaTeams" :key="index" class="arena-team-container">
          <div class="team-title arena" :class="index < 4 ? 'win' : 'lose'">
            {{ index + 1 }}위
          </div>

          <div class="arena-players-grid">
            <div v-for="player in team"
                 :key="player.puuid"
                 class="arena-player-grid-item"
                 :class="[
                   auth.user?.puuid != profileData.riotAccountInfoEntity.puuid
                   ? 'someone-else'
                   : {
                     'reviewed': !player.reviewDto?.reviewable,
                     'not-reviewed': player.reviewDto?.reviewable && player.puuid !== auth.user?.puuid,
                     'self-profile': player.puuid === auth.user?.puuid,
                   }
                 ]"
                 @click="handlePlayerClick(player)"
            >
              <!-- 챔피언 아이콘 영역 -->
              <div class="grid-champion">
                <div class="champion-icon">
                  <img :src="player.champProfileIconUrl" :alt="player.champName">
                </div>
                <div class="champion-level">{{ player.champLevel }}</div>
              </div>

              <!-- 증강 그리드 영역 - 별도로 분리 -->
              <div class="grid-augments" v-if="isArena">
                <div class="augments-grid">
                  <div class="augment-icon" v-if="player.playerAugment1">
                    <img :src="player.playerAugmentEntity1.iconLargeUrl" alt="Augment 1">
                  </div>
                  <div class="augment-icon" v-if="player.playerAugment2">
                    <img :src="player.playerAugmentEntity2.iconLargeUrl" alt="Augment 2">
                  </div>
                  <div class="augment-icon" v-if="player.playerAugment3">
                    <img :src="player.playerAugmentEntity3.iconLargeUrl" alt="Augment 3">
                  </div>
                  <div class="augment-icon" v-if="player.playerAugment4">
                    <img :src="player.playerAugmentEntity4.iconLargeUrl" alt="Augment 4">
                  </div>
                </div>
              </div>

              <!-- 플레이어 정보 영역 -->
              <div class="grid-player-info">
                <div class="player-name-wrapper">
                  <div class="player-name" @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
                    {{ player.riotIdGameName }}
                  </div>
                  <div class="player-tag">#{{ player.riotIdTagline }}</div>
                </div>
                <div class="kda-wrapper">
                  <span class="kda">
                    <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                    <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                    <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
                  </span>
                  <span class="kda-ratio" v-if="calculateKDA(player) > 0">
                    KDA {{ calculateKDA(player).toFixed(1) }}
                  </span>
                </div>
              </div>

              <!-- 데미지 바 영역 -->
              <div class="grid-damage-bars">
                <div class="damage-row">
                  <damage-bar
                      :value="player.totalDamageToChampion"
                      :max-value="maxDamage"
                      type="damage"
                      label="가한 피해량"
                      class="arena-bar"
                      style="--bar-min-width: 100px; --bar-display: block"
                  />
                </div>
                <div class="damage-row">
                  <damage-bar
                      :value="player.totalDamageTaken"
                      :max-value="maxDamageTaken"
                      type="taken"
                      label="받은 피해량"
                      class="arena-bar"
                      style="--bar-min-width: 100px; --bar-display: block"
                  />
                </div>
              </div>

              <!-- 리뷰 점수 영역 -->
              <div class="grid-review-scores">
                <div class="review-up">
                  <ThumbsUp class="thumb-icon up" />
                  <span class="count">{{ player.reviewStatsDto?.upCount || 0 }}</span>
                </div>
                <div class="review-down">
                  <ThumbsDown class="thumb-icon down" />
                  <span class="count">{{ player.reviewStatsDto?.downCount || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 모바일 레이아웃 -->
    <div class="md:hidden" v-if="!isArena">
      <template v-if="teamType === 'all'">
        <!-- 블루팀 -->
        <div class="mobile-team-title blue">
          블루팀
          <span class="mobile-result-text">{{ participants.find(p => p.teamId === 100)?.isWin ? '승리' : '패배' }}</span>
        </div>
        <div class="mobile-team-members">
          <div v-for="player in participants.filter(p => p.teamId === 100)"
               :key="player.puuid"
               class="mobile-player"
               :class="[
                 auth.user?.puuid != profileData.riotAccountInfoEntity.puuid
                 ? 'mobile-someone-else'
                 : {
                   'mobile-reviewed': !player.reviewDto?.reviewable,
                   'mobile-not-reviewed': player.reviewDto?.reviewable && player.puuid !== auth.user?.puuid,
                   'mobile-self-profile': player.puuid === auth.user?.puuid,
                 }
               ]"
               @click="handlePlayerClick(player)"
          >
            <!-- Top row: Player info and review scores -->
            <div class="mobile-top-row">
              <div class="mobile-main-info">
                <div class="mobile-champion-wrapper">
                  <div class="mobile-champion-icon">
                    <img :src="player.champProfileIconUrl" :alt="player.champName">
                  </div>
                  <div class="mobile-champion-level">{{ player.champLevel }}</div>
                </div>
                <div class="mobile-player-info">
                  <div class="mobile-name-tag">
                    <span class="mobile-name" @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
                      {{ player.riotIdGameName }}
                    </span>
                    <span class="mobile-tag">#{{ player.riotIdTagline }}</span>
                  </div>
                  <div class="mobile-kda">
                    <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                    <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                    <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
                  </div>
                </div>
              </div>
              <div class="mobile-review-scores" v-if="player.reviewStatsDto">
                <div class="mobile-review-up">
                  <ThumbsUp class="mobile-thumb-icon up" />
                  <span class="mobile-count">{{ player.reviewStatsDto.upCount }}</span>
                </div>
                <div class="mobile-review-down">
                  <ThumbsDown class="mobile-thumb-icon down" />
                  <span class="mobile-count">{{ player.reviewStatsDto.downCount }}</span>
                </div>
              </div>
            </div>

            <!-- Bottom row: Damage bar -->
            <div class="mobile-bottom-row">
              <div class="mobile-damage-bar">
                <damage-bar
                    :value="player.totalDamageToChampion"
                    :max-value="maxDamage"
                    type="damage"
                    label="챔피언에게 가한 피해량"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- 레드팀 -->
        <div class="mobile-team-title red">
          레드팀
          <span class="mobile-result-text">{{ participants.find(p => p.teamId === 200)?.isWin ? '승리' : '패배' }}</span>
        </div>
        <div class="mobile-team-members">
          <div v-for="player in participants.filter(p => p.teamId === 200)"
               :key="player.puuid"
               class="mobile-player"
               :class="[
                 auth.user?.puuid != profileData.riotAccountInfoEntity.puuid
                 ? 'mobile-someone-else'
                 : {
                   'mobile-reviewed': !player.reviewDto?.reviewable,
                   'mobile-not-reviewed': player.reviewDto?.reviewable && player.puuid !== auth.user?.puuid,
                   'mobile-self-profile': player.puuid === auth.user?.puuid,
                 }
               ]"
               @click="handlePlayerClick(player)"
          >
            <!-- Top row -->
            <div class="mobile-top-row">
              <div class="mobile-main-info">
                <div class="mobile-champion-wrapper">
                  <div class="mobile-champion-icon">
                    <img :src="player.champProfileIconUrl" :alt="player.champName">
                  </div>
                  <div class="mobile-champion-level">{{ player.champLevel }}</div>
                </div>
                <div class="mobile-player-info">
                  <div class="mobile-name-tag">
                    <span class="mobile-name" @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
                      {{ player.riotIdGameName }}
                    </span>
                    <span class="mobile-tag">#{{ player.riotIdTagline }}</span>
                  </div>
                  <div class="mobile-kda">
                    <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                    <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                    <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
                  </div>
                </div>
              </div>
              <div class="mobile-review-scores" v-if="player.reviewStatsDto">
                <div class="mobile-review-up">
                  <ThumbsUp class="mobile-thumb-icon up" />
                  <span class="mobile-count">{{ player.reviewStatsDto.upCount }}</span>
                </div>
                <div class="mobile-review-down">
                  <ThumbsDown class="mobile-thumb-icon down" />
                  <span class="mobile-count">{{ player.reviewStatsDto.downCount }}</span>
                </div>
              </div>
            </div>

            <!-- Bottom row -->
            <div class="mobile-bottom-row">
              <div class="mobile-damage-bar">
                <damage-bar
                    :value="player.totalDamageToChampion"
                    :max-value="maxDamage"
                    type="damage"
                    label="챔피언에게 가한 피해량"
                />
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
    <div class="md:hidden" v-else>
      <div v-for="(team, index) in arenaTeams" :key="index" class="mobile-arena-team">
        <div class="mobile-team-title arena" :class="index < 4 ? 'win' : 'lose'">
          {{ index + 1 }}위
        </div>
        <div class="mobile-arena-grid">
          <div v-for="player in team"
               :key="player.puuid"
               class="mobile-arena-player-item"
               :class="[
             auth.user?.puuid != profileData.riotAccountInfoEntity.puuid
             ? 'mobile-someone-else'
             : {
               'mobile-reviewed': !player.reviewDto?.reviewable,
               'mobile-not-reviewed': player.reviewDto?.reviewable && player.puuid !== auth.user?.puuid,
               'mobile-self-profile': player.puuid === auth.user?.puuid,
             }
           ]"
               @click="handlePlayerClick(player)"
          >
            <!-- 왼쪽 영역: 챔피언 아이콘 + 증강 -->
            <div class="mobile-arena-left-section">
              <!-- 챔피언 아이콘 -->
              <div class="mobile-champion-wrapper">
                <div class="mobile-champion-icon">
                  <img :src="player.champProfileIconUrl" :alt="player.champName">
                </div>
                <div class="mobile-champion-level">{{ player.champLevel }}</div>
              </div>

              <!-- 증강 그리드 -->
              <div class="mobile-augments-container" v-if="isArena">
                <div class="mobile-augments-grid">
                  <div class="mobile-augment-icon" v-if="player.playerAugment1">
                    <img :src="player.playerAugmentEntity1.iconLargeUrl" alt="Augment 1">
                  </div>
                  <div class="mobile-augment-icon" v-if="player.playerAugment2">
                    <img :src="player.playerAugmentEntity2.iconLargeUrl" alt="Augment 2">
                  </div>
                  <div class="mobile-augment-icon" v-if="player.playerAugment3">
                    <img :src="player.playerAugmentEntity3.iconLargeUrl" alt="Augment 3">
                  </div>
                  <div class="mobile-augment-icon" v-if="player.playerAugment4">
                    <img :src="player.playerAugmentEntity4.iconLargeUrl" alt="Augment 4">
                  </div>
                </div>
              </div>
            </div>

            <!-- 중앙 영역: 플레이어 정보 + KDA -->
            <div class="mobile-arena-center-section">
              <div class="mobile-player-name-wrapper">
            <span class="mobile-player-name" @click.stop="goSelectedSummonerProfile(player.riotIdGameName, player.riotIdTagline)">
              {{ player.riotIdGameName }}
            </span>
                <span class="mobile-player-tag">#{{ player.riotIdTagline }}</span>
              </div>
              <div class="mobile-player-kda">
                <span :class="{ 'highlight': player.kills > 0 }">{{ player.kills }}</span> /
                <span :class="{ 'highlight-death': player.deaths > 0 }">{{ player.deaths }}</span> /
                <span :class="{ 'highlight': player.assists > 0 }">{{ player.assists }}</span>
                <span class="mobile-kda-ratio" v-if="calculateKDA(player) > 0">
              ({{ calculateKDA(player).toFixed(1) }})
            </span>
              </div>
            </div>

            <!-- 오른쪽 영역: 리뷰 점수 + 데미지 바 -->
            <div class="mobile-arena-right-section">
              <div class="mobile-arena-review-scores" v-if="player.reviewStatsDto">
                <div class="mobile-review-score-row">
                  <ThumbsUp class="mobile-thumb-icon up" />
                  <span class="mobile-review-count up">{{ player.reviewStatsDto.upCount || 0 }}</span>
                </div>
                <div class="mobile-review-score-row">
                  <ThumbsDown class="mobile-thumb-icon down" />
                  <span class="mobile-review-count down">{{ player.reviewStatsDto.downCount || 0 }}</span>
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
import {type LolMatchParticipant, MatchGameMode} from '@/types/match'
import DamageBar from './DamageBar.vue'
import {computed, onMounted} from 'vue'
import {useAuthStore} from "@/stores/auth.ts";
import type {LolSummonerProfileResDto} from "@/types/summoner.ts";
import {useRouter} from "vue-router";
import {getRatingColor} from "@/utils/ratingUtil.ts";
import {ThumbsDown, ThumbsUp} from "lucide-vue-next";
import {goSelectedSummonerProfile} from "@/utils/common.ts";


const props = defineProps<{
  gameMode: string
  profileData: LolSummonerProfileResDto
  participants: LolMatchParticipant[]
  allParticipants: LolMatchParticipant[]
  teamType: 'blue' | 'red' | 'all'
}>()

onMounted(() => {
  console.log('isArena:', isArena.value);
  console.log('gameMode:', props.gameMode);
  console.log('MatchGameMode.CHERRY:', MatchGameMode.CHERRY);
});

const emit = defineEmits<{
  (e: 'reviewPlayer', player: LolMatchParticipant): void
}>()

const auth = useAuthStore();
const router = useRouter();

const maxDamage = computed(() =>
    Math.max(...props.allParticipants.map(p => p.totalDamageToChampion))
)

const maxDamageTaken = computed(() =>
    Math.max(...props.allParticipants.map(p => p.totalDamageTaken))
)

const calculateKDA = (player: LolMatchParticipant) => {
  return player.deaths === 0
      ? player.kills + player.assists
      : (player.kills + player.assists) / player.deaths
}

const handlePlayerClick = (player:any) => {
  // 자기 자신인 경우
  if (auth.isAuthenticated && auth.user?.puuid == player.puuid) {
    return
  }

  // 다른 사람의 프로필인 경우
  if (auth.user?.puuid !== props.profileData.riotAccountInfoEntity.puuid) {
    return
  }

  emit('reviewPlayer', player)
}

const calculateRatio = (upCount: number, total: number) => {
  if (total === 0) return 0
  return Math.round((upCount / total) * 100)
}

const isArena = computed(() => {
  return props.gameMode === MatchGameMode.CHERRY;
});

const arenaTeams = computed(() => {
  if (!isArena.value) return [];

  // playerSubteamId로 그룹핑
  const teams: { [key: number]: LolMatchParticipant[] } = {};
  props.participants.forEach(player => {
    const teamId = player.playerSubteamId || 0;
    if (!teams[teamId]) {
      teams[teamId] = [];
    }
    teams[teamId].push(player);
  });

  // 팀별로 subteamPlacement 값을 가져와서 오름차순 정렬
  const sortedTeams = Object.values(teams).sort((teamA, teamB) => {
    // 각 팀에서 첫 플레이어의 subteamPlacement를 기준으로 정렬
    // (팀 내 모든 플레이어의 subteamPlacement는 동일해야 함)
    const placementA = teamA[0]?.subteamPlacement ?? Number.MAX_SAFE_INTEGER;
    const placementB = teamB[0]?.subteamPlacement ?? Number.MAX_SAFE_INTEGER;
    return placementA - placementB;
  });

  return sortedTeams;
});
</script>

<style>
/* 기존 스타일 유지 - 비아레나 모드용 */
.team {
width: 100%;
}

.team-title {
display: flex;
align-items: center;
gap: 8px;
font-weight: 600;
font-size: 14px;
margin-bottom: 8px;
margin-top: 15px;
padding-left: 8px;
letter-spacing: 0.5px;
}

.team-title.win {
color: #4CAF50;
}

.team-title.lose {
color: #FF5252;
}

.team-title.blue {
color: #2979FF;
}

.team-title.red {
color: #FF5252;
}

.result-text {
font-size: 12px;
font-weight: 500;
padding: 2px 8px;
border-radius: 4px;
background: rgba(255, 255, 255, 0.1);
}

.win .result-text {
background: rgba(76, 175, 80, 0.2);
}

.lose .result-text {
background: rgba(255, 82, 82, 0.2);
}

.team-members {
display: flex;
flex-direction: column;
gap: 4px; /* 8px에서 4px로 줄임 */
padding: 4px 4px 4px 4px; /* 패딩 추가 */
}

.player {
position: relative;
display: flex;
align-items: center;
gap: 8px; /* 12px에서 8px로 줄임 */
padding: 8px 12px; /* 12px에서 패딩 줄임 */
border-radius: 8px;
transition: all 0.2s ease;
background: rgba(255, 255, 255, 0.04);
border: 2px solid transparent;
}

.player.compact {
padding: 8px;
}

.player.someone-else {
background: rgba(255, 255, 255, 0.02);
opacity: 0.8;
cursor: default;
}

.player.not-reviewed {
background: rgba(41, 121, 255, 0.08);
border: 2px solid rgba(41, 121, 255, 0.2);
box-shadow: 0 0 8px rgba(41, 121, 255, 0.1);
cursor: pointer;
}

.player.not-reviewed:hover,
.player.reviewed:hover:not(.self-profile):hover {
transform: translateX(4px);
border-color: rgba(41, 121, 255, 0.4);
background: rgba(41, 121, 255, 0.12);
}

.player.reviewed:not(.self-profile):hover {
background: rgba(76, 175, 80, 0.05);
border: 2px solid rgba(76, 175, 80, 0.1);
opacity: 0.9;
cursor: pointer;
}

.player.self-profile {
background: rgba(255, 255, 255, 0.02);
border: 2px solid rgba(255, 255, 255, 0.05);
opacity: 0.8;
cursor: default;
}

/* 기존 PC/태블릿 스타일 유지 */
.champion-wrapper {
position: relative;
}

.champion-icon {
width: 36px; /* 40px에서 36px로 줄임 */
height: 36px; /* 40px에서 36px로 줄임 */
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
color: #fff;
}

.player-info {
flex: 1;
min-width: 0;
}

.player-name-wrapper {
display: flex;
align-items: center;
gap: 4px;
margin-bottom: 2px; /* 4px에서 2px로 줄임 */
}

.player-name {
font-size: 14px;
font-weight: 500;
color: #fff;
white-space: nowrap;
overflow: hidden;
text-overflow: ellipsis;
transition: all 0.2s ease;
cursor: pointer;
}

.player-name:hover {
color: #2979FF;
text-decoration: underline;
}

.player-tag {
font-size: 12px;
color: #666;
}

.player-stats {
display: flex;
flex-direction: column;
gap: 4px; /* 8px에서 4px로 줄임 */
}

.combined-stats {
display: flex;
flex-direction: column;
gap: 2px;
}

.kda-wrapper {
display: flex;
align-items: center;
gap: 4px; /* 8px에서 4px로 줄임 */
}

.kda {
font-size: 13px; /* 14px에서 13px로 줄임 */
color: #888;
font-weight: 500;
}

.kda .highlight {
color: #4CAF50;
}

.kda .highlight-death {
color: #FF5252;
}

.kda-ratio {
font-size: 11px; /* 12px에서 11px로 줄임 */
color: #666;
font-weight: 500;
margin-left: 4px;
}

.damage-bars {
display: flex;
flex-direction: column;
gap: 2px; /* 4px에서 2px로 줄임 */
padding-right: 8px;
}

.review-scores {
position: absolute;
top: 8px; /* 12px에서 8px로 줄임 */
right: 8px; /* 12px에서 8px로 줄임 */
display: flex;
flex-direction: column;
align-items: flex-end;
gap: 4px;
}

.review-counts {
display: flex;
gap: 12px;
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

.review-ratio {
font-size: 12px;
font-weight: 600;
color: #2979FF;
}

.arena-teams-list {
display: flex;
flex-direction: column;
gap: 8px; /* 16px에서 8px로 줄임 */
}

.arena-teams-vertical {
display: flex;
flex-direction: column;
width: 100%;
gap: 8px; /* 16px에서 8px로 줄임 */
}

.arena-teams-vertical {
display: flex;
flex-direction: column;
width: 100%;
gap: 8px; /* 16px에서 8px로 줄임 */
}

/* PC/태블릿 아레나 모드 그리드 스타일 */
.arena-teams-container {
display: flex;
flex-direction: column;
width: 100%;
}

.arena-team-container {
width: 100%;
background: rgba(20, 20, 20, 0.6);
border-radius: 8px;
overflow: hidden;
border: 1px solid rgba(255, 255, 255, 0.05);
}

.team-title.arena {
display: flex;
align-items: center;
padding: 6px 10px;
background: rgba(0, 0, 0, 0.3);
border-radius: 0;
margin-bottom: 0;
border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.placement-text {
margin-left: auto;
font-size: 11px;
font-weight: 500;
padding: 2px 6px;
border-radius: 4px;
background: rgba(0, 0, 0, 0.3);
}

.arena-players-grid {
display: grid;
grid-template-columns: 1fr;
gap: 2px;
padding: 4px;
}

.arena-player-grid-item {
display: grid;
grid-template-columns: auto auto 1fr auto auto;
grid-template-areas: "champion augments info damage review";
gap: 8px;
padding: 8px;
background: rgba(0, 0, 0, 0.2);
border-radius: 4px;
align-items: center;
transition: all 0.2s ease;
border: 1px solid transparent;
}

.arena-player-grid-item.not-reviewed:hover,
.arena-player-grid-item.reviewed:not(.self-profile):hover {
transform: translateX(4px);
border-color: rgba(41, 121, 255, 0.4);
background: rgba(41, 121, 255, 0.12);
}

.arena-player-grid-item.someone-else {
background: rgba(255, 255, 255, 0.02);
opacity: 0.8;
cursor: default;
}

.arena-player-grid-item.not-reviewed {
background: rgba(41, 121, 255, 0.08);
border-color: rgba(41, 121, 255, 0.2);
box-shadow: 0 0 8px rgba(41, 121, 255, 0.1);
cursor: pointer;
}

.arena-player-grid-item.reviewed:not(.self-profile) {
cursor: pointer;
}

.arena-player-grid-item.self-profile {
background: rgba(255, 255, 255, 0.02);
border-color: rgba(255, 255, 255, 0.05);
opacity: 0.8;
cursor: default;
}

.grid-champion {
grid-area: champion;
position: relative;
}

/* 증강 그리드 영역 추가 */
.grid-augments {
grid-area: augments;
display: flex;
align-items: center;
margin: 0 5px;
}

.augments-grid {
display: grid;
grid-template-columns: 1fr 1fr;
grid-template-rows: 1fr 1fr;
gap: 2px;
width: 40px;
height: 40px;
}

.augment-icon {
width: 20px;
height: 20px;
border-radius: 2px;
background: rgba(0, 0, 0, 0.5);
border: 1px solid rgba(255, 215, 0, 0.7);
overflow: hidden;
}

.augment-icon img {
width: 100%;
height: 100%;
object-fit: cover;
}

.grid-player-info {
grid-area: info;
min-width: 0;
}

.grid-damage-bars {
grid-area: damage;
display: flex;
flex-direction: row; /* 세로에서 가로로 변경 */
gap: 20px; /* 간격 조정 */
align-items: center;
}

.damage-row {
display: flex;
align-items: center;
gap: 6px;
flex: 1; /* 추가: 각 데미지 바가 동일한 공간 차지 */
}

.damage-label {
font-size: 10px;
color: #888;
width: auto; /* 30px에서 auto로 변경 */
margin-right: 4px;
text-align: left; /* right에서 left로 변경 */
}

.damage-label.damage {
color: #FF5252;
}

.damage-label.taken {
color: #2979FF;
}

.grid-review-scores {
grid-area: review;
display: flex;
flex-direction: column;
gap: 4px;
min-width: 50px;
align-items: flex-end;
}

:deep(.arena-bar .bar-container) {
  display: block !important;
  min-width: 100px !important;
  width: 300px !important; /* 명시적인 너비 지정 */
}

:deep(.arena-bar .progress-bg) {
height: 5px !important;
width: 100% !important; /* 추가 */
}

:deep(.arena-bar .damage-text) {
font-size: 10px !important;
min-width: 30px !important;
}

/* 모바일 아레나 그리드 스타일 */
.mobile-arena-team {
margin-bottom: 12px;
width: 100%;
}

.mobile-team-title.arena {
display: flex;
align-items: center;
background: rgba(20, 20, 20, 0.8);
border-top-left-radius: 8px;
border-top-right-radius: 8px;
border: 1px solid rgba(255, 255, 255, 0.1);
border-bottom: none;
padding: 8px 10px;
font-weight: 600;
}

.mobile-placement-text {
margin-left: auto;
font-size: 10px;
padding: 2px 6px;
border-radius: 4px;
background: rgba(0, 0, 0, 0.3);
}

.mobile-arena-grid {
display: flex;
flex-direction: column;
gap: 1px;
background: rgba(20, 20, 20, 0.6);
border: 1px solid rgba(255, 255, 255, 0.1);
border-top: none;
border-bottom-left-radius: 8px;
border-bottom-right-radius: 8px;
overflow: hidden;
padding: 4px;
}

.mobile-arena-player-item {
display: grid;
grid-template-areas:
"left center right"
"damage damage damage";
grid-template-columns: auto 1fr auto;
grid-template-rows: auto auto;
background: rgba(0, 0, 0, 0.2);
border-radius: 4px;
padding: 8px 8px 6px 8px;
gap: 4px;
transition: all 0.2s ease;
border: 1px solid transparent;
}

/* Player states */
.mobile-arena-player-item.mobile-someone-else {
background: rgba(255, 255, 255, 0.02);
opacity: 0.8;
cursor: default;
}

.mobile-arena-player-item.mobile-not-reviewed {
background: rgba(41, 121, 255, 0.08);
border-color: rgba(41, 121, 255, 0.2);
box-shadow: 0 0 8px rgba(41, 121, 255, 0.1);
cursor: pointer;
}

.mobile-arena-player-item.mobile-not-reviewed:hover,
.mobile-arena-player-item.mobile-reviewed:not(.mobile-self-profile):hover {
transform: translateX(4px);
border-color: rgba(41, 121, 255, 0.4);
background: rgba(41, 121, 255, 0.12);
}

.mobile-arena-player-item.mobile-reviewed:not(.mobile-self-profile) {
background: rgba(76, 175, 80, 0.05);
border: 1px solid rgba(76, 175, 80, 0.1);
cursor: pointer;
}

.mobile-arena-player-item.mobile-self-profile {
background: rgba(255, 255, 255, 0.02);
border: 1px solid rgba(255, 255, 255, 0.05);
opacity: 0.8;
cursor: default;
}

/* Left section: Champion + Augments */
.mobile-arena-left-section {
grid-area: left;
display: flex;
align-items: center;
gap: 4px;
}

.mobile-champion-wrapper {
position: relative;
margin-right: 2px;
}

.mobile-champion-icon {
width: 28px;
height: 28px;
border-radius: 6px;
overflow: hidden;
background: #1a1a1a;
border: 1px solid rgba(255, 255, 255, 0.1);
}

.mobile-champion-icon img {
width: 100%;
height: 100%;
object-fit: cover;
}

.mobile-champion-level {
position: absolute;
bottom: -2px;
right: -2px;
background: #000;
border: 1px solid rgba(255, 255, 255, 0.1);
border-radius: 3px;
padding: 0px 2px;
font-size: 8px;
font-weight: 600;
color: #fff;
}

.mobile-augments-container {
margin-left: 2px;
}

.mobile-augments-grid {
display: grid;
grid-template-columns: 1fr 1fr;
grid-template-rows: 1fr 1fr;
gap: 1px;
width: 35px;
height: 35px;
}

.mobile-augment-icon {
width: 15px;
height: 15px;
border-radius: 2px;
background: rgba(0, 0, 0, 0.5);
border: 1px solid rgba(255, 215, 0, 0.7);
overflow: hidden;
}

.mobile-augment-icon img {
width: 100%;
height: 100%;
object-fit: cover;
}

/* Center section: Player info + KDA */
.mobile-arena-center-section {
grid-area: center;
min-width: 0;
padding: 0 4px;
}

.mobile-player-name-wrapper {
display: flex;
align-items: center;
gap: 4px;
min-width: 0;
margin-bottom: 2px;
}

.mobile-player-name {
font-size: 12px;
font-weight: 500;
color: #fff;
white-space: nowrap;
overflow: hidden;
text-overflow: ellipsis;
transition: all 0.2s ease;
cursor: pointer;
}

.mobile-player-name:hover {
color: #2979FF;
text-decoration: underline;
}

.mobile-player-tag {
font-size: 10px;
color: #666;
flex-shrink: 0;
}

.mobile-player-kda {
font-size: 11px;
color: #888;
}

.mobile-player-kda .highlight {
color: #4CAF50;
}

.mobile-player-kda .highlight-death {
color: #FF5252;
}

.mobile-kda-ratio {
font-size: 10px;
color: #666;
margin-left: 2px;
}

/* Right section: Review scores */
.mobile-arena-right-section {
grid-area: right;
display: flex;
justify-content: flex-end;
}

.mobile-arena-review-scores {
display: flex;
flex-direction: column;
gap: 3px;
}

.mobile-review-score-row {
display: flex;
align-items: center;
gap: 2px;
justify-content: flex-end;
}

.mobile-thumb-icon {
width: 10px;
height: 10px;
}

.mobile-thumb-icon.up {
color: #4CAF50;
}

.mobile-thumb-icon.down {
color: #FF5252;
}

.mobile-review-count {
font-size: 10px;
font-weight: 600;
min-width: 14px;
text-align: right;
}

.mobile-review-count.up {
color: #4CAF50;
}

.mobile-review-count.down {
color: #FF5252;
}

/* Damage section */
.mobile-arena-damage-section {
grid-area: damage;
display: flex;
flex-direction: column;
gap: 3px;
padding: 4px 4px 0 4px;
margin-top: 2px;
border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.mobile-arena-damage-row {
display: flex;
align-items: center;
gap: 6px;
}

.mobile-damage-label {
font-size: 9px;
color: #FF5252;
width: 24px;
text-align: right;
flex-shrink: 0;
}

.mobile-damage-label.taken {
color: #2979FF;
}

:deep(.mobile-arena-damage-bar .progress-bg) {
height: 4px !important;
}

:deep(.mobile-arena-damage-bar .damage-text) {
font-size: 9px !important;
min-width: 30px !important;
}

/* 모바일 스타일 (기존 유지) */
.mobile-team-title {
font-size: 12px;
font-weight: 600;
padding: 6px 6px; /* 8px에서 6px로 줄임 */
display: flex;
align-items: center;
gap: 4px;
border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.mobile-player {
display: flex;
flex-direction: column;
padding: 6px 6px; /* 8px에서 6px로 줄임 */
background: rgba(255, 255, 255, 0.04);
border-bottom: 1px solid rgba(255, 255, 255, 0.03);
gap: 4px; /* 6px에서 4px로 줄임 */
}

.mobile-top-row {
display: flex;
justify-content: space-between;
align-items: center;
width: 100%;
}

.mobile-bottom-row {
width: 100%;
padding: 0 4px;
margin-top: 2px; /* 마진 추가 */
}

.mobile-main-info {
display: flex;
align-items: center;
gap: 6px; /* 8px에서 6px로 줄임 */
flex: 1;
min-width: 0;
}

.mobile-champion-icon {
width: 24px; /* 28px에서 24px로 줄임 */
height: 24px; /* 28px에서 24px로 줄임 */
border-radius: 4px;
overflow: hidden;
}

.mobile-champion-icon img {
width: 100%;
height: 100%;
object-fit: cover;
}

.mobile-champion-level {
position: absolute;
bottom: -2px;
right: -2px;
background: #000;
border: 1px solid rgba(255, 255, 255, 0.1);
border-radius: 3px;
padding: 0px 2px;
font-size: 8px; /* 9px에서 8px로 줄임 */
font-weight: 600;
}

.mobile-player-info {
min-width: 0;
flex: 1;
}

.mobile-name-tag {
display: flex;
align-items: center;
gap: 4px;
min-width: 0;
}

.mobile-name {
font-size: 12px;
font-weight: 500;
color: #fff;
white-space: nowrap;
overflow: hidden;
text-overflow: ellipsis;
}

.mobile-tag {
font-size: 10px;
color: #666;
flex-shrink: 0;
}

.mobile-kda {
font-size: 11px;
color: #888;
}

.mobile-kda .highlight {
color: #4CAF50;
}

.mobile-kda .highlight-death {
color: #FF5252;
}

.mobile-damage-bar {
width: 100%;
height: 3px; /* 4px에서 3px로 줄임 */
}

.mobile-review-scores {
display: flex;
gap: 6px; /* 8px에서 6px로 줄임 */
padding-left: 6px; /* 8px에서 6px로 줄임 */
flex-shrink: 0;
}

.mobile-review-up,
.mobile-review-down {
display: flex;
align-items: center;
gap: 2px;
min-width: 28px; /* 32px에서 28px로 줄임 */
justify-content: flex-end;
}

.mobile-thumb-icon {
width: 10px; /* 12px에서 10px로 줄임 */
height: 10px; /* 12px에서 10px로 줄임 */
}

.mobile-thumb-icon.up {
color: #4CAF50;
}

.mobile-thumb-icon.down {
color: #FF5252;
}

.mobile-count {
font-size: 10px; /* 11px에서 10px로 줄임 */
font-weight: 600;
}

.mobile-review-up .mobile-count {
color: #4CAF50;
}

.mobile-review-down .mobile-count {
color: #FF5252;
}

/* 팀 구분 스타일 */
.mobile-team-title.blue {
color: #2979FF;
}

.mobile-team-title.red {
color: #FF5252;
margin-top: 8px;
}

.win .mobile-result-text {
background: rgba(76, 175, 80, 0.2);
padding: 1px 4px;
border-radius: 2px;
font-size: 10px;
}

.lose .mobile-result-text {
background: rgba(255, 82, 82, 0.2);
padding: 1px 4px;
border-radius: 2px;
font-size: 10px;
}

.mobile-player.mobile-someone-else {
background: rgba(255, 255, 255, 0.02);
opacity: 0.8;
cursor: default;
}

.mobile-player.mobile-not-reviewed {
background: rgba(41, 121, 255, 0.08);
border: 1px solid rgba(41, 121, 255, 0.2);
cursor: pointer;
}

.mobile-player.mobile-reviewed:not(.mobile-self-profile) {
background: rgba(76, 175, 80, 0.05);
border: 1px solid rgba(76, 175, 80, 0.1);
cursor: pointer;
}

.mobile-player.mobile-self-profile {
background: rgba(255, 255, 255, 0.02);
border: 1px solid rgba(255, 255, 255, 0.05);
opacity: 0.8;
cursor: default;
}

.arena-team-row {
width: 100%;
margin-bottom: 2px;
border-radius: 4px;
overflow: hidden;
background: rgba(30, 30, 30, 0.5);
border: 1px solid rgba(255, 255, 255, 0.05);
}

.arena-team-header {
padding: 4px 8px;
background: rgba(0, 0, 0, 0.3);
border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.arena-team-players {
padding: 4px;
}

.arena-participant {
border-radius: 4px;
background: rgba(0, 0, 0, 0.2);
margin-bottom: 2px;
padding: 4px 6px;
}

.arena-participant:last-child {
margin-bottom: 0;
}

.arena-placement {
font-size: 10px;
padding: 1px 4px;
border-radius: 3px;
background: rgba(0, 0, 0, 0.3);
}

.mobile-placement-text {
margin-left: auto;
font-size: 10px;
padding: 1px 4px;
border-radius: 3px;
background: rgba(0, 0, 0, 0.3);
}

.mobile-team-title.arena {
display: flex;
align-items: center;
background: rgba(0, 0, 0, 0.3);
border-top-left-radius: 8px;
border-top-right-radius: 8px;
border: 1px solid rgba(255, 255, 255, 0.05);
border-bottom: none;
}

.mobile-team-members {
background: rgba(20, 20, 20, 0.6);
border: 1px solid rgba(255, 255, 255, 0.05);
border-top: none;
border-bottom-left-radius: 8px;
border-bottom-right-radius: 8px;
overflow: hidden;
}

.mobile-player.arena-mobile {
border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.mobile-player.arena-mobile:last-child {
border-bottom: none;
}

.horizontal-mobile {
padding: 6px 8px !important;
}

.mobile-player-content {
display: flex;
justify-content: space-between;
align-items: center;
width: 100%;
}

.mobile-left-section {
display: flex;
align-items: center;
gap: 6px;
flex: 1;
min-width: 0;
}

.mobile-right-section {
display: flex;
align-items: center;
}

.mobile-bottom-section {
display: flex;
flex-direction: column;
gap: 2px;
width: 100%;
margin-top: 2px;
}

.mini-damage {
display: flex;
align-items: center;
gap: 4px;
margin-top: 2px;
}

.mini-label {
font-size: 9px;
width: 20px;
text-align: right;
flex-shrink: 0;
}

.mini-label.damage {
color: #FF5252;
}

.mini-label.taken {
color: #2979FF;
}

:deep(.mini-bar .progress-bg) {
height: 3px !important;
margin: 0 !important;
}

:deep(.mini-bar .damage-text) {
font-size: 9px !important;
min-width: 30px !important;
}

:deep(.mobile-damage-bar .progress-bg) {
height: 3px !important;
}

:deep(.mobile-damage-bar .damage-text) {
font-size: 9px !important;
min-width: 28px !important;
}

</style>