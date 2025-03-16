<template>
  <teleport to="body">
    <div v-if="isOpen" class="modal-overlay">
      <!-- Backdrop -->
      <div class="modal-backdrop" @click="closeModal"></div>

      <!-- Modal -->
      <div class="modal-container">
        <!-- Close button -->
        <button @click="closeModal" class="close-button">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M18 6 6 18"></path>
            <path d="m6 6 12 12"></path>
          </svg>
        </button>

        <!-- Slides container -->
        <div
            ref="slidesContainer"
            class="slides-wrapper"
            :style="{ transform: `translateX(-${currentSlide * 100}%)` }"
            @touchstart="handleTouchStart"
            @touchmove="handleTouchMove"
            @touchend="handleTouchEnd"
        >
          <div v-for="(slide, index) in slides" :key="index" class="slide">
            <div class="slide-content">
              <div class="slide-image-container">
                <picture>
                  <source
                      media="(max-width: 480px)"
                      :srcset="slide.image.mobile"
                  >
                  <img
                      :src="slide.image.desktop"
                      :alt="`Onboarding slide ${index + 1}`"
                      class="slide-image"
                  />
                </picture>
              </div>
              <h3 class="slide-title">{{ slide.title }}</h3>
              <p class="slide-description">{{ slide.description }}</p>
            </div>
          </div>
        </div>

        <!-- Indicators and controls -->
        <div class="controls-container">
          <!-- Indicators -->
          <div class="indicators">
            <button
                v-for="(_, index) in slides"
                :key="index"
                @click="goToSlide(index)"
                class="indicator"
                :class="{ 'active': index === currentSlide }"
            ></button>
          </div>

          <!-- Controls -->
          <div class="navigation-buttons">
            <button
                v-if="currentSlide > 0"
                @click="prevSlide"
                class="nav-button prev-button"
            >
              이전
            </button>
            <div v-else class="button-spacer"></div>

            <button
                v-if="currentSlide < slides.length - 1"
                @click="nextSlide"
                class="nav-button next-button"
            >
              다음
            </button>
            <button
                v-else
                @click="closeModal"
                class="nav-button start-button"
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

export interface Slide {
  title: string;
  description: string;
  image: {
    desktop: string;
    mobile: string;
  };
}

interface OnboardingModalProps {
  isOpen: boolean;
  slides: Slide[];
  storageKey?: string;
}

const props = withDefaults(defineProps<OnboardingModalProps>(), {
  storageKey: 'onboardingShown'
});

const emit = defineEmits(['close']);

const currentSlide = ref(0);
const touchStartX = ref(0);
const touchEndX = ref(0);
const slidesContainer = ref<HTMLElement | null>(null);

const closeModal = () => {
  emit('close');
  if (props.storageKey) {
    localStorage.setItem(props.storageKey, 'true');
  }
};

const goToSlide = (index: number) => {
  currentSlide.value = index;
};

const nextSlide = () => {
  if (currentSlide.value < props.slides.length - 1) {
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
  } else if (swipeDistance < -minSwipeDistance && currentSlide.value < props.slides.length - 1) {
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
  // 슬라이드가 변경되었을 때 현재 슬라이드를
  currentSlide.value = 0;
});

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeyDown);
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-backdrop {
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(3px);
  -webkit-backdrop-filter: blur(3px);
}

.modal-container {
  position: relative;
  background-color: #0A0A0A;
  border: 1.5px solid #333;
  border-radius: 12px;
  max-width: 800px;
  width: 90%;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.5), 0 10px 10px -5px rgba(0, 0, 0, 0.2);
}

.close-button {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 10;
  color: #888;
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-button:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}

.slides-wrapper {
  display: flex;
  transition: transform 0.4s cubic-bezier(0.25, 1, 0.5, 1);
  will-change: transform;
}

.slide {
  flex-shrink: 0;
  width: 100%;
}

.slide-content {
  padding: 2rem 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.slide-image-container {
  width: 100%;
  aspect-ratio: 16 / 9;
  background-color: #141414;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 1.5rem;
  margin-top: 2.0rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  max-height: 675px; /* 1200x675 이미지에 대응 */
}

@media (max-width: 480px) {
  .slide-image-container {
    aspect-ratio: 3 / 4;
    max-height: none;
  }
}

.slide-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.slide-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
  margin-bottom: 0.5rem;
  text-align: center;
}

.slide-description {
  color: #888;
  text-align: center;
  font-size: 1rem;
  line-height: 1.5;
  max-width: 600px;
}

.controls-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1rem 1.5rem 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.indicators {
  display: flex;
  gap: 8px;
  margin-bottom: 1rem;
}

.indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #333;
  border: none;
  padding: 0;
  cursor: pointer;
  transition: all 0.2s;
}

.indicator.active {
  width: 16px;
  background-color: #0066FF;
  border-radius: 4px;
}

.navigation-buttons {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.nav-button {
  height: 36px;
  border-radius: 8px;
  padding: 0 1.25rem;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: 1.5px solid transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 80px;
}

.prev-button {
  background-color: transparent;
  border-color: #333;
  color: #888;
}

.prev-button:hover {
  border-color: #444;
  color: white;
}

.next-button, .start-button {
  background-color: #0066FF;
  color: white;
}

.next-button:hover, .start-button:hover {
  background-color: #0055DD;
}

.button-spacer {
  width: 80px;
}

/* Responsive adjustments */
@media (min-width: 1200px) {
  .modal-container {
    max-width: 1000px;
  }

  .slide-title {
    font-size: 1.75rem;
  }

  .slide-description {
    font-size: 1.125rem;
    max-width: 700px;
  }

  .nav-button {
    min-width: 100px;
    height: 40px;
    font-size: 1rem;
  }
}

@media (max-width: 768px) {
  .modal-container {
    max-width: 95%;
    width: 95%;
  }

  .slide-content {
    padding: 1.75rem 1.25rem;
  }

  .slide-title {
    font-size: 1.25rem;
  }

  .slide-description {
    font-size: 0.9rem;
    max-width: 500px;
  }
}

@media (max-width: 480px) {
  .modal-container {
    max-width: 100%;
    width: 100%;
    height: auto;
    max-height: 90vh;
    margin: 0 8px;
  }

  .slide-content {
    padding: 1.5rem 1rem;
  }

  .slide-title {
    font-size: 1.125rem;
  }

  .slide-description {
    font-size: 0.875rem;
    max-width: 100%;
  }

  .controls-container {
    padding: 1rem;
  }

  .nav-button {
    min-width: 70px;
    height: 34px;
    padding: 0 1rem;
    font-size: 0.875rem;
  }

  .button-spacer {
    width: 70px;
  }

  .navigation-buttons {
    display: none;
  }
}
</style>