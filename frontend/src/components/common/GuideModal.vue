<template>
  <Teleport to="body">
    <div v-if="show" class="guide-modal-overlay" @click="handleOverlayClick">
      <div class="guide-modal" @click.stop>
        <!-- 헤더 -->
        <div class="guide-modal-header">
          <h2 class="guide-title">{{ currentPage.title }}</h2>
          <button class="close-button" @click="$emit('close')">
            <X class="w-5 h-5" />
          </button>
        </div>
        
        <!-- 내용 -->
        <div class="guide-modal-content">
          <!-- 이미지 영역 (직접 사진을 넣을 공간) -->
          <div class="guide-image-container">
            <div class="guide-image-placeholder">
              <!-- 이미지는 props에서 제공되는 URL을 사용 -->
              <img 
                v-if="currentPage.image" 
                :src="currentPage.image" 
                :alt="currentPage.title"
                class="guide-image"
              />
              <span v-else class="guide-image-placeholder-text">여기에 이미지가 표시됩니다</span>
            </div>
          </div>
          
          <!-- 텍스트 내용 -->
          <div class="guide-text-content">
            <p>{{ currentPage.content }}</p>
          </div>
        </div>
        
        <!-- 페이지 인디케이터 -->
        <div class="guide-page-indicator">
          <div 
            v-for="(_, index) in pages" 
            :key="index" 
            class="indicator-dot"
            :class="{ active: currentPageIndex === index }"
            @click="goToPage(index)"
          ></div>
        </div>
        
        <!-- 버튼 영역 -->
        <div class="guide-modal-footer">
          <button 
            v-if="currentPageIndex > 0"
            class="guide-btn prev"
            @click="prevPage"
          >
            이전
          </button>
          <button 
            v-if="currentPageIndex < pages.length - 1"
            class="guide-btn next"
            @click="nextPage"
          >
            다음
          </button>
          <button 
            v-else
            class="guide-btn finish"
            @click="$emit('complete')"
          >
            완료
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { X } from 'lucide-vue-next'

interface GuidePage {
  title: string
  content: string
  image?: string
}

const props = defineProps<{
  show: boolean
  pages: GuidePage[]
  closeOnOverlayClick?: boolean
  startAt?: number
}>()

const emit = defineEmits(['close', 'page-changed', 'complete'])

const currentPageIndex = ref(props.startAt || 0)

const currentPage = computed(() => {
  return props.pages[currentPageIndex.value] || { title: '', content: '' }
})

// 페이지 이동 함수들
const nextPage = () => {
  if (currentPageIndex.value < props.pages.length - 1) {
    currentPageIndex.value++
    emit('page-changed', currentPageIndex.value)
  }
}

const prevPage = () => {
  if (currentPageIndex.value > 0) {
    currentPageIndex.value--
    emit('page-changed', currentPageIndex.value)
  }
}

const goToPage = (pageIndex: number) => {
  if (pageIndex >= 0 && pageIndex < props.pages.length) {
    currentPageIndex.value = pageIndex
    emit('page-changed', currentPageIndex.value)
  }
}

const handleOverlayClick = (event: MouseEvent) => {
  // 오버레이 클릭 시 모달 닫기 (옵션에 따라)
  if (props.closeOnOverlayClick && event.target === event.currentTarget) {
    emit('close')
  }
}

// props 변경 시 현재 페이지 초기화
watch(() => props.show, (newVal) => {
  if (newVal) {
    currentPageIndex.value = props.startAt || 0
  }
})

// 외부에서 접근할 수 있도록 일부 함수와 상태 노출
defineExpose({
  nextPage,
  prevPage,
  goToPage,
  currentPageIndex
})
</script>

<style scoped>
.guide-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  padding: 16px;
  animation: fadeIn 0.3s ease;
}

.guide-modal {
  background-color: #141414;
  width: 100%;
  max-width: 800px;
  border-radius: 12px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  animation: slideUp 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

.guide-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.guide-title {
  font-size: 20px;
  font-weight: 600;
  color: #2979FF;
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-button:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}

.guide-modal-content {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.guide-image-container {
  margin-bottom: 20px;
}

.guide-image-placeholder {
  background-color: #1A1A1A;
  border-radius: 8px;
  width: 100%;
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.guide-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.guide-image-placeholder-text {
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
}

.guide-text-content {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  line-height: 1.6;
}

.guide-page-indicator {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 16px 0;
}

.indicator-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.2s;
}

.indicator-dot.active {
  background-color: #2979FF;
  transform: scale(1.2);
}

.indicator-dot:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.guide-modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.guide-btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 90px;
  text-align: center;
}

.guide-btn.prev {
  background-color: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
}

.guide-btn.prev:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
}

.guide-btn.next, .guide-btn.finish {
  background-color: #2979FF;
  border: none;
  color: white;
}

.guide-btn.next:hover, .guide-btn.finish:hover {
  background-color: #1A67E0;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(41, 121, 255, 0.3);
}

.guide-btn.finish {
  background-color: #4CAF50;
}

.guide-btn.finish:hover {
  background-color: #3d8b40;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

/* 반응형 스타일 */
@media (max-width: 640px) {
  .guide-modal {
    max-width: 100%;
    height: auto;
    max-height: 90vh;
  }
  
  .guide-modal-header {
    padding: 16px;
  }
  
  .guide-modal-content {
    padding: 16px;
  }
  
  .guide-image-placeholder {
    height: 200px;
  }
  
  .guide-text-content {
    font-size: 15px;
  }
  
  .guide-title {
    font-size: 18px;
  }
  
  .guide-modal-footer {
    padding: 16px;
  }
}
</style>