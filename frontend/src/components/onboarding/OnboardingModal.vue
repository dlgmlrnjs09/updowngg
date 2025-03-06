<template>
  <teleport to="body">
    <div v-if="isOpen" class="fixed inset-0 flex items-center justify-center z-50">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-black/70" @click="closeModal"></div>
      
      <!-- Modal -->
      <div class="relative bg-gray-800 rounded-xl max-w-xl w-full mx-4 overflow-hidden">
        <!-- Close button -->
        <button 
          @click="closeModal" 
          class="absolute top-3 right-3 z-10 text-white hover:text-gray-300"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-x"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
        </button>
        
        <!-- Slides container -->
        <div 
          ref="slidesContainer"
          class="flex transition-transform duration-300 ease-in-out"
          :style="{ transform: `translateX(-${currentSlide * 100}%)` }"
          @touchstart="handleTouchStart"
          @touchmove="handleTouchMove"
          @touchend="handleTouchEnd"
        >
          <div v-for="(slide, index) in slides" :key="index" class="flex-shrink-0 w-full">
            <div class="p-6 flex flex-col items-center">
              <div class="w-full aspect-video bg-gray-700 rounded-lg mb-6 flex items-center justify-center overflow-hidden">
                <img 
                  :src="slide.image" 
                  :alt="`Onboarding slide ${index + 1}`"
                  class="w-full h-full object-contain"
                />
              </div>
              <h3 class="text-xl font-bold text-white mb-2">{{ slide.title }}</h3>
              <p class="text-gray-300 text-center mb-6">{{ slide.description }}</p>
            </div>
          </div>
        </div>
        
        <!-- Indicators and controls -->
        <div class="flex flex-col items-center p-4 border-t border-gray-700">
          <!-- Indicators -->
          <div class="flex gap-2 mb-4">
            <button 
              v-for="(_, index) in slides" 
              :key="index"
              @click="goToSlide(index)"
              class="w-2 h-2 rounded-full transition-all"
              :class="index === currentSlide ? 'bg-blue-500 w-4' : 'bg-gray-500'"
            ></button>
          </div>
          
          <!-- Controls -->
          <div class="flex justify-between w-full">
            <button 
              v-if="currentSlide > 0"
              @click="prevSlide" 
              class="px-4 py-2 text-white bg-gray-700 rounded-lg hover:bg-gray-600 transition"
            >
              이전
            </button>
            <div v-else class="w-20"></div>
            
            <button 
              v-if="currentSlide < slides.length - 1" 
              @click="nextSlide"
              class="px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-500 transition"
            >
              다음
            </button>
            <button 
              v-else 
              @click="closeModal"
              class="px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-500 transition"
            >
              시작하기
            </button>
          </div>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';

interface Slide {
  title: string;
  description: string;
  image: string;
}

const props = defineProps<{
  isOpen: boolean;
}>();

const emit = defineEmits(['close']);

const slides = ref<Slide[]>([
  {
    title: '빠른 소환사 검색',
    description: '소환사 이름 검색으로 전적을 확인하고 평가해보세요.',
    image: '/images/onboarding/search.png'
  },
  {
    title: '상세한 게임 정보',
    description: '매치 상세 정보와 함께한 소환사들을 확인할 수 있습니다.',
    image: '/images/onboarding/match.png'
  },
  {
    title: '소환사 평가하기',
    description: '함께 플레이한 소환사를 평가하고 태그를 남겨보세요.',
    image: '/images/onboarding/review.png'
  },
  {
    title: '파티 모집하기',
    description: '함께 게임할 파티원을 모집하거나 참여해보세요.',
    image: '/images/onboarding/party.png'
  }
]);

const currentSlide = ref(0);
const touchStartX = ref(0);
const touchEndX = ref(0);
const slidesContainer = ref<HTMLElement | null>(null);

const closeModal = () => {
  emit('close');
  localStorage.setItem('onboardingShown', 'true');
};

const goToSlide = (index: number) => {
  currentSlide.value = index;
};

const nextSlide = () => {
  if (currentSlide.value < slides.value.length - 1) {
    currentSlide.value++;
  }
};

const prevSlide = () => {
  if (currentSlide.value > 0) {
    currentSlide.value--;
  }
};

// 터치 이벤트 핸들러
const handleTouchStart = (e: TouchEvent) => {
  touchStartX.value = e.touches[0].clientX;
};

const handleTouchMove = (e: TouchEvent) => {
  touchEndX.value = e.touches[0].clientX;
};

const handleTouchEnd = () => {
  const swipeDistance = touchEndX.value - touchStartX.value;
  const minSwipeDistance = 50;
  
  if (swipeDistance > minSwipeDistance && currentSlide.value > 0) {
    // 오른쪽으로 스와이프 - 이전 슬라이드
    prevSlide();
  } else if (swipeDistance < -minSwipeDistance && currentSlide.value < slides.value.length - 1) {
    // 왼쪽으로 스와이프 - 다음 슬라이드
    nextSlide();
  }
  
  // 터치 값 초기화
  touchStartX.value = 0;
  touchEndX.value = 0;
};

// 키보드 이벤트 처리
const handleKeyDown = (e: KeyboardEvent) => {
  if (props.isOpen) {
    if (e.key === 'Escape') {
      closeModal();
    } else if (e.key === 'ArrowRight') {
      nextSlide();
    } else if (e.key === 'ArrowLeft') {
      prevSlide();
    }
  }
};

onMounted(() => {
  document.addEventListener('keydown', handleKeyDown);
});

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeyDown);
});
</script>