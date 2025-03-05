<template>
  <Teleport to="body">
    <div v-if="show" class="tour-overlay" :class="{'active': show}" @click="handleOverlayClick">
      <div class="highlight-box" :style="highlightStyle"></div>
      
      <div class="tour-tooltip" :style="tooltipStyle">
        <div class="tooltip-header">
          <div class="step-indicator">{{ currentStep }}/{{ steps.length }}</div>
          <button class="close-button" @click="$emit('close')">
            <X class="w-4 h-4" />
          </button>
        </div>
        
        <div class="tooltip-content">
          <h3>{{ currentStepData.title }}</h3>
          <p>{{ currentStepData.content }}</p>
        </div>
        
        <div class="tooltip-footer">
          <button v-if="currentStep > 1" class="tour-btn prev" @click="prevStep">이전</button>
          <button 
            v-if="currentStep < steps.length" 
            class="tour-btn next" 
            @click="nextStep"
          >
            다음
          </button>
          <button v-else class="tour-btn finish" @click="$emit('close')">완료</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { X } from 'lucide-vue-next'

interface TourStep {
  target: string // CSS selector for the target element
  title: string
  content: string
  position?: 'top' | 'right' | 'bottom' | 'left' // Position of the tooltip relative to the target
  margin?: number // Extra margin around the target element
}

const props = defineProps<{
  steps: TourStep[]
  show: boolean
  startAt?: number
  closeOnOverlayClick?: boolean
}>()

const emit = defineEmits(['close', 'step-changed', 'tour-completed'])

const currentStep = ref(props.startAt || 1)
const tooltipPosition = ref({ top: 0, left: 0 })
const highlightPosition = ref({ top: 0, left: 0, width: 0, height: 0 })

const currentStepData = computed(() => {
  return props.steps[currentStep.value - 1] || { target: '', title: '', content: '' }
})

const highlightStyle = computed(() => {
  return {
    top: `${highlightPosition.value.top}px`,
    left: `${highlightPosition.value.left}px`,
    width: `${highlightPosition.value.width}px`,
    height: `${highlightPosition.value.height}px`
  }
})

const tooltipStyle = computed(() => {
  return {
    top: `${tooltipPosition.value.top}px`,
    left: `${tooltipPosition.value.left}px`
  }
})

const nextStep = () => {
  if (currentStep.value < props.steps.length) {
    currentStep.value++
    emit('step-changed', currentStep.value)
  } else {
    emit('tour-completed')
  }
}

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
    emit('step-changed', currentStep.value)
  }
}

const handleOverlayClick = (event: MouseEvent) => {
  // Only close if clicking directly on the overlay (not child elements)
  if (props.closeOnOverlayClick && event.target === event.currentTarget) {
    emit('close')
  }
}

const positionTooltip = () => {
  // 스크롤 위치를 조정한 후 지연 시간을 추가하는 함수
  const scrollWithDelay = async (scrollTop) => {
    window.scrollTo({ top: scrollTop, behavior: 'smooth' });

    // 스크롤 완료를 기다리기 위한 지연
    return new Promise(resolve => {
      setTimeout(() => {
        resolve();
      }, 500); // 0.5초 지연 - 모바일 스크롤에 충분한 시간
    });
  };

  const targetElement = document.querySelector(currentStepData.value.target);

  if (!targetElement) {
    return;
  }

  // 먼저 요소가 뷰포트에 보이도록 스크롤
  const targetRect = targetElement.getBoundingClientRect();
  const buffer = 150;
  const elementTop = targetRect.top + window.scrollY;
  const elementBottom = targetRect.bottom + window.scrollY;
  const viewportTop = window.scrollY;
  const viewportBottom = window.scrollY + window.innerHeight;

  // 스크롤이 필요한지 확인하고 실행
  let scrollNeeded = false;
  // if (elementTop < viewportTop + buffer) {
  //   scrollNeeded = true;
  //   scrollWithDelay(Math.max(0, elementTop - buffer)).then(() => {
  //     // 스크롤 후 위치 다시 계산
  //     setTimeout(calculatePositions, 100);
  //   });
  // } else if (elementBottom > viewportBottom - buffer) {
  //   scrollNeeded = true;
  //   scrollWithDelay(elementBottom - window.innerHeight + buffer).then(() => {
  //     // 스크롤 후 위치 다시 계산
  //     setTimeout(calculatePositions, 100);
  //   });
  // }

  // 스크롤이 필요하지 않으면 바로 위치 계산
  if (!scrollNeeded) {
    calculatePositions();
  }

  // 위치 계산 함수
  function calculatePositions() {
    // 스크롤 후 DOM이 업데이트 되었을 수 있으므로 요소 다시 찾기
    const updatedElement = document.querySelector(currentStepData.value.target);
    if (!updatedElement) return;

    const updatedRect = updatedElement.getBoundingClientRect();
    const margin = currentStepData.value.margin || 10;

    // 하이라이트 박스 위치 지정
    highlightPosition.value = {
      top: updatedRect.top - margin + window.scrollY,
      left: updatedRect.left - margin + window.scrollX,
      width: updatedRect.width + margin * 2,
      height: updatedRect.height + margin * 2
    };

    // 툴팁 차원 (근사값)
    const tooltipWidth = 280;
    const tooltipHeight = 180;
    const defaultPosition = currentStepData.value.position || 'bottom';

    // 디바이스 유형 가져오기
    const isMobile = window.innerWidth < 768;

    // 지정된 위치와 디바이스 유형에 따라 툴팁 배치
    let position = defaultPosition;
    let top, left;

    // 모바일의 경우 요소를 가리지 않도록 왼쪽/오른쪽 위치를 선호
    if (isMobile && (defaultPosition === 'top' || defaultPosition === 'bottom')) {
      // 양쪽에 충분한 공간이 있는지 확인
      if (updatedRect.left > tooltipWidth + 40) {
        position = 'left';
      } else if (window.innerWidth - updatedRect.right > tooltipWidth + 40) {
        position = 'right';
      }
      // 양쪽에 충분한 공간이 없는 경우 원래 위치를 유지하되 나중에 조정
    }

    // 결정된 위치에 따라 초기 위치 계산
    switch (position) {
      case 'top':
        top = updatedRect.top - tooltipHeight - 20 + window.scrollY;
        left = updatedRect.left + (updatedRect.width / 2) - (tooltipWidth / 2) + window.scrollX;
        break;
      case 'right':
        top = updatedRect.top + (updatedRect.height / 2) - (tooltipHeight / 2) + window.scrollY;
        left = updatedRect.right + 20 + window.scrollX;
        break;
      case 'left':
        top = updatedRect.top + (updatedRect.height / 2) - (tooltipHeight / 2) + window.scrollY;
        left = updatedRect.left - tooltipWidth - 20 + window.scrollX;
        break;
      case 'bottom':
      default:
        top = updatedRect.bottom + 20 + window.scrollY;
        left = updatedRect.left + (updatedRect.width / 2) - (tooltipWidth / 2) + window.scrollX;
    }

    // 툴팁이 뷰포트 내에 머무르도록 함
    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;

    // 필요한 경우 가로 위치 조정
    if (left < 20) left = 20;
    if (left + tooltipWidth > viewportWidth - 20) left = viewportWidth - tooltipWidth - 20;

    // 필요한 경우 세로 위치 조정
    if (top < 20) top = 20;

    // 툴팁이 뷰포트 아래로 내려갈 경우 다른 위치 시도
    if (top + tooltipHeight > viewportHeight + window.scrollY - 20) {
      if (position === 'bottom') {
        // 아래쪽이 맞지 않으면 위쪽 시도
        const topPosition = updatedRect.top - tooltipHeight - 20 + window.scrollY;
        if (topPosition > 20) {
          // 위쪽 위치가 가능하면 사용
          top = topPosition;
        } else {
          // 위쪽이나 아래쪽이 잘 맞지 않으면 왼쪽이나 오른쪽 시도
          if (updatedRect.left > tooltipWidth + 40) {
            // 왼쪽 위치 사용
            top = updatedRect.top + (updatedRect.height / 2) - (tooltipHeight / 2) + window.scrollY;
            left = updatedRect.left - tooltipWidth - 20 + window.scrollX;
          } else if (viewportWidth - updatedRect.right > tooltipWidth + 40) {
            // 오른쪽 위치 사용
            top = updatedRect.top + (updatedRect.height / 2) - (tooltipHeight / 2) + window.scrollY;
            left = updatedRect.right + 20 + window.scrollX;
          }
          // 모든 것이 실패하면 화면에 있도록만 확인
          if (top + tooltipHeight > viewportHeight + window.scrollY - 20) {
            top = viewportHeight + window.scrollY - tooltipHeight - 20;
          }
        }
      } else if (position === 'left' || position === 'right') {
        // 왼쪽/오른쪽 툴팁의 세로 정렬 조정
        top = Math.min(top, viewportHeight + window.scrollY - tooltipHeight - 20);
      }
    }

    if (window.innerWidth <= 480 && position === 'top') {
      // 모든 툴팁을 화면 상단에 고정
      top = 200 + window.scrollY;
      left = (viewportWidth - tooltipWidth) / 2; // 화면 중앙
    }

    // 최종 위치
    tooltipPosition.value = { top, left };
  }
};

const updatePosition = () => {
  // 모바일에서 더 긴 지연 추가
  const isMobile = window.innerWidth < 640;
  const delay = isMobile ? 100 : 100;

  nextTick(() => {
    // 레이아웃이 안정화 될 때까지 짧은 지연
    setTimeout(() => {
      positionTooltip();
    }, delay);
  });
};

watch(() => props.show, (newVal) => {
  if (newVal) {
    updatePosition()
  }
})

watch(() => currentStep.value, () => {
  updatePosition()
})

onMounted(() => {
  if (props.show) {
    updatePosition()
  }
  
  window.addEventListener('resize', updatePosition)
  window.addEventListener('scroll', updatePosition)
})

// Remove event listeners on unmount
defineExpose({
  nextStep,
  prevStep,
  currentStep
})
</script>

<style scoped>
.tour-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  background-color: rgba(0, 0, 0, 0.1);
  pointer-events: all;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.tour-overlay.active {
  opacity: 1;
}

.highlight-box {
  position: absolute;
  box-shadow: none;
  border-radius: 4px;
  z-index: 10000;
  background-color: transparent;
  border: 2px solid #2979FF;
  transition: all 0.3s ease;
  animation: highlight-pulse 2s infinite alternate;
  -webkit-mask: none;
  mask: none;
  /* 오버레이에서 해당 부분을 잘라내는 효과 */
  filter: drop-shadow(0 0 0 transparent);
}

@keyframes highlight-pulse {
  0% {
    border-color: rgba(41, 121, 255, 0.7);
    box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.75), 0 0 0 2px rgba(41, 121, 255, 0.2);
  }
  100% {
    border-color: rgba(41, 121, 255, 1);
    box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.75), 0 0 0 4px rgba(41, 121, 255, 0.4);
  }
}

.tour-tooltip {
  position: absolute;
  width: 280px;
  background-color: #141414;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5), 0 0 0 2px rgba(41, 121, 255, 0.3);
  z-index: 10001;
  color: white;
  transition: all 0.3s ease;
  overflow: hidden;
  animation: pulse 1.5s infinite alternate;
}

@keyframes pulse {
  0% {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5), 0 0 0 2px rgba(41, 121, 255, 0.2);
  }
  100% {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5), 0 0 0 4px rgba(41, 121, 255, 0.4);
  }
}

/* 툴팁 전체 크기를 모바일에서 더 작게 조정 */
@media (max-width: 640px) {
  .tour-tooltip {
    width: min(260px, calc(100vw - 40px));
    font-size: 0.9rem; /* 전체 폰트 크기 약간 줄임 */
  }

  .tooltip-header {
    padding: 10px 12px; /* 헤더 패딩 줄임 */
  }

  .step-indicator {
    font-size: 11px; /* 단계 표시 텍스트 크기 줄임 */
  }

  .close-button {
    padding: 3px; /* 닫기 버튼 패딩 줄임 */
  }

  .tooltip-content {
    padding: 12px; /* 내용 패딩 줄임 */
  }

  .tooltip-content h3 {
    font-size: 15px; /* 제목 크기 줄임 */
    margin-bottom: 6px;
  }

  .tooltip-content p {
    font-size: 13px; /* 내용 텍스트 크기 줄임 */
    line-height: 1.5;
  }

  .tooltip-footer {
    padding: 10px 12px; /* 푸터 패딩 줄임 */
  }

  .tour-btn {
    padding: 6px 12px; /* 버튼 패딩 줄임 */
    min-width: 70px; /* 버튼 최소 너비 줄임 */
    font-size: 12px; /* 버튼 텍스트 크기 줄임 */
  }

  /* 하이라이트 박스 테두리 두께 줄임 */
  .highlight-box {
    border-width: 1.5px;
  }

  /* 더 작은 모바일 화면에서 추가 조정 */
  @media (max-width: 375px) {
    .tour-tooltip {
      width: min(240px, calc(100vw - 30px));
      font-size: 0.85rem;
    }

    .tooltip-content h3 {
      font-size: 14px;
    }

    .tooltip-content p {
      font-size: 12px;
      line-height: 1.4;
    }

    .tour-btn {
      padding: 5px 10px;
      min-width: 60px;
      font-size: 11px;
    }
  }

  @keyframes highlight-pulse {
    0% {
      border-color: rgba(41, 121, 255, 0.7);
      box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.65), 0 0 0 1.5px rgba(41, 121, 255, 0.2);
    }
    100% {
      border-color: rgba(41, 121, 255, 1);
      box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.65), 0 0 0 3px rgba(41, 121, 255, 0.4);
    }
  }
}

.tooltip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.step-indicator {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.close-button {
  background: none;
  border: none;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  border-radius: 4px;
}

.close-button:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}

.tooltip-content {
  padding: 16px;
}

.tooltip-content h3 {
  margin-top: 0;
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #2979FF;
}

.tooltip-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.9);
}

.tooltip-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.tour-btn {
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 80px;
  text-align: center;
}

.tour-btn.prev {
  background-color: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
}

.tour-btn.prev:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
}

.tour-btn.next, .tour-btn.finish {
  background-color: #2979FF;
  border: none;
  color: white;
  position: relative;
  overflow: hidden;
}

.tour-btn.next:hover, .tour-btn.finish:hover {
  background-color: #1A67E0;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(41, 121, 255, 0.4);
}

.tour-btn.finish {
  background-color: #4CAF50;
}

.tour-btn.finish:hover {
  background-color: #3d8b40;
}

@media (max-width: 640px) {
  .tour-btn {
    padding: 10px 16px;
    min-width: 90px;
  }
  
  .tooltip-footer {
    padding: 16px;
  }
}
</style>