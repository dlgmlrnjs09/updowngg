<template>
  <div v-if="onboardingStore.isOnboardingActive">
    <!-- 현재 단계의 가이드 모달 -->
    <div ref="tooltipRef" class="onboarding-tooltip" :style="positionStyle">
      <div class="tooltip-header">
        <span class="step-indicator">{{ onboardingStore.currentStep + 1 }}/{{ onboardingStore.totalSteps }}</span>
        <button @click="onboardingStore.completeOnboarding()" class="close-button">
          <XIcon size="14" />
        </button>
      </div>
      
      <div class="tooltip-content">
        <p class="tooltip-description">{{ currentContent.description }}</p>
      </div>
      
      <div class="tooltip-footer">
        <button 
          v-if="onboardingStore.currentStep > 0" 
          @click="handlePrev" 
          class="prev-button"
        >
          이전
        </button>
        <button 
          @click="handleNext" 
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
import { ref, computed, onMounted, watch, onUnmounted, nextTick } from 'vue'
import { XIcon } from 'lucide-vue-next'
import { useOnboardingStore } from '@/stores/onboarding'

// 타겟 요소의 셀렉터
interface Props {
  targetSelectors: string[]  // 각 단계별 타겟 요소 셀렉터
}

const props = defineProps<Props>()
const onboardingStore = useOnboardingStore()
const tooltipRef = ref<HTMLElement | null>(null)
const positionStyle = ref({})
const arrowStyle = ref({})
const currentHighlightedElement = ref<HTMLElement | null>(null)

// 온보딩 내용 정의 - 간결하게 유지
const onboardingContents = [
  { description: '소환사의 기본 정보와 평가 점수를 확인할 수 있어요.' },
  { description: '받은 태그와 평가를 확인할 수 있어요.' },
  { description: '솔로랭크와 자유랭크 티어 정보를 확인할 수 있어요.' },
  { description: '최근 게임 기록을 확인할 수 있어요.' },
  { description: '소환사에 대한 리뷰를 작성하고 평가할 수 있어요.' }
]

// 현재 단계의 온보딩 내용
const currentContent = computed(() => {
  return onboardingContents[onboardingStore.currentStep]
})

// 이전 버튼 처리
const handlePrev = () => {
  removeHighlight()
  cleanupVirtualElement()
  onboardingStore.prevStep()
}

// 다음 버튼 처리
const handleNext = () => {
  removeHighlight()
  cleanupVirtualElement()
  onboardingStore.nextStep()
}

// 강조 효과 제거
const removeHighlight = () => {
  if (currentHighlightedElement.value) {
    currentHighlightedElement.value.style.removeProperty('outline')
    currentHighlightedElement.value.style.removeProperty('box-shadow')
    currentHighlightedElement.value.style.removeProperty('transition')
    currentHighlightedElement.value = null
  }
}

// 가상 요소 정리
const cleanupVirtualElement = () => {
  const virtualElement = document.getElementById('virtual-onboarding-element');
  if (virtualElement) {
    virtualElement.remove();
  }
}

// 요소 강조
const highlightElement = (element: HTMLElement) => {
  if (element) {
    // 이전 강조 효과 제거
    removeHighlight()
    
    // 새로운 요소 강조
    element.style.outline = '2px solid #2979FF'
    element.style.boxShadow = '0 0 0 2px rgba(41, 121, 255, 0.3)'
    element.style.transition = 'outline 0.3s, box-shadow 0.3s'
    
    currentHighlightedElement.value = element
  }
}

// 요소가 화면에 보이는지 확인
const isElementInViewport = (element: HTMLElement) => {
  const rect = element.getBoundingClientRect();
  return (
    rect.top >= 0 &&
    rect.left >= 0 &&
    rect.bottom <= window.innerHeight &&
    rect.right <= window.innerWidth
  );
};

// 요소로 스크롤
const scrollToElement = (element: HTMLElement) => {
  // 요소가 화면에 보이지 않으면 스크롤
  if (!isElementInViewport(element)) {
    // 가장 가까운 스크롤 가능한 부모 요소를 찾기
    let scrollParent = element.parentElement;
    while (scrollParent && getComputedStyle(scrollParent).overflow === 'visible') {
      scrollParent = scrollParent.parentElement;
    }
    
    // 스크롤 가능한 부모가 없으면 window로 스크롤
    if (!scrollParent || scrollParent === document.documentElement) {
      element.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
      });
    } else {
      // 부모 요소 내에서 스크롤
      const parentRect = scrollParent.getBoundingClientRect();
      const elementRect = element.getBoundingClientRect();
      const scrollTo = elementRect.top - parentRect.top - (parentRect.height / 2) + (elementRect.height / 2);
      
      scrollParent.scrollBy({
        top: scrollTo,
        behavior: 'smooth'
      });
    }
    
    // 스크롤 완료까지 잠시 대기 (애니메이션 시간 고려)
    return new Promise(resolve => setTimeout(resolve, 500));
  }
  
  return Promise.resolve();
};

// 가상 요소 생성 및 추가
const createVirtualElement = () => {
  // 이미 생성된 가상 요소가 있으면 제거
  const existingVirtual = document.getElementById('virtual-onboarding-element');
  if (existingVirtual) {
    existingVirtual.remove();
  }
  
  // 새 가상 요소 생성
  const virtualElement = document.createElement('div');
  virtualElement.id = 'virtual-onboarding-element';
  virtualElement.style.position = 'relative';
  virtualElement.style.width = '280px';
  virtualElement.style.height = '100px';
  virtualElement.style.margin = '40px auto';
  virtualElement.style.backgroundColor = 'rgba(41, 121, 255, 0.1)';
  virtualElement.style.borderRadius = '8px';
  virtualElement.style.display = 'flex';
  virtualElement.style.alignItems = 'center';
  virtualElement.style.justifyContent = 'center';
  virtualElement.style.color = '#2979FF';
  virtualElement.style.fontSize = '14px';
  virtualElement.style.fontWeight = '500';
  virtualElement.style.zIndex = '9998';
  
  // 현재 단계에 맞는 텍스트 추가
  const stepTexts = [
    '프로필 정보 영역',
    '통계 정보 영역',
    '티어 정보 영역',
    '게임 기록 영역',
    '리뷰 영역'
  ];
  virtualElement.textContent = stepTexts[onboardingStore.currentStep] || '안내 영역';
  
  // 페이지의 적절한 위치에 요소 추가
  // 컨테이너를 찾아보고 없으면 body에 직접 추가
  const container = document.querySelector('.container') || document.body;
  container.appendChild(virtualElement);
  
  return virtualElement;
};

// 툴팁 위치 계산 및 설정
const updateTooltipPosition = async () => {
  await nextTick();
  
  // 현재 단계에 해당하는 셀렉터 가져오기
  const targetSelector = props.targetSelectors[onboardingStore.currentStep];
  if (!targetSelector) return;
  
  // 해당 셀렉터에 맞는 요소 찾기
  let targetElement = document.querySelector(targetSelector) as HTMLElement;
  let isVirtualElement = false;
  
  // 요소가 없으면 가상 요소 생성
  if (!targetElement) {
    targetElement = createVirtualElement();
    isVirtualElement = true;
  }
  
  if (!targetElement || !tooltipRef.value) return;
  
  // 요소가 화면에 보이도록 스크롤
  await scrollToElement(targetElement);
  
  // 요소 강조 표시 (가상 요소가 아닌 경우에만)
  if (!isVirtualElement) {
    highlightElement(targetElement);
  }
  
  // 요소의 위치 가져오기
  const targetRect = targetElement.getBoundingClientRect();
  const tooltipRect = tooltipRef.value.getBoundingClientRect();
  
  // 화면 크기
  const viewportWidth = window.innerWidth;
  const viewportHeight = window.innerHeight;
  
  // 기본 위치 계산 (모바일에서는 상단에, 데스크탑에서는 요소 옆에 표시)
  let top = 0;
  let left = 0;
  
  if (viewportWidth < 768) {
    // 모바일: 화면 상단에 고정
    top = Math.max(10, targetRect.top - tooltipRect.height - 10);
    left = (viewportWidth - tooltipRect.width) / 2;
    
    // 모바일에서 요소가 화면 상단에 있으면 아래쪽에 표시
    if (top < 10) {
      top = Math.min(targetRect.bottom + 10, viewportHeight - tooltipRect.height - 10);
    }
    
    // 화살표 위치 설정
    arrowStyle.value = {
      left: `${targetRect.left + targetRect.width/2 - left}px`,
      top: top === Math.max(10, targetRect.top - tooltipRect.height - 10) ? '100%' : 'auto',
      bottom: top !== Math.max(10, targetRect.top - tooltipRect.height - 10) ? '100%' : 'auto',
      transform: 'translate(-50%, 0) rotate(45deg)'
    };
  } else {
    // 데스크탑: 요소 우측에 표시
    top = targetRect.top + (targetRect.height / 2) - (tooltipRect.height / 2);
    left = targetRect.right + 15;
    
    // 우측 공간이 부족하면 좌측에 표시
    if (left + tooltipRect.width > viewportWidth - 10) {
      left = targetRect.left - tooltipRect.width - 15;
      
      // 좌측 공간도 부족하면 상단이나 하단에 표시
      if (left < 10) {
        left = targetRect.left + (targetRect.width / 2) - (tooltipRect.width / 2);
        
        if (targetRect.top > tooltipRect.height + 15) {
          // 상단에 표시
          top = targetRect.top - tooltipRect.height - 15;
          arrowStyle.value = {
            bottom: '-6px',
            top: 'auto',
            left: '50%',
            transform: 'translate(-50%, 0) rotate(45deg)'
          };
        } else {
          // 하단에 표시
          top = targetRect.bottom + 15;
          arrowStyle.value = {
            top: '-6px',
            bottom: 'auto',
            left: '50%',
            transform: 'translate(-50%, 0) rotate(45deg)'
          };
        }
      } else {
        // 좌측에 표시
        arrowStyle.value = {
          right: '-6px',
          left: 'auto',
          top: '50%',
          transform: 'translate(0, -50%) rotate(45deg)'
        };
      }
    } else {
      // 우측에 표시
      arrowStyle.value = {
        left: '-6px',
        right: 'auto',
        top: '50%',
        transform: 'translate(0, -50%) rotate(45deg)'
      };
    }
  }
  
  // 최종 위치 설정
  positionStyle.value = {
    top: `${Math.max(10, Math.min(viewportHeight - tooltipRect.height - 10, top))}px`,
    left: `${Math.max(10, Math.min(viewportWidth - tooltipRect.width - 10, left))}px`
  };
}

// 단계가 변경될 때마다 위치 업데이트
watch(() => onboardingStore.currentStep, updateTooltipPosition)

// 온보딩 모드가 활성화될 때마다 위치 업데이트
watch(() => onboardingStore.isOnboardingActive, (newVal) => {
  if (newVal) {
    nextTick(updateTooltipPosition)
  } else {
    // 온보딩 종료 시 모든 요소 정리
    removeHighlight()
    cleanupVirtualElement()
  }
})

// 창 크기가 변경될 때 위치 업데이트
onMounted(() => {
  window.addEventListener('resize', updateTooltipPosition)
  if (onboardingStore.isOnboardingActive) {
    nextTick(updateTooltipPosition)
  }
})

// 컴포넌트 언마운트 시 이벤트 리스너 제거 및 모든 요소 정리
onUnmounted(() => {
  window.removeEventListener('resize', updateTooltipPosition)
  removeHighlight()
  cleanupVirtualElement()
})
</script>

<style scoped>
.onboarding-tooltip {
  position: fixed;
  background: #222222;
  border-radius: 6px;
  padding: 12px;
  width: 240px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  z-index: 10000;
  border: 1px solid #444;
}

.tooltip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.step-indicator {
  font-size: 11px;
  color: #999;
}

.close-button {
  background: transparent;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-button:hover {
  color: #fff;
}

.tooltip-content {
  margin-bottom: 12px;
}

.tooltip-description {
  font-size: 13px;
  color: #eee;
  line-height: 1.4;
}

.tooltip-footer {
  display: flex;
  justify-content: flex-end;
  gap: 6px;
}

.prev-button, .next-button {
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
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
  z-index: -1;
}

@media (max-width: 480px) {
  .onboarding-tooltip {
    width: 80%;
    max-width: 280px;
    padding: 10px;
  }
  
  .tooltip-description {
    font-size: 12px;
  }
  
  .prev-button, .next-button {
    padding: 4px 8px;
    font-size: 11px;
  }
}
</style>