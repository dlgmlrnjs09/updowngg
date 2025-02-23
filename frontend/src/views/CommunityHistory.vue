<template>
  <div class="min-h-[1000px] bg-[#0A0A0A] p-6">
    <!-- 컨텐츠 컨테이너 -->
    <div class="max-w-[1000px] mx-auto">
      <!-- 탭 네비게이션 -->
      <div class="flex justify-between items-center mb-8">
        <div class="flex gap-4">
          <button
              @click="handleTabChange('current')"
              :class="[
              'px-4 py-2 rounded-lg text-sm transition-colors',
              activeTab === 'current'
                ? 'bg-[#2979FF] text-white'
                : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
            ]"
          >현재 파티
          </button>
          <button
              @click="handleTabChange('history')"
              :class="[
              'px-4 py-2 rounded-lg text-sm transition-colors',
              activeTab === 'history'
                ? 'bg-[#2979FF] text-white'
                : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
            ]"
          >히스토리
          </button>
        </div>
      </div>

      <!-- Current Party Section -->
      <div v-if="activeTab === 'current'" class="space-y-4 min-h-[600px] place-items-center content-center">
        <div v-if="myActivePost" class="bg-[#141414] rounded-xl border border-[#ffffff1a] p-4 w-[60%]">
          <!-- 파티 상단 버튼들 -->
          <div class="flex justify-end gap-2 mb-4">
            <button
                @click="handleUpdatePartyStatus(myActivePost?.postCardDto.postId, 'CLOSE')"
                class="bg-[#2979FF] hover:bg-[#2565D1] text-white px-3 py-1.5 rounded flex items-center gap-1 text-sm"
            >
              <Users class="w-4 h-4"/>
              모집 마감
            </button>
            <button
                @click="handleUpdatePartyStatus(myActivePost?.postCardDto.postId, 'CANCEL')"
                class="bg-[#FF5252] hover:bg-[#D32F2F] text-white px-3 py-1.5 rounded flex items-center gap-1 text-sm"
            >
              <X class="w-4 h-4"/>
              모집 취소
            </button>
          </div>

          <!-- 파티 본문 -->
          <div class="bg-[#1A1A1A] p-3 rounded-lg mb-4 border-l-4 border-l-[#2979FF]">
            <p class="text-white leading-relaxed whitespace-pre-wrap break-words">
              {{ myActivePost?.postCardDto.content }}
            </p>
          </div>

          <!-- 포지션 섹션 -->
          <div class="space-y-3">
            <div v-for="(applicants, position) in myActivePost?.applicantByPositionMap" :key="position"
                 class="bg-[#1A1A1A] rounded-lg p-3">
              <!-- 현재 참여자 정보 -->
              <div class="flex items-center justify-between h-[52px]">
                <div class="flex items-center gap-2">
                  <img :src="getPositionImage(position)" :alt="position" class="w-5 h-5"/>
                </div>

                <!-- 참여자가 있는 경우 -->
                <template v-if="getParticipantByPosition[position]?.summonerInfoDto">
                  <div class="flex-1 text-left ml-2 min-w-0">
                    <div class="flex items-center gap-1">
                      <div class="text-xs font-medium text-white hover:text-[#2979FF] cursor-pointer truncate max-w-full">
                        {{ getParticipantByPosition[position].summonerInfoDto.summonerBasicInfoDto.gameName }}
                      </div>
                      <div class="text-[10px] text-gray-400 truncate">
                        #{{ getParticipantByPosition[position].summonerInfoDto.summonerBasicInfoDto.tagLine }}
                      </div>
                    </div>

                    <div class="flex gap-1 mt-0.5">
                      <span
                          v-for="tag in getParticipantByPosition[position].summonerInfoDto.frequentTagDtoList"
                          :key="tag.tagCode"
                          class="bg-[#2979FF]/10 text-[#2979FF] text-[9px] px-1 py-0.5 rounded"
                      >
                        {{ tag.tagValue }}
                      </span>
                    </div>
                  </div>

                  <div class="flex flex-col items-end">
                    <div class="flex items-center gap-1 text-[10px] mb-0.5">
                      <span class="text-gray-400">평가</span>
                      <span class="text-[#2979FF] font-medium">
                        {{ getParticipantByPosition[position].summonerInfoDto.reviewStatsDto.score?.toFixed(1) ?? 0 }}점
                      </span>
                    </div>

                    <div class="flex items-center gap-2">
                      <div class="flex items-center gap-1">
                        <ThumbsUp class="w-3 h-3 text-[#4CAF50]"/>
                        <span class="text-[#4CAF50] text-[10px]">
                          {{ getParticipantByPosition[position].summonerInfoDto.reviewStatsDto.upCount }}
                        </span>
                      </div>
                      <div class="flex items-center gap-1">
                        <ThumbsDown class="w-3 h-3 text-[#FF5252]"/>
                        <span class="text-[#FF5252] text-[10px]">
                          {{ getParticipantByPosition[position].summonerInfoDto.reviewStatsDto.downCount }}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div class="flex gap-1 ml-2">
                    <div
                        v-for="(champion, index) in getParticipantByPosition[position].summonerInfoDto.mostChampionDto?.slice(0, 2)"
                        :key="index"
                        class="bg-[#141414] rounded-lg p-1 flex flex-col items-center"
                    >
                      <img
                          :src="champion.iconUrl"
                          :alt="champion.nameKr"
                          class="w-5 h-5 rounded mb-0.5"
                      >
                      <span class="text-[9px] text-[#4CAF50]">
                        {{ champion.winRate }}%
                      </span>
                    </div>
                  </div>
                </template>

                <!-- 참여자가 없는 경우 -->
                <template v-else>
                  <div class="text-gray-500 text-xs flex-1 text-center">
                    {{ myActivePost?.postCardDto.participantDtoList.find(p => p.position === position)?.isOpenPosition ? '대기중' : '-' }}
                  </div>
                </template>

                <!-- 신청자 수와 토글 버튼 -->
                <div v-if="applicants?.length && !getParticipantByPosition[position]?.summonerInfoDto" class="flex items-center gap-2 ml-2">
                  <span class="text-[#2979FF] text-sm">신청자 {{ applicants.length }}명</span>
                  <button
                      @click="toggleApplicants(position)"
                      class="text-[#2979FF] hover:bg-[#2979FF1A] p-1 rounded"
                  >
                    <component :is="showApplicants[position] ? ChevronUp : ChevronDown" class="w-4 h-4"/>
                  </button>
                </div>
              </div>

              <!-- 신청자 목록 -->
              <div v-if="showApplicants[position] && applicants?.length"
                   class="mt-3 border border-[#333] rounded-lg overflow-hidden">
                <!-- 신청자 헤더 -->
                <div class="bg-[#1A1A1A] px-4 py-2 text-xs text-gray-400 flex items-center">
                  <div class="w-[240px]">신청자</div>
                  <div class="w-[140px]">평가</div>
                  <div class="w-[120px]">모스트 챔피언</div>
                  <div class="flex-1 text-right">승인/거절</div>
                </div>

                <!-- 신청자 리스트 -->
                <div class="divide-y divide-[#333]">
                  <div v-for="applicant in applicants"
                       :key="applicant"
                       class="px-4 py-3 flex items-center hover:bg-[#1A1A1A] transition-colors">
                    <!-- 신청자 정보 -->
                    <div class="w-[240px] flex items-center gap-3">
                      <img :src="applicant.summonerInfoDto.summonerBasicInfoDto.profileIconUrl"
                           :alt="applicant.summonerInfoDto.summonerBasicInfoDto.profileIconId"
                           class="w-10 h-10 rounded-lg"/>
                      <div>
                        <div class="flex items-center gap-1">
                          <span class="text-white text-sm font-medium">
                            {{ applicant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                          </span>
                          <span class="text-gray-400 text-xs">
                            #{{ applicant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
                          </span>
                        </div>
                        <div class="flex gap-1 mt-1">
                          <span v-for="tag in applicant.summonerInfoDto.frequentTagDtoList"
                                :key="tag.tagCode"
                                class="bg-[#2979FF]/10 text-[#2979FF] text-[9px] px-1.5 py-0.5 rounded">
                            {{ tag.tagValue }}
                          </span>
                        </div>
                      </div>
                    </div>

                    <!-- 평가 정보 -->
                    <div class="w-[140px] flex flex-col">
                      <div class="flex items-center gap-2">
                        <span class="text-[#2979FF] text-sm font-medium">
                          {{ applicant.summonerInfoDto.reviewStatsDto.score ?? 0 }}점
                        </span>
                        <span class="text-gray-400 text-xs">
                          ({{ applicant.summonerInfoDto.reviewStatsDto.totalReviewCnt }}회)
                        </span>
                      </div>
                      <div class="flex items-center gap-3 mt-1">
                        <div class="flex items-center gap-1">
                          <ThumbsUp class="w-3 h-3 text-[#4CAF50]"/>
                          <span class="text-[#4CAF50] text-xs">
                            {{ applicant.summonerInfoDto.reviewStatsDto.upCount }}
                          </span>
                        </div>
                        <div class="flex items-center gap-1">
                          <ThumbsDown class="w-3 h-3 text-[#FF5252]"/>
                          <span class="text-[#FF5252] text-xs">
                            {{ applicant.summonerInfoDto.reviewStatsDto.downCount }}
                          </span>
                        </div>
                      </div>
                    </div>

                    <!-- 모스트 챔피언 -->
                    <div class="w-[120px] flex gap-2">
                      <div v-for="(champion, index) in applicant.summonerInfoDto.mostChampionDto?.slice(0, 2)"
                           :key="index"
                           class="bg-[#141414] rounded-lg p-1.5 flex flex-col items-center">
                        <img :src="champion.iconUrl"
                             :alt="champion.nameKr"
                             class="w-6 h-6 rounded mb-1">
                        <span class="text-[#4CAF50] text-[10px]">{{ champion.winRate }}%</span>
                      </div>
                    </div>

                    <!-- 승인/거절 버튼 -->
                    <div class="flex-1 flex justify-end gap-2">
                      <button @click="handleApprove(applicant.postId, applicant.applicantPuuid, applicant.applicantSeq, position)"
                              class="bg-[#2979FF] hover:bg-[#2565D1] text-white px-3 py-1.5 rounded-lg flex items-center gap-1.5 text-xs font-medium transition-colors">
                        <UserCheck class="w-4 h-4"/>
                      </button>
                      <button @click="handleReject(applicant.postId, applicant.applicantPuuid, applicant.applicantSeq, position)"
                              class="bg-[#FF5252] hover:bg-[#D32F2F] text-white px-3 py-1.5 rounded-lg flex items-center gap-1.5 text-xs font-medium transition-colors">
                        <UserX class="w-4 h-4"/>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 파티 정보 -->
          <div class="bg-[#1A1A1A] rounded-lg p-3 mt-4 flex justify-between items-center">
            <div class="flex items-center gap-2">
              <span class="text-gray-400 text-xs">큐타입</span>
              <span class="text-white text-sm">{{ getGameModeName(myActivePost.postCardDto.gameMode) }}</span>
            </div>
            <div>
              <component :is="myActivePost.postCardDto.isUseMic ? Mic : MicOff"
                         :class="myActivePost.postCardDto.isUseMic ? 'text-[#2979FF]' : 'text-gray-500'"
                         class="w-4 h-4"/>
            </div>
          </div>
        </div>
        <div v-else class="flex-1 flex flex-col items-center justify-center">
          <img class="w-28 h-28 text-gray-600 mb-4" src="@/assets/icon/emoji/pengu.webp" alt="crying_bee">
          <h3 class="text-lg font-medium text-gray-200 mb-2">현재 등록된 파티가 없습니다</h3>
          <p class="text-gray-400 text-sm">새로운 파티를 등록해보세요!</p>
        </div>
      </div>

      <!-- History Section -->
      <div v-else class="space-y-4">
        <!-- Section Navigation -->
        <div class="flex space-x-2 mb-8 bg-[#141414] p-2 rounded-lg">
          <button
              @click="setActiveHistoryTab('hosted')"
              :class="[
                'flex items-center px-4 py-2 rounded-lg text-sm transition-colors flex-1',
                activeHistoryTab === 'hosted'
                  ? 'bg-[#2979FF] text-white'
                  : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
              ]"
          >
            <Users class="w-4 h-4 mr-2" />
            모집했던 파티
          </button>
          <button
              @click="setActiveHistoryTab('participated')"
              :class="[
                'flex items-center px-4 py-2 rounded-lg text-sm transition-colors flex-1',
                activeHistoryTab === 'participated'
                  ? 'bg-[#2979FF] text-white'
                  : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
              ]"
          >
            <History class="w-4 h-4 mr-2" />
            참가했던 파티
          </button>
          <button
              @click="setActiveHistoryTab('applied')"
              :class="[
                'flex items-center px-4 py-2 rounded-lg text-sm transition-colors flex-1',
                activeHistoryTab === 'applied'
                  ? 'bg-[#2979FF] text-white'
                  : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
              ]"
          >
            <Clock class="w-4 h-4 mr-2" />
            신청했던 파티
          </button>
        </div>

        <!-- 모집했던 파티 -->
        <div v-if="activeHistoryTab === 'hosted' && hostedPartyHistory"
             v-for="party in hostedPartyHistory"
             :key="party.postId"
             class="bg-[#141414] rounded-xl p-4"
        >
          {{party.postId}}
          <div class="flex justify-between items-start mb-4">
            <div class="flex flex-col">
              <span class="text-sm text-gray-400">{{ formatTimeAgo(party.regDt) }}</span>
              <div class="flex items-center gap-2 mt-1">
                <span class="text-white font-medium">{{ getGameModeName(party.gameMode) }}</span>
                <span :class="party.postStatus === 'CLOSE' ? 'text-[#2979FF]' : 'text-[#FF5252]'" class="text-sm">
                  {{ party.postStatus === 'CLOSE' ? '마감' : '취소' }}
                </span>
              </div>
            </div>
            <!-- 펼치기 버튼 -->
            <div class="flex items-center gap-2">
              <div class="flex items-center gap-1">
                <Users class="w-4 h-4 text-gray-400" />
                <span class="text-gray-400 text-sm">{{ party.participantCount }}/{{party.recruitCount}}</span>
              </div>
              <div class="flex items-center gap-1 cursor-pointer text-gray-400 hover:text-gray-300"
                   @click="togglePartyDetails(party.postId)">
                <component
                    :is="expandedParties.has(party.postId) ? ChevronUp : ChevronDown"
                    class="w-4 h-4"
                />
              </div>
            </div>
          </div>
          <div class="bg-[#1A1A1A] rounded-lg p-3 mb-3">
            <p class="text-white text-sm leading-relaxed">{{ party.content }}</p>
          </div>
          <!-- 펼쳐졌을 때 보이는 포지션별 참가자 정보 -->
          <div v-if="expandedParties.has(party.postId)"
               class="mt-3 space-y-2">
            <div v-for="participant in party.participantList"
                 :key="participant.position"
                 class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between">
              <div class="flex items-center gap-2">
                <img :src="getPositionImage(participant.position)" :alt="participant.position" class="w-5 h-5"/>
                <span class="text-gray-400 text-sm">{{ participant.position }}</span>
              </div>
              <div v-if="participant.summonerInfoDto" class="flex items-center gap-1">
                <span class="text-white text-sm">
                  {{ participant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                </span>
                <span class="text-gray-400 text-xs">
                  #{{ participant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
                </span>
              </div>
              <div v-else class="text-gray-500 text-sm">-</div>
            </div>
          </div>
        </div>

        <!-- 참가했던 파티 -->
        <div v-if="activeHistoryTab === 'participated' && participatedPartyHistory"
             v-for="party in participatedPartyHistory"
             :key="party.postId"
             class="bg-[#141414] rounded-xl p-4"
        >
          <div class="flex justify-between items-start mb-4">
            <div class="flex flex-col">
              <span class="text-sm text-gray-400">{{ formatTimeAgo(party.regDt) }}</span>
              <div class="flex items-center gap-2 mt-1">
                <span class="text-white font-medium">{{ getGameModeName(party.gameMode) }}</span>
                <span class="text-sm text-emerald-500">참가 완료</span>
              </div>
            </div>
            <div class="flex items-center gap-3">
              <!-- 신청인원 표시를 상단으로 이동 -->
              <div class="flex items-center gap-1">
                <Users class="w-4 h-4 text-gray-400" />
                <span class="text-gray-400 text-sm">{{ party.participantCount }}/{{party.recruitCount}}</span>
              </div>
              <div class="flex items-center gap-1 cursor-pointer text-gray-400 hover:text-gray-300"
                   @click="togglePartyDetails(party.postId)">
                <component
                    :is="expandedParties.has(party.postId) ? ChevronUp : ChevronDown"
                    class="w-4 h-4"
                />
              </div>
            </div>
          </div>
          <div class="bg-[#1A1A1A] rounded-lg p-3 mb-3">
            <p class="text-white text-sm leading-relaxed">{{ party.content }}</p>
          </div>
          <!-- 펼쳐졌을 때 보이는 포지션별 참가자 정보 -->
          <div v-if="expandedParties.has(party.postId)"
               class="mt-3 space-y-2">
            <div v-for="participant in party.participantList"
                 :key="participant.position"
                 class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between">
              <div class="flex items-center gap-2">
                <img :src="getPositionImage(participant.position)" :alt="participant.position" class="w-5 h-5"/>
                <span class="text-gray-400 text-sm">{{ participant.position }}</span>
              </div>
              <div v-if="participant.summonerInfoDto" class="flex items-center gap-1">
                <span class="text-white text-sm">
                  {{ participant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                </span>
                        <span class="text-gray-400 text-xs">
                  #{{ participant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
                </span>
              </div>
              <div v-else class="text-gray-500 text-sm">-</div>
            </div>
          </div>
        </div>

        <!-- 신청했던 파티 -->
        <div v-if="activeHistoryTab === 'applied' && appliedPartyHistory"
             v-for="party in appliedPartyHistory"
             :key="party.postId"
             class="bg-[#141414] rounded-xl p-4"
        >
          <div class="flex justify-between items-start mb-4">
            <div class="flex flex-col">
              <span class="text-sm text-gray-400">{{ formatTimeAgo(party.regDt) }}</span>
              <div class="flex items-center gap-2 mt-1">
                <span class="text-white font-medium">{{ getGameModeName(party.gameMode) }}</span>
                <span :class="getStatusColor(party.postStatus)" class="text-sm">
                  {{ getPostStatusName(party.postStatus) }}
                </span>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <!-- 신청인원 표시를 포지션 정보 옆으로 이동 -->
              <div class="flex items-center gap-2">
                <div class="flex items-center gap-1">
                  <Users class="w-4 h-4 text-gray-400" />
                  <span class="text-gray-400 text-sm">{{ party.participantCount }}/{{party.recruitCount}}</span>
                </div>
              </div>
              <div class="flex items-center gap-1 cursor-pointer text-gray-400 hover:text-gray-300"
                   @click="togglePartyDetails(party.postId)">
                <component
                    :is="expandedParties.has(party.postId) ? ChevronUp : ChevronDown"
                    class="w-4 h-4"
                />
              </div>
            </div>
          </div>
          <div class="bg-[#1A1A1A] rounded-lg p-3 mb-3">
            <p class="text-white text-sm leading-relaxed">{{ party.content }}</p>
          </div>
          <!-- 펼쳐졌을 때 보이는 포지션별 참가자 정보 -->
          <div v-if="expandedParties.has(party.postId)"
               class="mt-3 space-y-2">
            <div v-for="participant in party.participantList"
                 :key="participant.position"
                 class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between">
              <div class="flex items-center gap-2">
                <img :src="getPositionImage(participant.position)" :alt="participant.position" class="w-5 h-5"/>
                <span class="text-gray-400 text-sm">{{ participant.position }}</span>
              </div>
              <div v-if="participant.summonerInfoDto" class="flex items-center gap-1">
                <span class="text-white text-sm">
                  {{ participant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                </span>
                        <span class="text-gray-400 text-xs">
                  #{{ participant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
                </span>
              </div>
              <div v-else class="text-gray-500 text-sm">-</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div
          v-if="activeTab === 'history' && (
              (activeHistoryTab === 'hosted' && hostedPartyHistory?.length > 0) ||
              (activeHistoryTab === 'participated' && participatedPartyHistory?.length > 0) ||
              (activeHistoryTab === 'applied' && appliedPartyHistory?.length > 0)
          )"
          class="flex justify-center mt-6 gap-2"
      >
        <button
            v-if="currentPaging.hasPrevious"
            @click="handlePrevPage(activeTab)"
            class="px-3 h-8 rounded-lg text-sm font-medium bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]"
        >
          <
        </button>
        <button
            v-for="page in pageNumbers"
            :key="page"
            @click="handlePageChange(page)"
            :class="[
            'w-8 h-8 rounded-lg text-sm font-medium transition-colors',
            currentPaging.currentPage === page
              ? 'bg-[#2979FF] text-white'
              : 'bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]'
          ]"
        >
          {{ page }}
        </button>
        <button
            v-if="currentPaging.hasNext"
            @click="handleNextPage(activeTab)"
            class="px-3 h-8 rounded-lg text-sm font-medium bg-[#1A1A1A] text-gray-400 hover:bg-[#242424]"
        >
          >
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, computed, onMounted, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {
  ThumbsUp, ThumbsDown, Mic, MicOff, ChevronDown, ChevronUp,
  UserCheck, UserX, History, Users, X, Clock
} from 'lucide-vue-next'
import {createInitialPaging, formatTimeAgo, getPageNumbers} from '@/utils/common'
import {useToast} from 'vue-toastification'
import {useImageUrl} from '@/utils/imageUtil'
import {communityApi} from '@/api/community'
import {
  createInitialPartyCommunityHistoryResponse,
  type MyPartyPostDto,
  type PartyCommunityAppliedHistoryDto,
  type PartyCommunityHistoryDto,
  type PartyCommunityHistoryResponse
} from "@/types/community.ts";
import {assignWith} from "lodash";
import type {ReviewHistoryDto, ReviewHistoryResponse} from "@/types/review.ts";

const {getPositionImage} = useImageUrl()
const route = useRoute()
const router = useRouter()
const toast = useToast()
const activeHistoryTab = ref('hosted')
const expandedParties = ref(new Set<number>())
const ITEMS_PER_PAGE = 10;

const activeTab = ref('current')
const showApplicants = ref<{ [key in 'TOP' | 'JG' | 'MID' | 'BOT' | 'SUP']: boolean }>({
  'TOP': false,
  'JG': false,
  'MID': false,
  'BOT': false,
  'SUP': false
})

const myActivePost = ref<MyPartyPostDto | null>(null)
const hostedPartyHistory = ref<PartyCommunityHistoryDto[] | null>(null)
const participatedPartyHistory = ref<PartyCommunityHistoryDto[] | null>(null)
const appliedPartyHistory = ref<PartyCommunityAppliedHistoryDto[] | null>(null)
const hostedPaging = ref<PartyCommunityHistoryResponse>(createInitialPaging());
const participatedPaging = ref<PartyCommunityHistoryResponse>(createInitialPaging());
const appliedPaging = ref<PartyCommunityHistoryResponse>(createInitialPaging());
const pageNumbers = computed(() => getPageNumbers(currentPaging.value));

const currentPaging = computed(() => {
  switch (activeHistoryTab.value) {
    case 'hosted':
      return hostedPaging.value;
    case 'participated':
      return participatedPaging.value;
    case 'applied':
      return appliedPaging.value;
    default:
      return hostedPaging.value;
  }
});

const getParticipantByPosition = computed(() => {
  if (!myActivePost.value?.postCardDto.participantDtoList) return {};

  return myActivePost.value.postCardDto.participantDtoList.reduce((acc, participant) => {
    acc[participant.position] = participant;
    return acc;
  }, {} as Record<string, any>);
});

const setActiveHistoryTab = async (tab: string) => {
  activeHistoryTab.value = tab
  await fetchHistoryData(tab)
}

const togglePartyDetails = (partyId: number) => {
  if (expandedParties.value.has(partyId)) {
    expandedParties.value.delete(partyId)
  } else {
    expandedParties.value.add(partyId)
  }
}

const getStatusColor = (status: string) => {
  const colors = {
    'OPEN': 'text-[#4CAF50]',
    'CLOSE': 'text-[#2979FF]',
    'CANCEL': 'text-[#FF5252]',
  }
  return colors[status] || 'text-gray-400'
}

const getPostStatusName = (postStatus: string) => {
  if (postStatus === 'OPEN') {
    return '대기중'
  } else if (postStatus === 'CLOSE') {
    return '마감'
  } else if (postStatus === 'CANCEL') {
    return '취소'
  } else {
    return ''
  }
}

const getGameModeName = (code: string) => {
  const gameModeMap = {
    'ARAM': '칼바람나락',
    'NORMAL': '일반게임',
    'SOLO_RANK': '개인랭크',
    'FLEX_RANK': '자유랭크'
  }
  return gameModeMap[code] || code
}

const toggleApplicants = (position: 'TOP' | 'JG' | 'MID' | 'BOT' | 'SUP') => {
  showApplicants.value[position] = !showApplicants.value[position]
}

const handleTabChange = async (tab: string) => {
  await router.push({
    query: {...route.query, tab, page: '1'}
  })
}

const handlePageChange = async (page: number) => {
  await router.push({
    query: {...route.query, page: page.toString()}
  });
}

const handlePrevPage = () => {
  currentPaging.value.currentPage = currentPaging.value.startPage - 1;
  fetchHistoryData(activeHistoryTab.value);
}

const handleNextPage = () => {
  currentPaging.value.currentPage = currentPaging.value.endPage + 1;
  fetchHistoryData(activeHistoryTab.value);
}

const handleApprove = async (postId: number, applicantPuuid: string, applicantSeq: number, position: string) => {
  await communityApi.approvePartyApplicant(postId, applicantSeq, position)
  toast.success('승인되었습니다.')
  await fetchMyPartyPost()
}

const handleReject = async (postId: number, applicantPuuid: string, applicantSeq: number, position: string) => {
  await communityApi.rejectPartyApplicant(postId, applicantSeq, position)
  toast.success('거절되었습니다.')
  await fetchMyPartyPost()
}

const handleUpdatePartyStatus = (postId: number, status: string) => {
  if (status === 'CLOSE') {
    communityApi.closeParty(postId);
    toast.success('모집 마감되었습니다.')
    myActivePost.value = null
  } else {
    communityApi.cancelParty(postId);
    toast.success('모집 취소되었습니다.')
    myActivePost.value = null
  }
}

const fetchHistoryData = async (type: string) => {
  const page = currentPaging.value.currentPage;
  switch (type) {
    case 'hosted': {
      const hostedResponse = await communityApi.getPartyHostedHistory(page, ITEMS_PER_PAGE)
      hostedPartyHistory.value = hostedResponse.data.items
      hostedPaging.value = hostedResponse.data
      break
    }
    case 'participated': {
      const participatedResponse = await communityApi.getPartyParticipatedHistory(page, ITEMS_PER_PAGE)
      participatedPartyHistory.value = participatedResponse.data.items
      participatedPaging.value = participatedResponse.data
      break
    }
    case 'applied': {
      const appliedResponse = await communityApi.getPartyAppliedHistory(page, ITEMS_PER_PAGE)
      appliedPartyHistory.value = appliedResponse.data.items
      appliedPaging.value = appliedResponse.data
      break
    }
  }
}

const fetchMyPartyPost = async () => {
  const response = await communityApi.getMyPartyPost();
  myActivePost.value = response.data
}

onMounted(() => {
  if (route.query.tab) {
    activeTab.value = route.query.tab as string
  }
  if (route.query.page) {
    currentPaging.value.currentPage = parseInt(route.query.page as string)
  }

  watch(
      () => route.query,
      (query) => {
        if (query.tab) {
          activeTab.value = query.tab as string
        }
        if (query.page) {
          currentPaging.value.currentPage = parseInt(query.page as string)
          if (activeTab.value === 'history') {
            fetchHistoryData(activeHistoryTab.value)
          }
        }
      }, { immediate: true }
  )
})

/*watch([activeTab, activeHistoryTab], ([newTab, newHistoryTab]) => {
  if (newTab === 'history') {
    fetchHistoryData(newHistoryTab)
  }
}, { immediate: true })*/

onMounted(async () => {
  await fetchMyPartyPost();
})
</script>

<style scoped>
.highlight-party {
  background-color: rgba(41, 121, 255, 0.1);
  border-color: #2979FF;
  box-shadow: 0 0 0 1px #2979FF;
}

@keyframes fadeOut {
  from {
    background-color: rgba(41, 121, 255, 0.1);
    border-color: #2979FF;
    box-shadow: 0 0 0 1px #2979FF;
  }
  to {
    background-color: #141414;
    border-color: rgba(255, 255, 255, 0.1);
    box-shadow: none;
  }
}

.party-name:hover {
  cursor: pointer;
  color: #2979FF;
  text-decoration: underline;
}
</style>