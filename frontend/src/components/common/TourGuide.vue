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
    // 단계 변경 전에 잠시 딜레이를 주어 스크롤 및 요소 준비 시간 확보
    setTimeout(() => {
      currentStep.value++
      emit('step-changed', currentStep.value)
      
      // 단계 변경 후 추가 딜레이로 화면이 준비될 시간 제공
      setTimeout(() => {
        positionTooltip()
      }, 300)
    }, 100)
  } else {
    emit('tour-completed')
  }
}

const prevStep = () => {
  if (currentStep.value > 1) {
    // 단계 변경 전에 잠시 딜레이를 주어 스크롤 및 요소 준비 시간 확보
    setTimeout(() => {
      currentStep.value--
      emit('step-changed', currentStep.value)
      
      // 단계 변경 후 추가 딜레이로 화면이 준비될 시간 제공
      setTimeout(() => {
        positionTooltip()
      }, 300)
    }, 100)
  }
}

const handleOverlayClick = (event: MouseEvent) => {
  // Only close if clicking directly on the overlay (not child elements)
  if (props.closeOnOverlayClick && event.target === event.currentTarget) {
    emit('close')
  }
}

const positionTooltip = () => {
  const targetElement = document.querySelector(currentStepData.value.target)
  
  if (!targetElement) {
    console.error(`Tour target element not found: ${currentStepData.value.target}`)
    return
  }
  
  const targetRect = targetElement.getBoundingClientRect()
  const margin = currentStepData.value.margin || 10
  
  // Position the highlight box
  highlightPosition.value = {
    top: targetRect.top - margin + window.scrollY,
    left: targetRect.left - margin + window.scrollX,
    width: targetRect.width + margin * 2,
    height: targetRect.height + margin * 2
  }
  
  // Tooltip dimensions (approximate)
  const tooltipWidth = 280
  const tooltipHeight = 180
  const defaultPosition = currentStepData.value.position || 'bottom'
  
  // Get device type
  const isMobile = window.innerWidth < 768
  
  // Position the tooltip based on specified position and device type
  let position = defaultPosition
  let top, left

  // For mobile, prefer left/right positioning to avoid covering the element
  if (isMobile && (defaultPosition === 'top' || defaultPosition === 'bottom')) {
    // Check if we have enough space on either side
    if (targetRect.left > tooltipWidth + 40) {
      position = 'left'
    } else if (window.innerWidth - targetRect.right > tooltipWidth + 40) {
      position = 'right'
    }
    // If neither side has enough space, keep the original position but adjust later
  }

  // Calculate initial position based on the determined position
  switch (position) {
    case 'top':
      top = targetRect.top - tooltipHeight - 20 + window.scrollY
      left = targetRect.left + (targetRect.width / 2) - (tooltipWidth / 2) + window.scrollX
      break
    case 'right':
      top = targetRect.top + (targetRect.height / 2) - (tooltipHeight / 2) + window.scrollY
      left = targetRect.right + 20 + window.scrollX
      break
    case 'left':
      top = targetRect.top + (targetRect.height / 2) - (tooltipHeight / 2) + window.scrollY
      left = targetRect.left - tooltipWidth - 20 + window.scrollX
      break
    case 'bottom':
    default:
      top = targetRect.bottom + 20 + window.scrollY
      left = targetRect.left + (targetRect.width / 2) - (tooltipWidth / 2) + window.scrollX
  }
  
  // Make sure tooltip stays within viewport
  const viewportWidth = window.innerWidth
  const viewportHeight = window.innerHeight
  
  // Adjust horizontal position if needed
  if (left < 20) left = 20
  if (left + tooltipWidth > viewportWidth - 20) left = viewportWidth - tooltipWidth - 20
  
  // Adjust vertical position if needed
  if (top < 20) top = 20
  
  // If the tooltip would go below viewport, try another position
  if (top + tooltipHeight > viewportHeight + window.scrollY - 20) {
    if (position === 'bottom') {
      // If bottom doesn't fit, try top
      const topPosition = targetRect.top - tooltipHeight - 20 + window.scrollY
      if (topPosition > 20) {
        // If top position is viable, use it
        top = topPosition
      } else {
        // If neither top nor bottom fits well, try left or right
        if (targetRect.left > tooltipWidth + 40) {
          // Use left position
          top = targetRect.top + (targetRect.height / 2) - (tooltipHeight / 2) + window.scrollY
          left = targetRect.left - tooltipWidth - 20 + window.scrollX
        } else if (viewportWidth - targetRect.right > tooltipWidth + 40) {
          // Use right position
          top = targetRect.top + (targetRect.height / 2) - (tooltipHeight / 2) + window.scrollY
          left = targetRect.right + 20 + window.scrollX
        }
        // If all else fails, just make sure it's on screen
        if (top + tooltipHeight > viewportHeight + window.scrollY - 20) {
          top = viewportHeight + window.scrollY - tooltipHeight - 20
        }
      }
    } else if (position === 'left' || position === 'right') {
      // Adjust vertical alignment for left/right tooltips
      top = Math.min(top, viewportHeight + window.scrollY - tooltipHeight - 20)
    }
  }
  
  // Final position
  tooltipPosition.value = { top, left }
  
  // 개선된 스크롤 로직: 대상 요소가 화면 중앙에 오도록 함
  const buffer = 200 // 더 큰 버퍼로 여유 공간 확보
  const elementTop = targetRect.top + window.scrollY
  const elementBottom = targetRect.bottom + window.scrollY
  const elementHeight = targetRect.height
  const viewportTop = window.scrollY
  const viewportHeight = window.innerHeight
  const viewportBottom = viewportTop + viewportHeight
  
  // 요소의 중앙 위치 계산
  const elementCenter = elementTop + (elementHeight / 2)
  // 화면 중앙으로 스크롤할 위치 계산
  const targetScrollPosition = elementCenter - (viewportHeight / 2)
  
  // 요소가 화면에 완전히 보이지 않을 경우 스크롤
  if (elementTop < viewportTop + buffer || elementBottom > viewportBottom - buffer) {
    // 요소를 화면 중앙에 배치하도록 스크롤
    window.scrollTo({ 
      top: Math.max(0, targetScrollPosition), 
      behavior: 'smooth' 
    })
    
    // 스크롤 애니메이션이 완료된 후 위치 업데이트를 위해 약간의 지연 추가
    setTimeout(() => {
      positionTooltip()
    }, 500)
  }
}

const updatePosition = () => {
  nextTick(() => {
    positionTooltip()
  })
}

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
  background-color: rgba(0, 0, 0, 0.7);
  pointer-events: all;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.tour-overlay.active {
  opacity: 1;
}

.highlight-box {
  position: absolute;
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.75);
  border-radius: 4px;
  z-index: 10000;
  background-color: transparent;
  border: 2px solid #2979FF;
  transition: all 0.3s ease;
  animation: highlight-pulse 2s infinite alternate;
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

@media (max-width: 640px) {
  .tour-tooltip {
    width: min(280px, calc(100vw - 40px));
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

@media (max-width: 640px) {
  .tooltip-content {
    padding: 16px;
  }
  
  .tooltip-content h3 {
    font-size: 17px;
    margin-bottom: 10px;
  }
  
  .tooltip-content p {
    font-size: 15px;
    line-height: 1.7;
  }
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