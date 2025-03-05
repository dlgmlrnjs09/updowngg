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
  const position = currentStepData.value.position || 'bottom'
  
  // Position the tooltip based on specified position
  let top, left

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
  
  if (left < 20) left = 20
  if (left + tooltipWidth > viewportWidth - 20) left = viewportWidth - tooltipWidth - 20
  if (top < 20) top = 20
  if (top + tooltipHeight > viewportHeight + window.scrollY - 20) {
    // If it would go below viewport, try to position it above the element instead
    top = targetRect.top - tooltipHeight - 20 + window.scrollY
  }
  
  tooltipPosition.value = { top, left }
  
  // Scroll target into view if needed
  const buffer = 150 // Extra buffer to scroll before the element
  const elementTop = targetRect.top + window.scrollY
  const elementBottom = targetRect.bottom + window.scrollY
  const viewportTop = window.scrollY
  const viewportBottom = window.scrollY + window.innerHeight
  
  if (elementTop < viewportTop + buffer) {
    window.scrollTo({ top: elementTop - buffer, behavior: 'smooth' })
  } else if (elementBottom > viewportBottom - buffer) {
    window.scrollTo({ top: elementBottom - window.innerHeight + buffer, behavior: 'smooth' })
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
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.7);
  border-radius: 4px;
  z-index: 10000;
  background-color: transparent;
  border: 2px solid #2979FF;
  transition: all 0.3s ease;
}

.tour-tooltip {
  position: absolute;
  width: 280px;
  background-color: #141414;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
  z-index: 10001;
  color: white;
  transition: all 0.3s ease;
  overflow: hidden;
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
}

.tooltip-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.8);
}

.tooltip-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.tour-btn {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.tour-btn.prev {
  background-color: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
}

.tour-btn.prev:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.tour-btn.next, .tour-btn.finish {
  background-color: #2979FF;
  border: none;
  color: white;
}

.tour-btn.next:hover, .tour-btn.finish:hover {
  background-color: #1A67E0;
}
</style>