<!-- PartyCommunity-backup.vue -->
<template>
  <div class="min-h-screen bg-[#0A0A0A] p-4 sm:p-6">
    <!-- 듀오 등록 모달 -->
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
      <PartyFilter
          :initial-game-mode="selectedGameMode"
          :initial-position="selectedPosition"
          :initial-tier="selectedTier"
          @filter-update="onFilterUpdate"
          @write="showWriteModal = true"
      />

      <!-- 내 파티 미니바 -->
      <MyActiveParty
          v-if="myActiveParty"
          :party="myActiveParty"
          :my-puuid="myPuuid"
          :has-new-applicant="hasNewApplicant"
          :participant-count="participantCount"
          @update-status="handleUpdatePartyStatus"
          @approve-applicant="handleApprove"
          @reject-applicant="handleReject"
          @leave-party="handleLeaveParty"
      />

      <!-- 듀오 카드 그리드 -->
      <PartyGrid
          :cards="postCards"
          :my-puuid="myPuuid"
          :applied-positions="appliedPositions"
          :is-loading="isLoading"
          :show-read-more="showReadMore"
          @apply="applyForPosition"
          @load-more="onLoadMore"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { RefreshCcw } from 'lucide-vue-next'
import PartyFilter from '@/components/community/party/PartyFilter.vue'
import MyActiveParty from '@/components/community/party/MyActiveParty.vue'
import PartyGrid from '@/components/community/party/PartyGrid.vue'
import WriteModal from '@/components/community/party/WriteModal.vue'
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

const toast = useToast()
const authStore = useAuthStore()
const myPuuid = computed(() => authStore.user?.puuid)
const myActiveParty = ref<MyPartyPostDto | null>(null)
const hasNewApplicant = ref(false)
const participantCount = ref<number | null>(null)
const myApplicationsStatus = ref<Map<string, string>>(new Map()); // key: `${postId}-${position}`, value: 'PENDING', 'APPROVED' 등
const previousApplicationsStatus = ref<Map<string, string>>(new Map());

onMounted(async () => {
  setupVisibilityHandler()
  await fetchPosts({})
  await fetchAppliedPositions()
  await fetchMyPartyPost()
  await fetchMyApplicationStatus();
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

// 내 파티 생성/참가 감지
watch(() => myActiveParty.value, (newVal, oldVal) => {
  if (!oldVal && newVal) {
    hasNewApplicant.value = true
    setTimeout(() => hasNewApplicant.value = false, 2000) // 2초 후 효과 제거
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

  try {
    const response = await communityApi.getMyApplyList();

    console.log(response.data)

    // 이전 상태 저장
    previousApplicationsStatus.value = new Map(myApplicationsStatus.value);

    // 새로운 상태 맵 생성
    const newStatusMap = new Map();

    if (response.data && Array.isArray(response.data)) {
      response.data.forEach((application: PartyCommunityApplicantDto) => {
        const key = `${application.postId}-${application.position}`;
        newStatusMap.set(key, application.applyStatus || 'PENDING');
      });
    }

    // 상태 변경 확인 및 토스트 메시지 표시
    checkApplicationStatusChanges(previousApplicationsStatus.value, newStatusMap);

    // 새 상태로 업데이트
    myApplicationsStatus.value = newStatusMap;
  } catch (error) {
    console.error('내 파티 신청 상태 조회 중 오류:', error);
  }
};

// 상태 변경 감지 및 토스트 메시지 표시 함수
const checkApplicationStatusChanges = (prevStatusMap: Map<string, string>, newStatusMap: Map<string, string>) => {
  // 각 신청에 대해 상태 변경 확인
  newStatusMap.forEach((newStatus, key) => {
    const prevStatus = prevStatusMap.get(key);

    // 상태가 변경된 경우 (이전에는 'PENDING'이었고 이제는 다른 상태인 경우)
    if (prevStatus === 'PENDING' && newStatus !== 'PENDING') {
      const [postIdStr, position] = key.split('-');
      const postId = parseInt(postIdStr);

      // 해당 파티 찾기
      const party = postCards.value.find(card => card.postId === postId);

      if (party) {
        if (newStatus === 'APPROVE') {
          toast.success('파티에 가입되었습니다.');
        } else if (newStatus === 'REJECT') {
          toast.error('파티 가입 신청이 거절되었습니다.');
        } else if (newStatus === 'CANCEL') {
          toast.info(`파티 가입 신청이 취소되었습니다.`);
        }
      }
    }
  });

  // 목록에서 사라진 신청 확인 (거절된 경우)
  prevStatusMap.forEach((prevStatus, key) => {
    if (!newStatusMap.has(key) && prevStatus === 'PENDING') {
      // 신청이 목록에서 사라졌으면 거절된 것으로 간주
      const [postIdStr, position] = key.split('-');
      const postId = parseInt(postIdStr);

      const party = postCards.value.find(card => card.postId === postId);

      if (party) {
        toast.error(`파티 가입 신청이 거절되었습니다.`);
      }
    }
  });
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
    await fetchMyPartyPost()
    await fetchMyApplicationStatus();
  } catch (error) {
    console.error('업데이트 체크 중 오류:', error)
  } finally {
    isPolling.value = false
  }
}

const applyForPosition = async (postId: number, position: string) => {
  await communityApi.applyParty(postId, position)
  appliedPositions.value.set(`${postId}-${position}`, true)
  toast.success("참가신청이 완료되었습니다.")
  // 신청 상태 추가
  myApplicationsStatus.value.set(`${postId}-${position}`, 'PENDING');
  await checkUpdates()
}

const handleLeaveParty = async (postId: number) => {
  await communityApi.leaveMyParty(postId)
  toast.success('파티 탈퇴되었습니다.')
  await checkUpdates()
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
</style>