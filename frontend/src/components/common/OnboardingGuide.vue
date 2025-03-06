<template>
  <div 
    v-if="onboardingStore.isOnboardingActive && currentContent"
    class="onboarding-overlay" 
    @click.self="handleOverlayClick"
  >
    <div ref="tooltipRef" class="onboarding-tooltip" :style="positionStyle">
      <div class="tooltip-header">
        <span class="step-indicator">{{ onboardingStore.currentStep + 1 }}/{{ onboardingStore.totalSteps }}</span>
        <button @click="onboardingStore.toggleOnboarding(false)" class="close-button">
          <XIcon size="16" />
        </button>
      </div>
      
      <div class="tooltip-content">
        <h3 class="tooltip-title">{{ currentContent.title }}</h3>
        <p class="tooltip-description">{{ currentContent.description }}</p>
        
        <!-- 데이터가 없는 경우 추가 설명 -->
        <p v-if="currentContent.emptyStateDescription" class="empty-state-description">
          {{ currentContent.emptyStateDescription }}
        </p>
        
        <!-- 예시 이미지 표시 (데이터가 없을 때) -->
        <img 
          v-if="currentContent.exampleImage" 
          :src="currentContent.exampleImage" 
          alt="예시 이미지" 
          class="example-image"
        />
      </div>
      
      <div class="tooltip-footer">
        <button 
          v-if="onboardingStore.currentStep > 0" 
          @click="onboardingStore.prevStep" 
          class="prev-button"
        >
          이전
        </button>
        <button 
          @click="onboardingStore.nextStep" 
          class="next-button"
        >
          {{ onboardingStore.currentStep < onboardingStore.totalSteps - 1 ? '다음' : '완료' }}
        </button>
      </div>
      
      <div class="tooltip-arrow" :style="arrowStyle"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, watch } from 'vue'
import { XIcon } from 'lucide-vue-next'
import { useOnboardingStore } from '@/stores/onboarding'

// 타겟 요소의 셀렉터와 온보딩 진행 방향
interface Props {
  targetSelectors: string[]  // 각 단계별 타겟 요소 셀렉터
  allowOverlayClose?: boolean // 오버레이 클릭으로 닫기 허용 여부
}

const props = withDefaults(defineProps<Props>(), {
  allowOverlayClose: true
})

const onboardingStore = useOnboardingStore()
const tooltipRef = ref<HTMLElement | null>(null)
const positionStyle = ref({})
const arrowStyle = ref({})

// 온보딩 내용 정의
const onboardingContents = [
  {
    title: '프로필 정보',
    description: '여기서 소환사의 기본 정보와 평가 점수를 확인할 수 있어요.',
    emptyStateDescription: null,
    exampleImage: null
  },
  {
    title: '통계 정보',
    description: '소환사가 받은 태그와 최근 평가를 확인할 수 있어요.',
    emptyStateDescription: '아직 받은 평가가 없어요. 게임을 더 플레이하고 평가를 받으면 여기에 표시됩니다.',
    exampleImage: '/images/onboarding/stats_example.png'
  },
  {
    title: '티어 정보',
    description: '소환사의 솔로랭크와 자유랭크 티어 정보를 확인할 수 있어요.',
    emptyStateDescription: null,
    exampleImage: null
  },
  {
    title: '매치 기록',
    description: '소환사의 최근 게임 기록을 확인할 수 있어요.',
    emptyStateDescription: '아직 매치 기록이 없거나 불러오지 못했어요. 전적을 갱신하거나 게임을 더 플레이하면 여기에 표시됩니다.',
    exampleImage: '/images/onboarding/match_example.png'
  },
  {
    title: '리뷰 작성하기',
    description: '소환사에 대한 리뷰를 작성하고 평가할 수 있어요.',
    emptyStateDescription: null,
    exampleImage: null
  }
]

// 현재 단계의 온보딩 내용
const currentContent = computed(() => {
  return onboardingContents[onboardingStore.currentStep]
})

// 오버레이 클릭 처리
const handleOverlayClick = () => {
  if (props.allowOverlayClose) {
    onboardingStore.toggleOnboarding(false)
  }
}

// 툴팁 위치 계산 및 설정
const updateTooltipPosition = async () => {
  await nextTick()
  
  const targetSelector = props.targetSelectors[onboardingStore.currentStep]
  if (!targetSelector) return
  
  const targetElement = document.querySelector(targetSelector) as HTMLElement
  if (!targetElement || !tooltipRef.value) return
  
  const targetRect = targetElement.getBoundingClientRect()
  const tooltipRect = tooltipRef.value.getBoundingClientRect()
  
  // 화면 크기
  const viewportWidth = window.innerWidth
  const viewportHeight = window.innerHeight
  
  // 기본 위치 (타겟 아래)
  let top = targetRect.bottom + 15
  let left = targetRect.left + (targetRect.width / 2) - (tooltipRect.width / 2)
  let arrowPosition = '0'
  
  // 툴팁이 화면 하단을 벗어나는 경우 위로 배치
  if (top + tooltipRect.height > viewportHeight) {
    top = targetRect.top - tooltipRect.height - 15
    arrowStyle.value = {
      top: '100%',
      bottom: 'auto',
      transform: 'translate(-50%, 0) rotate(45deg)'
    }
  } else {
    arrowStyle.value = {
      top: 'auto',
      bottom: '100%',
      transform: 'translate(-50%, 0) rotate(45deg)'
    }
  }
  
  // 툴팁이 화면 왼쪽을 벗어나는 경우
  if (left < 10) {
    arrowPosition = `${targetRect.left + targetRect.width/2 - left - 10}px`
    left = 10
  } 
  // 툴팁이 화면 오른쪽을 벗어나는 경우
  else if (left + tooltipRect.width > viewportWidth - 10) {
    arrowPosition = `${targetRect.left + targetRect.width/2 - (viewportWidth - tooltipRect.width - 10)}px`
    left = viewportWidth - tooltipRect.width - 10
  } else {
    arrowPosition = '50%'
  }
  
  // 화살표 위치 설정
  arrowStyle.value = {
    ...arrowStyle.value,
    left: arrowPosition
  }
  
  // 툴팁 위치 설정
  positionStyle.value = {
    top: `${top}px`,
    left: `${left}px`
  }
}

// 단계가 변경될 때마다 위치 업데이트
watch(() => onboardingStore.currentStep, updateTooltipPosition)

// 온보딩 모드가 활성화될 때마다 위치 업데이트
watch(() => onboardingStore.isOnboardingActive, (newVal) => {
  if (newVal) {
    nextTick(updateTooltipPosition)
  }
})

// 창 크기가 변경될 때 위치 업데이트
onMounted(() => {
  window.addEventListener('resize', updateTooltipPosition)
  if (onboardingStore.isOnboardingActive) {
    updateTooltipPosition()
  }
})
</script>

<style scoped>
.onboarding-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 9999;
  backdrop-filter: blur(2px);
}

.onboarding-tooltip {
  position: absolute;
  background: #222222;
  border-radius: 8px;
  padding: 16px;
  width: 320px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  z-index: 10000;
  border: 1px solid #444;
}

.tooltip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.step-indicator {
  font-size: 12px;
  color: #999;
}

.close-button {
  background: transparent;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 4px;
}

.close-button:hover {
  color: #fff;
}

.tooltip-content {
  margin-bottom: 16px;
}

.tooltip-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 8px;
}

.tooltip-description {
  font-size: 14px;
  color: #ccc;
  line-height: 1.5;
}

.empty-state-description {
  margin-top: 12px;
  font-size: 13px;
  color: #999;
  padding: 8px;
  background-color: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  font-style: italic;
}

.example-image {
  margin-top: 12px;
  width: 100%;
  border-radius: 4px;
  border: 1px solid #444;
}

.tooltip-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.prev-button, .next-button {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.prev-button {
  background: transparent;
  border: 1px solid #666;
  color: #ccc;
}

.prev-button:hover {
  background: rgba(255, 255, 255, 0.05);
}

.next-button {
  background: #2979FF;
  border: none;
  color: white;
}

.next-button:hover {
  background: #1c68e3;
}

.tooltip-arrow {
  position: absolute;
  width: 12px;
  height: 12px;
  background: #222222;
  border: 1px solid #444;
}

@media (max-width: 480px) {
  .onboarding-tooltip {
    width: 90%;
    max-width: 320px;
  }
}
</style>