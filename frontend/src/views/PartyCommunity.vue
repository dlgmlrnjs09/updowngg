<template>
  <div class="min-h-screen bg-[#0A0A0A] p-4 sm:p-6">
    <WriteModal
      v-if="showWriteModal"
      @submit="handleDuoSubmit"
      @close="showWriteModal = false"
    />

    <div class="max-w-6xl mx-auto mb-8">
      <!-- 새로고침 영역 -->
      <div class="flex justify-end items-center mb-4 gap-3">
        <span class="text-gray-400 text-sm">{{ countdown }}초 후 새로고침</span>
        <button
            @click="manualRefresh"
            class="bg-[#141414] text-[#2979FF] p-2 rounded-lg transition-colors"
            :class="{
        'animate-spin': isPolling,
        'hover:bg-[#1A1A1A]': countdown <= 5 && !isPolling,
        'opacity-50 cursor-not-allowed': countdown > 5 || isPolling
      }"
            :disabled="countdown > 5 || isPolling"
        >
          <RefreshCcw class="w-5 h-5" />
        </button>
      </div>
    </div>

    <div class="max-w-6xl mx-auto mb-8">
      <!-- 필터 영역 -->
      <div class="flex flex-col space-y-4 sm:flex-row sm:justify-between sm:items-center mb-8 sm:mb-14">
        <div class="grid grid-cols-2 gap-2 sm:bg-[#141414] sm:p-3 sm:rounded-xl sm:flex sm:gap-3 sm:flex-1 sm:mr-4">
          <select
              v-model="selectedGameMode"
              class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
              @change="onFilterUpdate"
          >
            <option value="">모든 큐</option>
            <option value="SOLO_RANK">솔로랭크</option>
            <option value="FLEX_RANK">자유랭크</option>
            <option value="NORMAL">일반게임</option>
            <option value="ARAM">칼바람나락</option>
          </select>
          <select
              v-model="selectedPosition"
              class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF]"
              @change="onFilterUpdate"
          >
            <option value="">모든 포지션</option>
            <option value="TOP">탑</option>
            <option value="JG">정글</option>
            <option value="MID">미드</option>
            <option value="AD">원딜</option>
            <option value="SUP">서포터</option>'
          </select>
          <select
              v-model="selectedTier"
              class="bg-[#1A1A1A] text-white text-sm px-3 py-2.5 sm:py-1.5 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#2979FF] col-span-2 sm:col-span-1"
              @change="onFilterUpdate"
          >
            <option value="">모든 티어</option>
            <option value="IRON">아이언</option>
            <option value="BRONZE">브론즈</option>
            <option value="SILVER">실버</option>
            <option value="GOLD">골드</option>
            <option value="PLATINUM">플래티넘</option>
          </select>
        </div>
        <button
            @click="showWriteModal = true"
            class="w-full sm:w-auto bg-[#2979FF] text-white px-4 py-3 sm:py-1.5 rounded-lg hover:bg-[#2565D1] transition-colors text-sm font-medium sm:h-12"
        >
          {{ isMobile ? '듀오 등록' : '듀오 등록하기' }}
        </button>
      </div>

      <!-- 내 파티 미니바 -->
      <div v-if="myActiveParty" class="max-w-6xl mx-auto mb-8">
        <div class="bg-[#141414] rounded-xl border border-[#2979FF] p-4">
          <!-- PC/태블릿 뷰 -->
          <div class="hidden sm:flex items-center justify-between">
            <!-- 좌측: 기본 정보 -->
            <div class="flex items-center gap-4">
              <div class="flex items-center gap-2">
                <span class="text-[#2979FF] text-sm font-medium">
                  {{ myActiveParty.postCardDto.writerPuuid === myPuuid ? '모집중' : '참가중' }}
                </span>
                <span class="text-white text-sm">
                  {{ getGameModeName(myActiveParty.postCardDto.gameMode) }}
                </span>
              </div>
              <div class="h-4 w-[1px] bg-gray-700"></div>
              <div class="flex items-center gap-2">
                <Users class="w-4 h-4 text-gray-400" />
                <span class="text-white text-sm">
                  {{ myActiveParty.postCardDto.participantCount}} / {{ myActiveParty.postCardDto.recruitCount }}
                </span>
              </div>
              <div class="h-4 w-[1px] bg-gray-700"></div>
              <!-- 포지션 정보 -->
              <div class="flex items-center gap-2">
                <template v-for="participant in myActiveParty.postCardDto.participantDtoList" :key="participant.position">
                  <div class="relative flex items-center gap-1 bg-[#1A1A1A] px-2 py-1 rounded-lg border border-gray-700">
                    <img :src="getPositionImage(participant.position)" :alt="participant.position" class="w-4 h-4">
                    <template v-if="participant.summonerInfoDto">
                      <span class="text-white text-xs truncate max-w-[80px]">
                        {{ participant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                      </span>
                    </template>
                    <template v-else>
                      <span class="text-[#2979FF] text-xs">대기중</span>
                      <!-- 신청자 -->
                      <template
                          v-if="myActiveParty.postCardDto.writerPuuid === myPuuid"
                          v-for="position in POSITION_ORDER"
                          :key="position"
                      >
                        <template v-if="myActiveParty?.applicantByPositionMap?.[position]?.length > 0">
                          <button
                              @click="toggleApplicants(participant.position)"
                              class="flex items-center gap-1 ml-1 text-[#2979FF] text-xs"
                          >
                          <span>
                            ({{ myActiveParty.applicantByPositionMap[position].length }})
                          </span>
                            <component
                                :is="showApplicants[position] ? ChevronUp : ChevronDown"
                                class="w-3 h-3"
                            />
                          </button>
                        </template>
                      </template>
                    </template>

                    <!-- 신청자 목록 팝오버 -->
                    <div v-if="showApplicants[participant.position] && myActiveParty.applicantByPositionMap[participant.position] && myActiveParty.postCardDto.writerPuuid === myPuuid"
                         class="absolute top-full left-0 mt-2 w-[300px] bg-[#1A1A1A] rounded-lg shadow-lg border border-gray-700 z-10">
                      <div v-for="applicant in myActiveParty.applicantByPositionMap[participant.position]"
                           :key="applicant.applicantSeq"
                           class="p-3 border-b border-gray-700 last:border-0">
                        <div class="flex items-center gap-3">
                          <img
                              :src="applicant.summonerInfoDto.summonerBasicInfoDto.profileIconUrl"
                              :alt="applicant.summonerInfoDto.summonerBasicInfoDto.gameName"
                              class="w-10 h-10 rounded-lg"
                          />
                          <div class="flex-1">
                            <div class="flex items-center gap-1">
                              <span class="text-white text-sm">
                                {{ applicant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                              </span>
                              <span class="text-gray-400 text-xs">
                                #{{ applicant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
                              </span>
                            </div>
                            <div class="flex gap-1 mt-1">
                               <span
                                   v-for="tag in applicant.summonerInfoDto.frequentTagDtoList"
                                   :key="tag.tagCode"
                                   class="bg-[#2979FF]/10 text-[#2979FF] text-[9px] px-1 py-0.5 rounded"
                               >
                                 {{ tag.tagValue }}
                               </span>
                            </div>
                          </div>
                          <div class="flex items-center gap-1">
                            <button
                                @click="handleApprove(myActiveParty.postCardDto.postId, applicant.summonerInfoDto.summonerBasicInfoDto.puuid, applicant.applicantSeq, participant.position)"
                                class="bg-[#2979FF] hover:bg-[#2565D1] text-white p-1.5 rounded">
                              <UserCheck class="w-4 h-4" />
                            </button>
                            <button
                                @click="handleReject(myActiveParty.postCardDto.postId, applicant.summonerInfoDto.summonerBasicInfoDto.puuid, applicant.applicantSeq, participant.position)"
                                class="bg-[#FF5252] hover:bg-[#D32F2F] text-white p-1.5 rounded">
                              <UserX class="w-4 h-4" />
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
              </div>
            </div>

            <!-- 우측: 액션 버튼 -->
            <div class="flex items-center gap-2">
              <component
                  :is="myActiveParty.postCardDto.isUseMic ? MicIcon : MicOffIcon"
                  :class="myActiveParty.postCardDto.isUseMic ? 'text-[#2979FF]' : 'text-gray-500'"
                  class="w-4 h-4"
              />
              <template v-if="myActiveParty.postCardDto.writerPuuid === myPuuid">
                <button
                    @click="handleUpdatePartyStatus(myActiveParty.postCardDto.postId, 'CLOSE')"
                    class="bg-[#1A1A1A] hover:bg-[#242424] text-white px-3 py-1.5 rounded-lg text-xs"
                >
                  마감
                </button>
                <button
                    @click="handleUpdatePartyStatus(myActiveParty.postCardDto.postId, 'CANCEL')"
                    class="bg-[#1A1A1A] hover:bg-[#242424] text-[#FF5252] px-3 py-1.5 rounded-lg text-xs"
                >
                  취소
                </button>
              </template>
              <template v-else>
                <button
                    @click="handleLeaveParty(myActiveParty.postCardDto.postId)"
                    class="bg-[#1A1A1A] hover:bg-[#242424] text-[#FF5252] px-3 py-1.5 rounded-lg text-xs"
                >
                  나가기
                </button>
              </template>
            </div>
          </div>

          <!-- 모바일 뷰 -->
          <div class="sm:hidden space-y-4">
            <!-- 상태 및 기본 정보 -->
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <span class="text-[#2979FF] text-sm font-medium">
                  {{ myActiveParty.postCardDto.writerPuuid === myPuuid ? '모집중' : '참가중' }}
                </span>
                <span class="text-white text-sm">{{ getGameModeName(myActiveParty.postCardDto.gameMode) }}</span>
              </div>
              <component
                  :is="myActiveParty.postCardDto.isUseMic ? MicIcon : MicOffIcon"
                  :class="myActiveParty.postCardDto.isUseMic ? 'text-[#2979FF]' : 'text-gray-500'"
                  class="w-4 h-4"
              />
            </div>

            <!-- 포지션 정보 -->
            <div class="grid grid-cols-1 gap-2">
              <div v-for="participant in myActiveParty.postCardDto.participantDtoList"
                   :key="participant.position"
                   class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between text-center h-[52px] w-full"
                   :class="{
                     'border-2 border-[#2979FF]': myActiveParty.postCardDto.writerPuuid === participant.summonerInfoDto?.summonerBasicInfoDto?.puuid,
                     'border border-[#333]': participant.isOpenPosition && !participant.summonerInfoDto,
                     'border border-gray-700': participant.summonerInfoDto && myActiveParty.postCardDto.writerPuuid !== participant.summonerInfoDto.summonerBasicInfoDto.puuid,
                     'border border-[#383838]': !(myActiveParty.postCardDto.writerPuuid === participant.summonerInfoDto?.summonerBasicInfoDto?.puuid) && !participant.isOpenPosition,
                     'opacity-50': !(myActiveParty.postCardDto.writerPuuid === participant.summonerInfoDto?.summonerBasicInfoDto?.puuid) && !participant.isOpenPosition
                   }"
              >
                <!-- 포지션 채워진 경우 -->
                <template v-if="participant.summonerInfoDto">
                  <div class="flex items-center gap-2">
                    <img
                        :src="getPositionImage(participant.position)"
                        :alt="participant.position"
                        class="w-5 h-5"
                    >
                  </div>

                  <div class="flex-1 text-left ml-2 min-w-0">
                    <div class="flex items-center gap-1">
                      <div class="text-xs font-medium text-white hover:text-[#2979FF] cursor-pointer truncate max-w-full">
                        {{ participant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                      </div>
                      <div class="text-[10px] text-gray-400 truncate">
                        #{{ participant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
                      </div>
                    </div>

                    <div class="flex gap-1 mt-0.5">
                      <span
                          v-for="tag in participant.summonerInfoDto.frequentTagDtoList"
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
                        {{ participant.summonerInfoDto.reviewStatsDto.score?.toFixed(1) ?? 0 }}점
                      </span>
                    </div>

                    <div class="flex items-center gap-2">
                      <div class="flex items-center gap-1">
                        <ThumbsUp class="w-3 h-3 text-[#4CAF50]" />
                        <span class="text-[#4CAF50] text-[10px]">
                          {{ participant.summonerInfoDto.reviewStatsDto.upCount }}
                        </span>
                      </div>
                      <div class="flex items-center gap-1">
                        <ThumbsDown class="w-3 h-3 text-[#FF5252]" />
                        <span class="text-[#FF5252] text-[10px]">
                          {{ participant.summonerInfoDto.reviewStatsDto.downCount }}
                        </span>
                      </div>
                    </div>
                  </div>
                  <div class="flex gap-1 ml-2">
                    <div v-for="(champion, index) in participant.summonerInfoDto.mostChampionDto.slice(0, 2)"
                         :key="index"
                         class="bg-[#141414] rounded-lg p-1 flex flex-col items-center">
                      <img
                          :src="champion.iconUrl"
                          :alt="champion.nameUs"
                          class="w-5 h-5 rounded mb-0.5"
                      >
                      <span class="text-[9px] text-[#4CAF50]">
                        {{ champion.winRate }}%
                      </span>
                    </div>
                  </div>
                </template>

                <!-- 대기 중인 경우 -->
                <template v-else>
                  <div class="flex items, center gap-2">
                    <img
                        :src="getPositionImage(participant.position)"
                        :alt="participant.position"
                        class="w-5 h-5"
                    >
                    <div class="text-gray-500 text-xs">
                      {{ participant.isOpenPosition ? '대기 중' : '-' }}
                    </div>
                  </div>
                  <template v-if="myActiveParty.postCardDto.writerPuuid === myPuuid && myActiveParty.applicantByPositionMap[participant.position]?.length > 0">
                    <button
                        @click="toggleApplicants(participant.position)"
                        class="flex items-center gap-1 text-[#2979FF] text-sm"
                    >
                      신청자 ({{ myActiveParty.applicantByPositionMap[participant.position].length }})
                      <component
                          :is="showApplicants[participant.position] ? ChevronUp : ChevronDown"
                          class="w-4 h-4"
                      />
                    </button>
                  </template>
                </template>
              </div>

              <!-- 신청자 목록 (모바일) -->
              <template
                  v-if="myActiveParty.postCardDto.writerPuuid === myPuuid"
                  v-for="(applicants, position) in myActiveParty.applicantByPositionMap"
                  :key="`applicants-${position}`"
              >
                <div v-if="showApplicants[position] && myActiveParty.applicantByPositionMap[position]"
                     class="space-y-2">
                  <div v-for="applicant in applicants"
                       :key="applicant.summonerInfoDto.summonerBasicInfoDto.puuid"
                       class="bg-[#141414] rounded-lg p-3">
                    <div class="flex items-center gap-3">
                      <img
                          :src="applicant.summonerInfoDto.summonerBasicInfoDto.profileIconUrl"
                          :alt="applicant.summonerInfoDto.summonerBasicInfoDto.gameName"
                          class="w-10 h-10 rounded-lg"
                      />
                      <div class="flex-1">
                        <div class="flex items-center gap-1">
                          <span class="text-white text-sm">
                            {{ applicant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                          </span>
                          <span class="text-gray-400 text-xs">
                            #{{ applicant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
                          </span>
                        </div>
                        <div class="flex gap-1 mt-1">
                        <span v-for="tag in applicant.summonerInfoDto.frequentTagDtoList"
                              :key="tag.tagCode"
                              class="bg-[#2979FF]/10 text-[#2979FF] text-[9px] px-1 py-0.5 rounded">
                          {{ tag.tagValue }}
                        </span>
                        </div>
                      </div>
                      <div class="flex items-center gap-1">
                        <button
                            @click="handleApprove(myActiveParty.postCardDto.postId, applicant.applicantPuuid, applicant.applicantSeq, position)"
                            class="bg-[#2979FF] hover:bg-[#2565D1] text-white p-1.5 rounded">
                          <UserCheck class="w-4 h-4" />
                        </button>
                        <button
                            @click="handleReject(myActiveParty.postCardDto.postId, applicant.applicantPuuid, applicant.applicantSeq, position)"
                            class="bg-[#FF5252] hover:bg-[#D32F2F] text-white p-1.5 rounded">
                          <UserX class="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </div>

            <!-- 액션 버튼 -->
            <div class="flex justify-end gap-2">
              <template v-if="myActiveParty.postCardDto.writerPuuid === myPuuid">
                <button
                    @click="handleUpdatePartyStatus(myActiveParty.postCardDto.postId, 'CLOSE')"
                    class="bg-[#1A1A1A] hover:bg-[#242424] text-white px-3 py-1.5 rounded-lg text-xs"
                >
                  마감
                </button>
                <button
                    @click="handleUpdatePartyStatus(myActiveParty.postCardDto.postId, 'CANCEL')"
                    class="bg-[#1A1A1A] hover:bg-[#242424] text-[#FF5252] px-3 py-1.5 rounded-lg text-xs"
                >
                  취소
                </button>
              </template>
              <template v-else>
                <button
                    @click="handleLeaveParty(myActiveParty.postCardDto.postId)"
                    class="bg-[#1A1A1A] hover:bg-[#242424] text-[#FF5252] px-3 py-1.5 rounded-lg text-xs"
                >
                  나가기
                </button>
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- 듀오 카드 그리드 -->
      <transition-group
          name="card-transition"
          tag="div"
          class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3"
      >
        <div
            v-for="card in postCards"
            :key="card.postId"
            class="bg-[#141414] rounded-xl p-4 border border-[#2979FF1A]"
        >
          <!-- 본문 -->
          <div class="bg-[#1A1A1A] p-3 rounded-lg mb-4 border-l-4 border-l-[#2979FF] min-h-[102px]">
            <p class="text-white leading-relaxed line-clamp-3 whitespace-pre-wrap break-words">
              {{ card.content }}
            </p>
          </div>
            <!-- 포지션 그리드 -->
            <div class="grid grid-cols-1 gap-2 mb-4">
              <!-- 각 포지션 슬롯 -->
              <div
                  v-for="participant in card.participantDtoList"
                  :key="participant.position"
                  class="bg-[#1A1A1A] rounded-lg p-2 flex items-center justify-between text-center h-[52px] w-full"
                  :class="{
                      'border-2 border-[#2979FF]': card.writerPuuid === participant.summonerInfoDto?.summonerBasicInfoDto?.puuid,
                      'border border-[#333]': participant.isOpenPosition && !participant.summonerInfoDto,
                      'border border-gray-700': participant.summonerInfoDto && card.writerPuuid !== participant.summonerInfoDto.summonerBasicInfoDto.puuid,
                      'border border-[#383838]': !(card.writerPuuid === participant.summonerInfoDto?.summonerBasicInfoDto?.puuid) && !participant.isOpenPosition,
                      'opacity-50': !(card.writerPuuid === participant.summonerInfoDto?.summonerBasicInfoDto?.puuid) && !participant.isOpenPosition // 모집하지 않는 포지션 어둡게
                  }"
              >
                <!-- 포지션 채워진 경우 -->
                <template v-if="participant.summonerInfoDto">
                  <div class="flex items-center gap-2">
                    <img
                        :src="getPositionImage(participant.position)"
                        :alt="participant.position"
                        class="w-5 h-5"
                    >
                  </div>

                  <div class="flex-1 text-left ml-2 min-w-0">
                    <div class="flex items-center gap-1">
                      <div
                          class="text-xs font-medium text-white hover:text-[#2979FF] cursor-pointer truncate max-w-full"
                          @click="goSelectedSummonerProfile(
                            participant.summonerInfoDto.summonerBasicInfoDto.gameName,
                            participant.summonerInfoDto.summonerBasicInfoDto.tagLine
                          )"
                      >
                        {{ participant.summonerInfoDto.summonerBasicInfoDto.gameName }}
                      </div>
                      <div class="text-[10px] text-gray-400 truncate">
                        #{{ participant.summonerInfoDto.summonerBasicInfoDto.tagLine }}
                      </div>
                    </div>

                    <!-- frequentTag 추가 -->
                    <div class="flex gap-1 mt-0.5">
                    <span
                        v-for="tag in participant.summonerInfoDto.frequentTagDtoList"
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
                        {{ participant.summonerInfoDto.reviewStatsDto.score?.toFixed(1) ?? 0 }}점
                      </span>
                    </div>

                    <div class="flex items-center gap-2">
                      <div class="flex items-center gap-1">
                        <ThumbsUp class="w-3 h-3 text-[#4CAF50]" />
                        <span class="text-[#4CAF50] text-[10px]">
                          {{ participant.summonerInfoDto.reviewStatsDto.upCount }}
                        </span>
                      </div>
                      <div class="flex items-center gap-1">
                        <ThumbsDown class="w-3 h-3 text-[#FF5252]" />
                        <span class="text-[#FF5252] text-[10px]">
                          {{ participant.summonerInfoDto.reviewStatsDto.downCount }}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div class="flex gap-1 ml-2">
                    <div
                        v-for="(champion, index) in participant.summonerInfoDto.mostChampionDto.slice(0, 2)"
                        :key="index"
                        class="bg-[#141414] rounded-lg p-1 flex flex-col items-center"
                    >
                      <img
                          :src="champion.iconUrl"
                          :alt="champion.nameUs"
                          class="w-5 h-5 rounded mb-0.5"
                      >
                      <span class="text-[9px] text-[#4CAF50]">
                        {{ champion.winRate }}%
                      </span>
                    </div>
                  </div>
                </template>

                <!-- 대기 중인 경우 -->
                <template v-else>
                  <img
                      :src="getPositionImage(participant.position)"
                      :alt="participant.position"
                      class="w-5 h-5"
                  >
                  <div class="text-gray-500 text-xs flex-1">
                    {{ participant.isOpenPosition ? '대기 중' : '-' }}
                  </div>
                  <button
                      v-if="shouldShowApplyButton(card, participant)"
                      @click="applyForPosition(card.postId, participant.position)"
                      :class="[
                        'text-[10px] px-2 py-0.5 rounded',
                        appliedPositions.get(`${card.postId}-${participant.position}`)
                          ? 'bg-gray-500 cursor-not-allowed'
                          : 'bg-[#2979FF]'
                      ]"
                      :disabled="appliedPositions.get(`${card.postId}-${participant.position}`)"
                  >
                    {{ appliedPositions.get(`${card.postId}-${participant.position}`) ? '신청완료' : '신청' }}
                  </button>
                </template>
              </div>
            </div>

          <!-- 추가 정보 -->
          <div class="bg-[#1A1A1A] rounded-lg p-3 flex justify-between items-center">
            <div class="flex items-center gap-2">
              <span class="text-gray-400 text-xs">큐타입</span>
              <span class="text-white text-sm">
                {{ getGameModeName(card.gameMode) }}
              </span>
            </div>
            <div>
              <MicIcon
                  v-if="card.isUseMic"
                  class="w-4 h-4 text-[#2979FF]"
              />
              <MicOffIcon
                  v-else
                  class="w-4 h-4 text-gray-500"
              />
            </div>
          </div>
        </div>
      </transition-group>

      <!-- 더보기 버튼 -->
      <div v-if="showReadMore" class="max-w-7xl mx-auto mb-20">
        <button
            class="w-full bg-[#141414] border border-[#2979FF] text-[#2979FF] py-3 rounded-lg text-sm font-medium hover:bg-[rgba(41,121,255,0.1)] transition-all disabled:opacity-70 disabled:cursor-not-allowed disabled:border-[#4a4a4a] disabled:text-[#4a4a4a]"
            @click="onLoadMore"
            :disabled="isLoading"
        >
          <div class="flex justify-center items-center h-5">
            <span v-if="!isLoading">더보기</span>
            <div v-else class="spinner"></div>
          </div>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { ThumbsUp, ThumbsDown, MicIcon, MicOffIcon, RefreshCcw, Users, ChevronUp, ChevronDown, UserX, UserCheck } from 'lucide-vue-next'
import WriteModal from '@/components/community/party/WriteModal.vue'
import type {
  CommunityPostDto, MyPartyPostDto,
  PartyCommunityApplicantDetailDto,
  PartyPostCardDto,
  SearchFilter
} from "@/types/community.ts"
import { communityApi } from "@/api/community.ts"
import { useToast } from "vue-toastification"
import { goSelectedSummonerProfile } from "@/utils/common.ts"
import { useImageUrl } from "@/utils/imageUtil.ts"
import {useAuthStore} from "@/stores/auth.ts";

const { getPositionImage } = useImageUrl()

const selectedPosition = ref('')
const selectedGameMode = ref('')
const selectedTier = ref('')
const showWriteModal = ref(false)
const showReadMore = ref(true)
const isLoading = ref(false)
const isPolling = ref(false)
/*const postCards = ref<DuoPostCardDto[]>([])*/
const currentStartIndex = ref(0)
const isMobile = computed(() => window.innerWidth < 640)
const postCards = ref<PartyPostCardDto[]>()
const appliedPositions = ref(new Map());
const updateInterval = ref<number>()
const UPDATE_INTERVAL = 10000
const countdown = ref(10)
const countdownInterval = ref<number>()

const toast = useToast()
const authStore = useAuthStore();
const myPuuid = authStore.user?.puuid;

const myActiveParty = ref<MyPartyPostDto | null>(null)

const POSITION_ORDER = ['TOP', 'JUNGLE', 'MID', 'AD', 'SUP'] as const;

const handleUpdatePartyStatus = (postId: number, status: string) => {
  if (status === 'CLOSE') {
    communityApi.closeParty(postId);
    toast.success('모집 마감되었습니다.')
    myActiveParty.value = null
  } else {
    communityApi.cancelParty(postId);
    toast.success('모집 취소되었습니다.')
    myActiveParty.value = null
  }
}

const handleApprove = async (postId: number, applicantPuuid: string, applicantSeq: number, position: string) => {
  await communityApi.approvePartyApplicant(postId, applicantPuuid, applicantSeq, position)
  toast.success('승인되었습니다.')
  await fetchMyPartyPost()
}

const handleReject = async (postId: number, applicantPuuid: string, applicantSeq: number, position: string) => {
  await communityApi.rejectPartyApplicant(postId, applicantPuuid, applicantSeq, position)
  toast.success('거절되었습니다.')
  await fetchMyPartyPost()
}

const getParticipantByPosition = computed(() => {
  if (!myActiveParty.value?.postCardDto.participantDtoList) return {};

  return myActiveParty.value.postCardDto.participantDtoList.reduce((acc, participant) => {
    acc[participant.position] = participant;
    return acc;
  }, {} as Record<string, any>);
});

// 신청자 포함된 목업 데이터
/*const myActiveParty = {
  postId: 1,
  content: "같이 솔랭 가실 서폿 구합니다",
  gameMode: "SOLO_RANK",
  isUseMic: true,
  isWriter: true,
  writerPuuid: "writer-123",
  // 신청자 정보를 포지션별로 관리
  applicantByPositionMap: {
    "SUP": [
      {
        applicantSeq: 1,
        postId: 1,
        position: "SUP",
        summonerInfoDto: {
          summonerBasicInfoDto: {
            puuid: "applicant-1",
            gameName: "서폿신청자1",
            tagLine: "KR1",
            profileIconUrl: "/icons/4.png"
          },
          frequentTagDtoList: [
            { tagCode: "P001", tagValue: "겜잘함" }
          ],
          reviewStatsDto: {
            score: 4.2,
            upCount: 42,
            downCount: 8,
            totalReviewCnt: 50
          },
          mostChampionDto: [
            {
              iconUrl: "/champions/lulu.png",
              nameKr: "룰루",
              nameUs: "Lulu",
              winRate: 62
            }
          ]
        }
      },
      {
        applicantSeq: 2,
        postId: 1,
        position: "SUP",
        summonerInfoDto: {
          summonerBasicInfoDto: {
            puuid: "applicant-2",
            gameName: "서폿신청자2",
            tagLine: "KR1",
            profileIconUrl: "/icons/5.png"
          },
          frequentTagDtoList: [
            { tagCode: "P002", tagValue: "실력자" }
          ],
          reviewStatsDto: {
            score: 4.5,
            upCount: 45,
            downCount: 5,
            totalReviewCnt: 50
          },
          mostChampionDto: [
            {
              iconUrl: "/champions/thresh.png",
              nameKr: "쓰레쉬",
              nameUs: "Thresh",
              winRate: 58
            }
          ]
        }
      }
    ]
  },
  participantDtoList: [
    {
      position: "MID",
      isOpenPosition: false,
      summonerInfoDto: {
        summonerBasicInfoDto: {
          puuid: "writer-123",
          gameName: "모크리",
          tagLine: "KR1",
          profileIconUrl: "/icons/1.png"
        },
        frequentTagDtoList: [
          { tagCode: "P001", tagValue: "겜잘함" },
          { tagCode: "P002", tagValue: "실력자" }
        ],
        reviewStatsDto: {
          score: 4.5,
          upCount: 45,
          downCount: 5,
          totalReviewCnt: 50
        },
        mostChampionDto: [
          {
            iconUrl: "/champions/ahri.png",
            nameKr: "아리",
            nameUs: "Ahri",
            winRate: 55
          },
          {
            iconUrl: "/champions/lux.png",
            nameKr: "럭스",
            nameUs: "Lux",
            winRate: 58
          }
        ]
      }
    },
    {
      position: "SUP",
      isOpenPosition: true,
      summonerInfoDto: null
    }
  ]
}*/

onMounted(async () => {
  setupVisibilityHandler()
  await fetchPosts({})
  await fetchAppliedPositions()
  await fetchMyPartyPost()
  startCountdown()

  // 10초마다 전체 업데이트
  updateInterval.value = window.setInterval(async () => {
    await checkUpdates()
  }, UPDATE_INTERVAL)
})

onUnmounted(() => {
  // 컴포넌트 언마운트 시 모든 interval 정리
  if (updateInterval.value) {
    clearInterval(updateInterval.value)
  }
  if (countdownInterval.value) {
    clearInterval(countdownInterval.value)
  }
  document.removeEventListener('visibilitychange', visibilityHandler)
})

// 카운트다운 시작 함수
const startCountdown = () => {
  // 기존 카운트다운이 있다면 제거
  if (countdownInterval.value) {
    clearInterval(countdownInterval.value)
  }

  countdown.value = 10
  countdownInterval.value = window.setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownInterval.value)
    }
  }, 1000)
}

// 수동 새로고침 함수
const manualRefresh = async () => {
  // 5초 이상 남았거나 이미 새로고침 중이면 실행하지 않음
  if (countdown.value > 5 || isPolling.value) return

  await checkUpdates()
  startCountdown()
}

const fetchAppliedPositions = async() => {
  const postIds = postCards.value?.map(p => p.postId);
  if (postIds && postIds.length > 0 && authStore.isAuthenticated) {
    const response = await communityApi.getApplyList(postIds);
    // 기존 신청 목록을 appliedPositions에 설정
    response.data.forEach((application: PartyCommunityApplicantDetailDto) => {
      appliedPositions.value.set(`${application.postId}-${application.position}`, true);
    });
  }
}

const handleDuoSubmit = async (formData: CommunityPostDto) => {
  await communityApi.insertPost('party', formData)
  showWriteModal.value = false
  toast.success('게시글 등록에 성공했습니다!')

  const currentFilter: SearchFilter = {
    gameMode: selectedGameMode.value,
    tier: selectedTier.value,
    positionSelf: selectedPosition.value,
    offset: 0,
    limit: 15
  }
  await fetchPosts(currentFilter)
}

const fetchPosts = async (filter: SearchFilter) => {
  isLoading.value = true
  const response = await communityApi.getPartyPost('party', filter)

  if (filter.offset === 0) {
    postCards.value = response.data
  } else {
    postCards.value = [...(postCards.value || []), ...response.data];
  }

  showReadMore.value = response.data.length >= 15
  isLoading.value = false

  return response.data
}

const fetchMyPartyPost = async () => {
  if (authStore.isAuthenticated) {
    const response = await communityApi.getMyPartyPost();
    myActiveParty.value = response.data
  }
}

const onFilterUpdate = () => {
  currentStartIndex.value = 0
  const reqDto: SearchFilter = {
    gameMode: selectedGameMode.value,
    tier: selectedTier.value,
    positionSelf: selectedPosition.value,
    offset: currentStartIndex.value,
    limit: 15
  }
  fetchPosts(reqDto)
}

const onLoadMore = () => {
  currentStartIndex.value += 15
  const reqDto: SearchFilter = {
    gameMode: selectedGameMode.value,
    tier: selectedTier.value,
    positionSelf: selectedPosition.value,
    offset: currentStartIndex.value,
    limit: 15
  }
  fetchPosts(reqDto)
}

const visibilityHandler = () => {
  if (document.hidden) {
    // 페이지 비활성화 시 모든 interval 제거
    if (updateInterval.value) {
      clearInterval(updateInterval.value)
      updateInterval.value = undefined
    }
    if (countdownInterval.value) {
      clearInterval(countdownInterval.value)
      countdownInterval.value = undefined
    }
  } else {
    // 페이지 활성화 시 다시 시작
    if (!updateInterval.value) {
      checkUpdates() // 즉시 한번 체크
      startCountdown() // 카운트다운 시작
      updateInterval.value = window.setInterval(async () => {
        await checkUpdates()
      }, UPDATE_INTERVAL)
    }
  }
}

const setupVisibilityHandler = () => {
  document.addEventListener('visibilitychange', visibilityHandler)
}

const checkUpdates = async () => {
  if (isPolling.value) return

  isPolling.value = true
  startCountdown()
  try {
    const currentFilter: SearchFilter = {
      gameMode: selectedGameMode.value,
      tier: selectedTier.value,
      positionSelf: selectedPosition.value,
      offset: 0,
      limit: currentStartIndex.value + 15
    }

    const response = await communityApi.getPartyPost('party', currentFilter)
    postCards.value = response.data
    await fetchAppliedPositions()
  } catch (error) {
    console.error('업데이트 체크 중 오류:', error)
  } finally {
    isPolling.value = false
  }
}

const applyForPosition = async (postId: number, position: string) => {
  await communityApi.applyParty(postId, position);
    appliedPositions.value.set(`${postId}-${position}`, true);
    toast.success("참가신청이 완료되었습니다.")
}

const shouldShowApplyButton = (card: PartyPostCardDto, currentParticipant: any) => {
  // 작성자인 경우 버튼 표시하지 않음
  if (card.writerPuuid === authStore.user?.puuid) {
    return false;
  }

  // 이미 다른 포지션에 참가자로 등록된 경우 버튼 표시하지 않음
  const isAlreadyParticipant = card.participantDtoList.some(participant =>
      participant.summonerInfoDto?.summonerBasicInfoDto?.puuid === authStore.user?.puuid
  );
  if (isAlreadyParticipant) {
    return false;
  }

  // 해당 포지션이 신청 가능한 상태인 경우에만 버튼 표시
  return currentParticipant.isOpenPosition && !currentParticipant.summonerInfoDto;
}


const showApplicants = ref<{[key: string]: boolean}>({
  'TOP': false,
  'JG': false,
  'MID': false,
  'AD': false,
  'SUP': false
})


// 신청자 목록 토글
const toggleApplicants = (position: string) => {
  showApplicants.value[position] = !showApplicants.value[position]
}

// 파티 나가기
const handleLeaveParty = async (postId: number) => {
  try {
    await communityApi.leaveParty(postId)
    toast.success('파티에서 나왔습니다.')

    // 파티 정보 새로고침
    await checkUpdates()
  } catch (error) {
    console.error('파티 나가기 실패:', error)
    toast.error('파티 나가기에 실패했습니다.')
  }
}

const getGameModeName = (code: string) => {
  const gameModeMap: {[key: string]: string} = {
    'ARAM': '칼바람나락',
    'NORMAL': '일반게임',
    'SOLO_RANK': '개인랭크',
    'FLEX_RANK': '자유랭크'
  }
  return gameModeMap[code] || code
}
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.spinner {
  border: 2px solid transparent;
  border-top: 2px solid #2979FF;
  border-right: 2px solid #2979FF;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  animation: spin 1s linear infinite;
}

.card-transition-move {
  transition: transform 0.5s ease;
}

.card-transition-enter-active {
  transition: all 0.5s ease;
}

.card-transition-leave-active {
  transition: all 0.5s ease;
  position: absolute; /* 제거될 때 다른 요소들의 재배치를 부드럽게 */
  width: calc(100% - 1rem); /* gap 고려한 너비 */
}

.card-transition-enter-from,
.card-transition-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 반응형에 따른 카드 너비 조정 */
@media (min-width: 640px) {
  .card-transition-leave-active {
    width: calc(50% - 1rem);
  }
}

@media (min-width: 1024px) {
  .card-transition-leave-active {
    width: calc(33.333% - 1rem);
  }
}
</style>