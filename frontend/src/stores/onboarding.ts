import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useOnboardingStore = defineStore('onboarding', () => {
  // 온보딩 모드 활성화 여부
  const isOnboardingActive = ref(false)
  
  // 진행 상태 - 사용자가 어느 단계까지 온보딩을 확인했는지 추적
  const currentStep = ref(0)
  
  // 온보딩에서 다뤄야 하는 모든 섹션
  const totalSteps = 5 // 프로필, 통계, 티어, 매치목록, 리뷰 작성
  
  // 온보딩을 완료했는지 여부
  const isCompleted = ref(false)
  
  // 현재 강조 중인 요소의 셀렉터
  const highlightedElement = ref('')
  
  // 온보딩 모드 전환
  function toggleOnboarding(value?: boolean) {
    isOnboardingActive.value = value !== undefined ? value : !isOnboardingActive.value
    
    // 온보딩 모드가 꺼질 때 현재 진행 단계 초기화
    if (!isOnboardingActive.value) {
      currentStep.value = 0
      highlightedElement.value = ''
    }
  }
  
  // 다음 단계로 이동
  function nextStep() {
    if (currentStep.value < totalSteps - 1) {
      currentStep.value++
    } else {
      // 마지막 단계가 끝나면 온보딩 완료 처리
      completeOnboarding()
    }
  }
  
  // 이전 단계로 이동
  function prevStep() {
    if (currentStep.value > 0) {
      currentStep.value--
    }
  }
  
  // 강조할 요소 변경
  function setHighlightedElement(selector: string) {
    highlightedElement.value = selector
  }
  
  // 온보딩 완료 처리
  function completeOnboarding() {
    isCompleted.value = true
    localStorage.setItem('onboarding_completed', 'true')
    toggleOnboarding(false)
  }
  
  // 온보딩 초기화 (앱 시작 시 호출)
  function initOnboarding() {
    const completed = localStorage.getItem('onboarding_completed')
    isCompleted.value = completed === 'true'
    
    // 첫 방문 시 자동으로 온보딩 활성화
    if (!isCompleted.value) {
      toggleOnboarding(true)
    }
  }
  
  // 온보딩 다시 보기 (온보딩을 이미 완료한 사용자도 다시 볼 수 있도록)
  function resetOnboarding() {
    isCompleted.value = false
    localStorage.removeItem('onboarding_completed')
    currentStep.value = 0
    toggleOnboarding(true)
  }
  
  return {
    isOnboardingActive,
    currentStep,
    totalSteps,
    isCompleted,
    highlightedElement,
    toggleOnboarding,
    nextStep,
    prevStep,
    setHighlightedElement,
    completeOnboarding,
    initOnboarding,
    resetOnboarding
  }
})