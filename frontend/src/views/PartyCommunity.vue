
<template>
  <div class="min-h-screen bg-[#0A0A0A] p-4 sm:p-6">
    <!-- 듀오 등록 모달 -->
    <WriteModal
        v-if="showWriteModal"
        @submit="handleDuoSubmit"
        @close="showWriteModal = false"
    />

    <!-- 온보딩 투어 가이드 -->
    <TourGuide 
      v-if="showTour" 
      :steps="tourSteps" 
      :show="showTour"
      :close-on-overlay-click="true"
      @close="closeTour"
      @step-changed="handleTourStepChange"
      @tour-completed="completeTour"
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
      <PartyFilter
          id="party-filter"
          :initial-game-mode="selectedGameMode"
          :initial-position="selectedPosition"
          :initial-tier="selectedTier"
          @filter-update="onFilterUpdate"
          @write="showWriteModal = true"
      />

      <!-- 내 파티 미니바 -->
      <MyActiveParty
          v-if="myActiveParty"
          id="active-party"
          :party="myActiveParty"
          :my-puuid="myPuuid"
          :has-new-applicant="hasNewApplicant"
          :participant-count="participantCount"
          @update-status="handleUpdatePartyStatus"
          @approve-applicant="handleApprove"
          @reject-applicant="handleReject"
          @leave-party="handleLeaveParty"
          @kick-member="handleKickMember"
      />
      
      <!-- 투어를 위한 가상 파티 - 항상 표시 (투어 모드일 때) -->
      <MockActiveParty 
          v-if="!myActiveParty && showTour" 
          id="active-party"
          :has-highlight="currentTourStep >= 4"
      />

      <!-- 듀오 카드 그리드 - 실제 카드들 (투어가 아닐 때만 표시) -->
      <PartyGrid
          v-if="!showTour"
          id="party-grid"
          :cards="postCards"
          :my-puuid="myPuuid"
          :applied-positions="appliedPositions"
          :is-loading="isLoading"
          :show-read-more="showReadMore"
          @apply="applyForPosition"
          @load-more="onLoadMore"
      />

      <!-- 투어를 위한 가상 파티 카드 (투어 중에만 표시) -->
      <div v-if="showTour" id="mock-party-grid" class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 card-transition">
        <MockPartyCard :has-highlight="currentTourStep === 3" />
      </div>
      
      <!-- 투어 시작 버튼 -->
      <button
        v-if="!showTour && !tourCompleted"
        @click="startTour"
        class="fixed bottom-8 right-8 bg-[#2979FF] text-white p-3 rounded-full shadow-lg hover:bg-[#1A67E0] transition-all z-50 animate-bounce-soft flex items-center group"
        style="animation-delay: 1s;"
        aria-label="파티 기능 설명 보기"
      >
        <HelpCircle class="w-6 h-6" />
        <span class="max-w-0 overflow-hidden whitespace-nowrap group-hover:max-w-xs transition-all duration-500 ease-linear bg-[#2979FF] rounded-l-full -ml-3 pl-5 pr-2">
          파티 기능 설명
        </span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { RefreshCcw, HelpCircle } from 'lucide-vue-next'
import PartyFilter from '@/components/community/party/PartyFilter.vue'
import MyActiveParty from '@/components/community/party/MyActiveParty.vue'
import MockActiveParty from '@/components/community/party/MockActiveParty.vue'
import MockPartyCard from '@/components/community/party/MockPartyCard.vue'
import PartyGrid from '@/components/community/party/PartyGrid.vue'
import WriteModal from '@/components/community/party/WriteModal.vue'
import TourGuide from '@/components/common/TourGuide.vue'
import type {
  CommunityPostDto, MyPartyPostDto,
  PartyCommunityApplicantDetailDto, PartyCommunityApplicantDto, PartyParticipantDto,
  PartyPostCardDto,
  SearchFilter
} from "@/types/community.ts"
import { communityApi } from "@/api/community.ts"
import { useToast } from "vue-toastification"
import { useAuthStore } from "@/stores/auth.ts"

// 상태 관리
const selectedPosition = ref('')
const selectedGameMode = ref('')
const selectedTier = ref('')
const showWriteModal = ref(false)
const showReadMore = ref(true)
const isLoading = ref(false)
const isPolling = ref(false)
const currentStartIndex = ref(0)
const isMobile = computed(() => window.innerWidth < 640)
const postCards = ref<PartyPostCardDto[]>([])
const appliedPositions = ref(new Map())
const updateInterval = ref<number>()
const UPDATE_INTERVAL = 10000
const countdown = ref(10)
const countdownInterval = ref<number>()

// 온보딩 투어 관련 상태
const showTour = ref(false)
const tourCompleted = ref(false)
const TOUR_COMPLETED_KEY = 'party_tour_completed'
const currentTourStep = ref(1)
const shouldShowMockParty = ref(false)

// 온보딩 투어 스텝 정의 - 기본 스텝
const baseSteps = [
  {
    target: '#party-filter',
    title: '파티 필터',
    content: '원하는 게임 모드, 티어, 포지션을 선택하여 맞춤형 파티를 찾을 수 있습니다.',
    position: 'bottom',
    margin: 5
  },
  {
    target: '#party-filter .write-button',
    title: '파티 생성',
    content: '이 버튼을 클릭하여 새로운 파티를 생성할 수 있습니다. 게임 모드, 마이크 사용 여부, 모집할 포지션을 선택하세요.',
    position: 'left',
    margin: 5
  },
  {
    target: '#mock-party-grid > div:first-child',
    title: '파티 카드',
    content: '각 카드에는 파티 정보와 참여 가능한 포지션이 표시됩니다. 원하는 파티를 찾아 참가해보세요.',
    position: 'right',
    margin: 10
  },
  {
    target: '.apply-btn-container.position-buttons',
    title: '포지션 신청',
    content: '원하는 포지션의 신청 버튼을 클릭하여 파티 참가를 신청하세요. 파티장이 승인하면 파티에 합류됩니다.',
    position: 'right',
    margin: 5
  }
]

// 내 활성 파티 관련 스텝
const myPartySteps = [
  {
    target: '#active-party',
    title: '내 활성 파티',
    content: '내가 참여중인 파티 정보입니다. 파티장이면 신청자 관리, 모집 마감, 취소가 가능하며, 일반 멤버는 파티에서 나갈 수 있습니다.',
    position: 'bottom',
    margin: 5
  },
  {
    target: '.applicant-button',
    title: '신청자 관리',
    content: '파티장은 이 버튼을 통해 포지션별 신청자를 확인하고 승인하거나 거절할 수 있습니다. 신청자가 있으면 숫자로 표시됩니다.',
    position: 'right',
    margin: 5
  }
]

// 모든 사용자에게 전체 투어 스텝을 보여줌 (항상 모든 단계 표시)
const tourSteps = computed(() => {
  return [...baseSteps, ...myPartySteps]
})

const toast = useToast()
const authStore = useAuthStore()
const myPuuid = computed(() => authStore.user?.puuid)
const myActiveParty = ref<MyPartyPostDto | null>(null)
const hasNewApplicant = ref(false)
const participantCount = ref<number | null>(null)
const myApplicationsStatus = ref<Map<string, string>>(new Map()); // key: `${postId}-${position}`, value: 'PENDING', 'APPROVED' 등
const previousApplicationsStatus = ref<Map<string, string>>(new Map());
const isApplyingPosition = ref(false);
const lastAppliedTime = ref(new Map<string, number>());

// 투어 관련 메서드
const startTour = () => {
  // 리셋 및 초기화
  currentTourStep.value = 1
  showTour.value = true
}

const closeTour = () => {
  showTour.value = false
  shouldShowMockParty.value = false
}

const handleTourStepChange = (step: number) => {
  // 현재 스텝 저장
  currentTourStep.value = step
  
  // 특정 스텝에서 필요한 추가 액션 처리
  console.log(`Tour step changed to ${step}`)
}

const completeTour = () => {
  showTour.value = false
  tourCompleted.value = true
  shouldShowMockParty.value = false
  
  // 로컬 스토리지에 투어 완료 상태 저장
  localStorage.setItem(TOUR_COMPLETED_KEY, 'true')
  
  // 축하 메시지 및 시각적 피드백
  toast.success('축하합니다! 파티 기능 설명을 완료했습니다. 이제 파티에 참여해보세요.')
  
  // 애니메이션 효과를 위한 임시 요소 생성 및 제거
  const confetti = document.createElement('div')
  confetti.className = 'confetti-container'
  document.body.appendChild(confetti)
  
  // 브랜드 색상 - 주로 파란색(파티 테마), 하얀색, 그린
  const colors = ['#2979FF', '#1A67E0', '#FFFFFF', '#4CAF50', '#1976D2', '#E3F2FD', '#64B5F6']
  
  // 40개의 색종이 생성
  for (let i = 0; i < 40; i++) {
    const confettiPiece = document.createElement('div')
    confettiPiece.className = 'confetti-piece'
    
    // 랜덤 위치 및 크기
    confettiPiece.style.left = `${Math.random() * 100}%`
    const size = Math.random() * 10 + 5
    confettiPiece.style.width = `${size}px`
    confettiPiece.style.height = `${size}px`
    
    // 색상 및 모양
    confettiPiece.style.backgroundColor = colors[Math.floor(Math.random() * colors.length)]
    confettiPiece.style.borderRadius = Math.random() > 0.5 ? '50%' : '3px'
    
    // 애니메이션 랜덤화
    confettiPiece.style.animationDelay = `${Math.random() * 1.5}s`
    confettiPiece.style.animationDuration = `${Math.random() * 2 + 2}s`
    
    // 랜덤 좌우 이동 (CSS 변수 설정)
    confettiPiece.style.setProperty('--random', Math.random().toString())
    
    confetti.appendChild(confettiPiece)
  }
  
  // 3.5초 후 색종이 효과 제거
  setTimeout(() => {
    if (document.body.contains(confetti)) {
      document.body.removeChild(confetti)
    }
  }, 3500)
  
  // 1분 후에 도움 버튼 다시 표시
  setTimeout(() => {
    tourCompleted.value = false
  }, 60000)
}

onMounted(async () => {
  setupVisibilityHandler()
  await fetchPosts({})
  await fetchAppliedPositions()
  await fetchMyPartyPost()
  await fetchMyApplicationStatus();
  startCountdown()

  // 로컬 스토리지에서 투어 완료 상태 확인
  const completedStatus = localStorage.getItem(TOUR_COMPLETED_KEY)
  tourCompleted.value = completedStatus === 'true'
  
  // 첫 방문이고 투어를 완료하지 않은 사용자에게 2초 후 자동으로 투어 시작
  if (!tourCompleted.value && postCards.value.length > 0) {
    setTimeout(() => {
      startTour()
    }, 2000)
  }

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

// 파티 인원수 변화 감지
watch(() => myActiveParty.value?.postCardDto?.participantDtoList, (newParticipants, oldParticipants) => {
  if (!oldParticipants || !newParticipants) return;

  const changes = compareParticipants(oldParticipants, newParticipants);

  if (changes) {
    // 나간 사람에 대한 toast 표시
    changes.left.forEach(summoner => {
      toast.info(`${summoner.gameName}님이 파티에서 나갔습니다.`);
    });

    // 들어온 사람에 대한 toast 표시
    changes.joined.forEach(summoner => {
      toast.success(`${summoner.gameName}님이 파티에 참가하였습니다.`);
    });

    // 누군가 들어왔을 때 참가자 수 애니메이션 업데이트
    if (changes.joined.length > 0) {
      participantCount.value = myActiveParty.value.postCardDto.participantCount;
      setTimeout(() => participantCount.value = null, 2000);
    }
  }
}, { deep: true });

// 신청자 변화 감지
watch(() => myActiveParty.value?.applicantByPositionMap, (newVal, oldVal) => {
  if (!oldVal || !newVal) return;

  // 내가 파티장인지 확인
  const isPartyLeader = myActiveParty.value?.postCardDto?.writerPuuid === myPuuid.value;

  if (isPartyLeader) {
    const newApplicants = compareApplicants(oldVal, newVal);

    // 새 신청자에 대한 toast 표시
    newApplicants.forEach(({ applicant, position }) => {
      toast.info(`${applicant.gameName}님이 ${getPositionName(position)} 포지션에 파티참가 신청하였습니다.`);
    });

    if (newApplicants.length > 0) {
      hasNewApplicant.value = true;
      setTimeout(() => hasNewApplicant.value = false, 2000);
    }
  }
}, { deep: true });

// 내 파티 생성/참가/강퇴 감지
watch(() => myActiveParty.value, (newVal, oldVal) => {
  if (!oldVal && newVal) {
    // 파티 생성/참가
    hasNewApplicant.value = true
    setTimeout(() => hasNewApplicant.value = false, 2000) // 2초 후 효과 제거
  } else if (oldVal && !newVal) {
    // 파티에서 사라짐 (강퇴된 경우)
    toast.info('파티에서 강퇴되었습니다.');
  }
})

// 참가자 목록을 비교하여 참가자, 탈퇴자 식별
const compareParticipants = (oldParticipants: PartyParticipantDto[], newParticipants: PartyParticipantDto[]) => {
  if (!oldParticipants || !newParticipants) return null;

  // 더 쉬운 비교를 위해 Map으로 변환
  const oldMap = new Map();
  const newMap = new Map();

  oldParticipants.forEach(participant => {
    if (participant.summonerInfoDto) {
      oldMap.set(participant.position, participant.summonerInfoDto.summonerBasicInfoDto);
    }
  });

  newParticipants.forEach(participant => {
    if (participant.summonerInfoDto) {
      newMap.set(participant.position, participant.summonerInfoDto.summonerBasicInfoDto);
    }
  });

  // 나간 사람 확인
  const left: any[] = [];
  oldMap.forEach((summoner, position) => {
    const newSummoner = newMap.get(position);
    if (!newSummoner || newSummoner.puuid !== summoner.puuid) {
      left.push(summoner);
    }
  });

  // 들어온 사람 확인
  const joined: any[] = [];
  newMap.forEach((summoner, position) => {
    const oldSummoner = oldMap.get(position);
    if (!oldSummoner || oldSummoner.puuid !== summoner.puuid) {
      joined.push(summoner);
    }
  });

  return { left, joined };
};

// 2. 신청자 비교해서 새로운 신청자 식별
const compareApplicants = (oldApplicantMap: any, newApplicantMap: any) => {
  if (!oldApplicantMap || !newApplicantMap) return [];

  const newApplicants: any[] = [];

  // 각 포지션별로 확인
  Object.keys(newApplicantMap).forEach(position => {
    const oldApplicants = oldApplicantMap[position] || [];
    const newApplicantsList = newApplicantMap[position] || [];

    // 이전 목록에 없던 신청자 찾기
    newApplicantsList.forEach(applicant => {
      const isNew = !oldApplicants.some(
          oldApp => oldApp.summonerInfoDto.summonerBasicInfoDto.puuid ===
              applicant.summonerInfoDto.summonerBasicInfoDto.puuid
      );

      if (isNew) {
        newApplicants.push({
          position,
          applicant: applicant.summonerInfoDto.summonerBasicInfoDto
        });
      }
    });
  });

  return newApplicants;
};

const handleUpdatePartyStatus = async (postId: number, status: string) => {
  if (status === 'CLOSE') {
    await communityApi.closeParty(postId)
    toast.success('모집 마감되었습니다.')
    myActiveParty.value = null
  } else {
    await communityApi.cancelParty(postId)
    toast.success('모집 취소되었습니다.')
    myActiveParty.value = null
  }

  await checkUpdates()
}

const handleApprove = async (postId: number, applicantPuuid: string, applicantSeq: number, position: string) => {
  await communityApi.approvePartyApplicant(postId, applicantPuuid, applicantSeq, position)
  toast.success('승인되었습니다.')
  await checkUpdates()
}

const handleReject = async (postId: number, applicantPuuid: string, applicantSeq: number, position: string) => {
  await communityApi.rejectPartyApplicant(postId, applicantPuuid, applicantSeq, position)
  toast.success('거절되었습니다.')
  await checkUpdates()
}

// 카운트다운 시작 함수
const startCountdown = () => {
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
  if (countdown.value > 5 || isPolling.value) return

  await checkUpdates()
  startCountdown()
}

const fetchAppliedPositions = async() => {
  const postIds = postCards.value?.map(p => p.postId)
  if (postIds && postIds.length > 0 && authStore.isAuthenticated) {
    // 맵 초기화 (기존 상태 제거)
    appliedPositions.value = new Map();

    // 현재 활성 상태인 신청만 가져옴
    const response = await communityApi.getApplyList(postIds)
    response.data.forEach((application: PartyCommunityApplicantDetailDto) => {
        appliedPositions.value.set(`${application.postId}-${application.position}`, true)
    })
  }
}

const handleDuoSubmit = async (formData: CommunityPostDto) => {
  await communityApi.insertPost('party', formData)
  showWriteModal.value = false
  toast.success('게시글 등록에 성공했습니다!')
  await checkUpdates()
}

const fetchPosts = async (filter: SearchFilter) => {
  isLoading.value = true
  const response = await communityApi.getPartyPost('party', filter)

  if (filter.offset === 0) {
    postCards.value = response.data
  } else {
    postCards.value = [...(postCards.value || []), ...response.data]
  }

  showReadMore.value = response.data.length >= 15
  isLoading.value = false

  return response.data
}

const fetchMyPartyPost = async () => {
  if (authStore.isAuthenticated) {
    const response = await communityApi.getMyPartyPost()
    myActiveParty.value = response.data
  }
}

// 내 파티 신청 상태 조회 함수
const fetchMyApplicationStatus = async () => {
  if (!authStore.isAuthenticated) return;

  const response = await communityApi.getMyApplyList();

  // 이전 상태 저장
  previousApplicationsStatus.value = new Map(myApplicationsStatus.value);

  // 새로운 상태 맵 생성
  const newStatusMap = new Map();

  if (response.data && Array.isArray(response.data)) {
    response.data.forEach((application: PartyCommunityApplicantDto) => {
      const key = `${application.postId}-${application.position}`;
      const status = application.applyStatus || 'PENDING';
      newStatusMap.set(key, status);

      // appliedPositions 맵 업데이트 (PENDING 상태가 아니면 신청 버튼을 다시 활성화하기 위해 제거)
      if (status !== 'PENDING') {
        appliedPositions.value.delete(key);
      } else if (status === 'PENDING') {
        appliedPositions.value.set(key, true);
      }
    });
  }

  // 상태 변경 확인 및 토스트 메시지 표시
  checkApplicationStatusChanges(previousApplicationsStatus.value, newStatusMap);

  // 새 상태로 업데이트
  myApplicationsStatus.value = newStatusMap;
};


// 상태 변경 감지 및 토스트 메시지 표시 함수
const checkApplicationStatusChanges = (prevStatusMap: Map<string, string>, newStatusMap: Map<string, string>) => {
  // 승인된 파티 ID를 추적 (동일 파티 내 다른 포지션 신청건의 상태 변경 알림 억제용)
  const approvedPartyIds = new Set<number>();

  // 먼저 승인된 파티 ID 확인
  newStatusMap.forEach((newStatus, key) => {
    if (newStatus === 'APPROVE') {
      const [postIdStr] = key.split('-');
      approvedPartyIds.add(parseInt(postIdStr));
    }
  });

  // 각 신청에 대해 상태 변경 확인
  newStatusMap.forEach((newStatus, key) => {
    const prevStatus = prevStatusMap.get(key);

    // 상태가 변경된 경우 (이전에는 'PENDING'이었고 이제는 다른 상태인 경우)
    if (prevStatus === 'PENDING' && newStatus !== 'PENDING') {
      const [postIdStr, position] = key.split('-');
      const postId = parseInt(postIdStr);

      // 이미 PENDING이 아니면 appliedPositions에서 제거
      appliedPositions.value.delete(key);

      // 해당 파티 찾기
      const party = postCards.value.find(card => card.postId === postId);

      if (party) {
        // 승인 알림은 항상 표시
        if (newStatus === 'APPROVE') {
          toast.success('파티에 가입되었습니다.');
        }
        // 거절/취소 알림도 항상 표시
        else if (newStatus === 'REJECT' || newStatus === 'CANCEL') {
          if (newStatus === 'REJECT') {
            toast.error('파티 가입 신청이 거절되었습니다.');
          } else if (newStatus === 'CANCEL') {
            toast.info(`파티 가입 신청이 취소되었습니다.`);
          }
        }
      }
    }
  });

  // 목록에서 사라진 신청 확인 (거절된 경우)
  /*prevStatusMap.forEach((prevStatus, key) => {
    if (!newStatusMap.has(key) && prevStatus === 'PENDING') {
      // 신청이 목록에서 사라졌으면 거절된 것으로 간주
      appliedPositions.value.delete(key);

      const [postIdStr, position] = key.split('-');
      const postId = parseInt(postIdStr);

      // 만약 동일 파티에 승인된 포지션이 있다면 알림 표시하지 않음
      if (!approvedPartyIds.has(postId)) {
        const party = postCards.value.find(card => card.postId === postId);
        if (party) {
          toast.error(`파티 가입 신청이 거절되었습니다.`);
        }
      }
    }
  });*/
};

const onFilterUpdate = (filter: { gameMode: string; position: string; tier: string }) => {
  selectedGameMode.value = filter.gameMode
  selectedPosition.value = filter.position
  selectedTier.value = filter.tier

  currentStartIndex.value = 0

  const reqDto: SearchFilter = {
    gameMode: filter.gameMode,
    tier: filter.tier,
    positionSelf: filter.position,
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
    if (updateInterval.value) {
      clearInterval(updateInterval.value)
      updateInterval.value = undefined
    }
    if (countdownInterval.value) {
      clearInterval(countdownInterval.value)
      countdownInterval.value = undefined
    }
  } else {
    if (!updateInterval.value) {
      checkUpdates()
      startCountdown()
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
  if (isPolling.value) return;

  isPolling.value = true;
  startCountdown();
  try {
    const currentFilter: SearchFilter = {
      gameMode: selectedGameMode.value,
      tier: selectedTier.value,
      positionSelf: selectedPosition.value,
      offset: 0,
      limit: currentStartIndex.value + 15
    };

    // 현재 appliedPositions 상태 저장
    const currentAppliedPositions = new Map(appliedPositions.value);

    // 모든 API 호출을 병렬로 처리하여 시간 단축
    const [postsResponse, myPartyResponse] = await Promise.all([
      communityApi.getPartyPost('party', currentFilter),
      authStore.isAuthenticated ? communityApi.getMyPartyPost() : Promise.resolve({ data: null })
    ]);

    // 포스트 데이터 업데이트
    postCards.value = postsResponse.data;
    // 내 파티 데이터 업데이트
    if (myPartyResponse.data) {
      myActiveParty.value = myPartyResponse.data;
    } else {
      // 데이터가 없거나 204 응답인 경우 myActiveParty를 null로 설정
      myActiveParty.value = null;
    }

    // postIds 추출 후 appliedPositions 업데이트
    const postIds = postCards.value?.map(p => p.postId);
    if (postIds && postIds.length > 0 && authStore.isAuthenticated) {
      const applyResponse = await communityApi.getApplyList(postIds);

      // 새로운 Map 생성 (기존 값 복사하지 않고 완전히 새로 생성)
      const newAppliedPositions = new Map();

      // PENDING 상태인 신청만 추가
      applyResponse.data.forEach((application: PartyCommunityApplicantDetailDto) => {
        newAppliedPositions.set(`${application.postId}-${application.position}`, true);
      });

      // 기존에 방금 신청한 항목이 API에 아직 반영되지 않았다면 유지
      currentAppliedPositions.forEach((value, key) => {
        const [postId, position] = key.split('-');
        // API 응답에 없지만 방금 신청한 항목인 경우 유지
        if (!newAppliedPositions.has(key) &&
            myApplicationsStatus.value.get(key) === 'PENDING' &&
            new Date().getTime() - (lastAppliedTime.value.get(key) || 0) < 5000) {
          newAppliedPositions.set(key, value);
        }
      });

      // 상태 업데이트 (한번에 적용)
      appliedPositions.value = newAppliedPositions;
    }

    // 신청 상태 업데이트
    await fetchMyApplicationStatus();
  } catch (error) {
    console.error('업데이트 체크 중 오류:', error);
  } finally {
    isPolling.value = false;
  }
};

const applyForPosition = async (postId: number, position: string) => {
  // 이미 신청 중인 경우 중복 호출 방지
  if (isApplyingPosition.value) {
    return;
  }

  // 신청 중 상태로 설정
  isApplyingPosition.value = true;

  try {
    // UI 즉시 업데이트 (사용자 피드백 향상)
    const key = `${postId}-${position}`;
    appliedPositions.value.set(key, true);
    myApplicationsStatus.value.set(key, 'PENDING');
    lastAppliedTime.value.set(key, new Date().getTime()); // 신청 시간 기록

    // API 호출
    await communityApi.applyParty(postId, position);
    toast.success("참가신청이 완료되었습니다.");

    // 별도의 checkUpdates 호출 없이 로컬 상태만 업데이트
    // API 호출 이후 자동으로 폴링될 때 서버 상태와 동기화됨
  } catch (error) {
    // 오류 발생 시 상태 복원
    appliedPositions.value.delete(`${postId}-${position}`);
    myApplicationsStatus.value.delete(`${postId}-${position}`);
    lastAppliedTime.value.delete(`${postId}-${position}`);
  } finally {
    // 신청 상태 초기화
    isApplyingPosition.value = false;
  }
};

const handleLeaveParty = async (postId: number) => {
  await communityApi.leaveMyParty(postId)
  toast.success('파티 탈퇴되었습니다.')
  await checkUpdates()
}

const handleKickMember = async (postId: number, memberPuuid: string) => {
  try {
    await communityApi.kickPartyMember(postId, memberPuuid)
    toast.success('파티원이 강퇴되었습니다.')
    await checkUpdates()
  } catch (error) {
    console.error('파티원 강퇴 중 오류:', error)
    toast.error('파티원 강퇴에 실패했습니다.')
  }
}

const getPositionName = (position: string) => {
  const positionMap = {
    'TOP': '탑',
    'JG': '정글',
    'MID': '미드',
    'AD': '원딜',
    'SUP': '서포터'
  };
  return positionMap[position] || position;
};

</script>

<style scoped>
/* 이제 대부분의 스타일이 각 컴포넌트로 이동했습니다 */
@keyframes bounce-soft {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.animate-bounce-soft {
  animation: bounce-soft 2s infinite;
  animation-timing-function: cubic-bezier(0.28, 0.84, 0.42, 1);
}

/* 색종이 효과 스타일 */
.confetti-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 9999;
  overflow: hidden;
}

.confetti-piece {
  position: absolute;
  top: -10px;
  animation: confetti-fall 3s linear forwards;
  border-radius: 3px;
  opacity: 0.8;
}

@keyframes confetti-fall {
  0% {
    top: -20px;
    transform: rotate(0deg) translateX(0);
    opacity: 1;
  }
  100% {
    top: 100vh;
    transform: rotate(720deg) translateX(calc(5vw - 2.5vw * var(--random, 0.5)));
    opacity: 0;
  }
}
</style>