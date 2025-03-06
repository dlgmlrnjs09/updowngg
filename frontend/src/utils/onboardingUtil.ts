/**
 * 온보딩 유틸리티 - 다른 페이지에서 온보딩 기능을 쉽게 사용할 수 있도록 도와주는 함수 모음
 */
import { useOnboardingStore } from '@/stores/onboarding';

// 페이지별 온보딩 상태를 저장하는 키
const getPageOnboardingKey = (pageName: string) => `onboarding_completed_${pageName}`;

/**
 * 특정 페이지의 온보딩을 초기화 (페이지 로드 시 호출)
 * @param pageName 페이지 이름 (예: 'profile', 'party', 'stats' 등)
 * @param autoStart 자동 시작 여부 (처음 방문시)
 * @returns 페이지별 완료 여부
 */
export const initPageOnboarding = (pageName: string, autoStart = true): boolean => {
  const onboardingStore = useOnboardingStore();
  const storageKey = getPageOnboardingKey(pageName);
  const isCompleted = localStorage.getItem(storageKey) === 'true';
  
  // 페이지별 완료 여부에 따라 전체 온보딩 상태 업데이트
  if (!isCompleted && autoStart) {
    onboardingStore.toggleOnboarding(true);
  }
  
  return isCompleted;
};

/**
 * 특정 페이지의 온보딩 완료 처리
 * @param pageName 페이지 이름
 */
export const completePageOnboarding = (pageName: string): void => {
  const storageKey = getPageOnboardingKey(pageName);
  localStorage.setItem(storageKey, 'true');
  
  // 온보딩 스토어도 완료 처리
  const onboardingStore = useOnboardingStore();
  onboardingStore.completeOnboarding();
};

/**
 * 특정 페이지의 온보딩 초기화 (다시 볼 수 있도록)
 * @param pageName 페이지 이름
 */
export const resetPageOnboarding = (pageName: string): void => {
  const storageKey = getPageOnboardingKey(pageName);
  localStorage.removeItem(storageKey);
  
  // 온보딩 스토어 초기화
  const onboardingStore = useOnboardingStore();
  onboardingStore.resetOnboarding();
};

/**
 * 특정 페이지에 대한 온보딩 셀렉터 생성 
 * 각 페이지에서 가이드할 요소에 대한 셀렉터를 정의
 * @param pageName 페이지 이름
 * @returns 셀렉터 배열
 */
export const getPageSelectors = (pageName: string): string[] => {
  // 각 페이지별 셀렉터 정의
  const selectors: Record<string, string[]> = {
    // 소환사 프로필 페이지
    'profile': [
      '.profile-info',          // 프로필 정보
      '.tags-section',          // 통계 정보
      '.rank-section',          // 티어 정보
      '.games-section',         // 매치 목록
      '.button-group'           // 리뷰 버튼
    ],
    
    // 파티 커뮤니티 페이지
    'party': [
      '.party-header',          // 파티 헤더
      '.party-filter',          // 파티 필터
      '.party-list',            // 파티 목록
      '.party-create-button',   // 파티 생성 버튼
      '.my-party-section'       // 내 파티 정보
    ],
    
    // 듀오 커뮤니티 페이지
    'duo': [
      '.duo-header',            // 듀오 헤더
      '.duo-filter',            // 듀오 필터
      '.duo-list',              // 듀오 목록
      '.duo-create-button',     // 듀오 생성 버튼
      '.my-applications'        // 내 신청 정보
    ],
    
    // 통계 페이지
    'stats': [
      '.stats-header',          // 통계 헤더
      '.stats-filter',          // 통계 필터
      '.champion-stats',        // 챔피언 통계
      '.position-stats',        // 포지션 통계
      '.stats-chart'            // 통계 차트
    ],
    
    // 기본 페이지 (다른 페이지 없을 때 사용)
    'default': [
      '.header',                // 헤더
      '.content-area',          // 콘텐츠 영역
      '.sidebar',               // 사이드바
      '.footer',                // 푸터
      '.action-button'          // 액션 버튼
    ]
  };
  
  // 해당 페이지의 셀렉터가 없으면 기본 셀렉터 반환
  return selectors[pageName] || selectors['default'];
};

export default {
  initPageOnboarding,
  completePageOnboarding,
  resetPageOnboarding,
  getPageSelectors
};