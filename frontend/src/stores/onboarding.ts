import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useOnboardingStore = defineStore('onboarding', () => {
  // 온보딩 모드 활성화 여부
  const isOnboardingActive = ref(false)
  
  // 진행 상태 - 사용자가 어느 단계까지 온보딩을 확인했는지 추적
  const currentStep = ref(0)
  
  // 온보딩에서 다뤄야 하는 모든 섹션
  const totalSteps = 5 // 프로필, 통계, 티어, 매치목록, 리뷰 작성
  
  // 이미 완료한 온보딩 단계 (로컬 스토리지에 저장)
  const completedSteps = ref<number[]>([])
  
  // 온보딩 모드 전환
  function toggleOnboarding(value?: boolean) {
    isOnboardingActive.value = value !== undefined ? value : !isOnboardingActive.value
    
    // 온보딩 모드가 꺼질 때 현재 진행 단계 초기화
    if (!isOnboardingActive.value) {
      currentStep.value = 0
    }
  }
  
  // 다음 단계로 이동
  function nextStep() {
    if (currentStep.value < totalSteps - 1) {
      currentStep.value++
      
      // 현재 단계를 완료한 것으로 표시
      if (!completedSteps.value.includes(currentStep.value - 1)) {
        completedSteps.value.push(currentStep.value - 1)
        // 로컬 스토리지에 저장
        localStorage.setItem('onboarding_completed', JSON.stringify(completedSteps.value))
      }
    } else {
      // 마지막 단계가 끝나면 온보딩 종료
      toggleOnboarding(false)
    }
  }
  
  // 이전 단계로 이동
  function prevStep() {
    if (currentStep.value > 0) {
      currentStep.value--
    }
  }
  
  // 온보딩 초기화 (앱 시작 시 호출)
  function initOnboarding() {
    const saved = localStorage.getItem('onboarding_completed')
    if (saved) {
      completedSteps.value = JSON.parse(saved)
    }
  }
  
  return {
    isOnboardingActive,
    currentStep,
    totalSteps,
    completedSteps,
    toggleOnboarding,
    nextStep,
    prevStep,
    initOnboarding
  }
})