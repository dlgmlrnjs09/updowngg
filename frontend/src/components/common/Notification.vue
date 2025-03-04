<template>
  <div class="relative" ref="notificationDropdown">
    <button class="notificationEntity-btn" @click="toggleDropdown">
      <Bell class="bell-icon" />
      <div v-if="store.unreadCount" class="badge">
        {{ store.unreadCount }}
      </div>
    </button>

    <div v-if="isDropdownOpen" class="dropdown-notification">
      <div class="dropdown-header">
        <h3>알림</h3>
      </div>
      <div v-for="notificationEntity in store.notifications"
           :key="notificationEntity.notificationId"
           class="notification-item"
           :class="{ 'unread': !notificationEntity.readYn }"
           @click="handleNotificationClick(notificationEntity)"
      >
        {{notificationEntity.iconUrl}}
        <div class="flex items-center gap-3">
          <!-- iconUrl이 있으면 이미지를, 없으면 아이콘 컴포넌트를 표시 -->
          <img v-if="notificationEntity.iconUrl"
               :src="notificationEntity.iconUrl"
               class="w-8 h-8 rounded-full"
               alt=""/>
          <component v-else
                     :is="getNotificationIcon(notificationEntity.notificationType)"
                     class="w-8 h-8 p-1 rounded-full bg-gray-800"
          />

          <span class="content" v-html="formatContent(notificationEntity.content)"></span>
        </div>
        <div class="notification-footer">
          <span class="time">{{ formatTimeAgo(notificationEntity.regDt) }}</span>
        </div>
      </div>
      <div v-if="store.notifications.length == 0" class="empty-state">
        받은 알림이 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useNotificationStore } from '@/stores/notification.ts'
import { onMounted, ref, onUnmounted, watch } from 'vue'
import { Bell, Star, Users, Info, MessageSquare, UserPlus } from 'lucide-vue-next'
import {formatTimeAgo} from "@/utils/common.ts";
import { useDropdownStore } from '@/stores/dropdown'
import type {Notification} from "@/types/notification.ts";
import {reviewApi} from "@/api/review.ts";
import {useRouter} from "vue-router";

const dropdownStore = useDropdownStore()
const store = useNotificationStore()
const isDropdownOpen = ref(false)
const notificationDropdown = ref<HTMLElement | null>(null)
const router = useRouter();

// 아이콘 컴포넌트 반환 함수
const getNotificationIcon = (type: string) => {
  switch(type) {
    case 'SITE_REVIEW':
      return MessageSquare;
    case 'PARTY_COMMUNITY':
      return Users;
    default:
      return Bell;
  }
};

const formatContent = (content: string) => {
  if (!content) {
    return content;
  }

  return content.replace(/\*\*([^*]+)\*\*/g, (fullMatch, capturedText) => {
    // ISO 날짜 형식인지 확인 (YYYY-MM-DDThh:mm:ss.sss)
    const isISOTimestamp = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d{1,3})?$/.test(capturedText);

    if (isISOTimestamp) {
      return `<strong>${formatTimeAgo(capturedText)}</strong>`;
    }

    // 타임스탬프가 아니라면 단순 강조처리
    return `<strong>${capturedText}</strong>`;
  });
};

onMounted(() => {
  /*store.initSSE()*/
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

watch(() => dropdownStore.openDropdown, (newValue) => {
  if (newValue !== 'notification') {
    isDropdownOpen.value = false
  }
})

const handleClickOutside = (event: MouseEvent) => {
  if (notificationDropdown.value && !notificationDropdown.value.contains(event.target as Node)) {
    isDropdownOpen.value = false
  }
}

const toggleDropdown = () => {
  if (isDropdownOpen.value) {
    dropdownStore.setOpenDropdown(null)
  } else {
    dropdownStore.setOpenDropdown('notification')
    handleReadAll(store.notifications.filter(n => !n.readYn).map(n => n.notificationId))
  }
  isDropdownOpen.value = !isDropdownOpen.value
}

const handleRead = (id: string) => {
  store.markAsRead(id)
}

const handleReadAll = (ids: string[]) => {
  store.markAsReadAll(ids)
}

const handleNotificationClick = async (notification: Notification) => {
  try {
    if (notification.notificationType === 'SITE_REVIEW') {
      const response = await reviewApi.findReviewPage(notification.subSeq);
      const page = response.data;

      await router.push({
        name: 'reviewHistory',
        query: {
          page: page.toString(),
          tab: 'received',
          reviewSeq: notification.subSeq.toString()
        }
      });
    }

    dropdownStore.setOpenDropdown(null)
    isDropdownOpen.value = !isDropdownOpen.value
  } catch (error) {
    console.error('Error handling notification click:', error);
  }
};
</script>

<style scoped>
.notificationEntity-btn {
  position: relative;
  height: 36px;
  padding: 0 15px;
  color: #888;
  background: none;
  border: 1.5px solid #444;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
}

.bell-icon {
  width: 1.25rem;
  height: 1.25rem;
}

.notificationEntity-btn:hover {
  color: white;
  border-color: #666;
}

.notification-footer {
  text-align: right;
}

.badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #ff4444;
  color: white;
  font-size: 11px;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.dropdown-notification {
  position: absolute;
  top: 100%;
  /*right: 0;*/
  margin-top: 8px;
  background: #141414;
  border: 1.5px solid #333;
  border-radius: 8px;
  width: 320px;
  max-height: 400px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(41, 121, 255, 0.3) transparent;
  z-index: 9999;
}

/* Webkit 스크롤바 스타일링 */
.dropdown-notification::-webkit-scrollbar {
  width: 6px;
}

.dropdown-notification::-webkit-scrollbar-track {
  background: transparent;
}

.dropdown-notification::-webkit-scrollbar-thumb {
  background-color: rgba(41, 121, 255, 0.3);
  border-radius: 3px;
}

.dropdown-notification::-webkit-scrollbar-thumb:hover {
  background-color: rgba(41, 121, 255, 0.5);
}

.dropdown-header {
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dropdown-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
}

.read-all-btn, .read-btn {
  font-size: 12px;
  color: #888;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.read-all-btn:hover, .read-btn:hover {
  color: #fff;
  background: rgba(255,255,255,0.1);
}

.divider {
  height: 1px;
  background: #333;
  /*margin: 0 12px;*/
  /*margin-bottom: 10px;*/
}

.notifications-list {
  padding: 8px;
}

.notification-item {
  padding: 12px;
  border: 1px solid #333;
  /*border-radius: 6px;*/
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.notification-item:hover {
  background: #1a1a1a;
}

.notification-item.unread {
  background: rgba(255,255,255,0.05);
}

.content {
  font-size: 14px;
  color: #fff;
}

.time {
  font-size: 12px;
  color: #666;
  text-align: right;
}

.empty-state {
  padding: 24px;
  text-align: center;
  color: #666;
  font-size: 14px;
  border: 1px solid #333
}

:deep(.content strong) {
  color: #2979ff;
  font-weight: 600;
}

@media (max-width: 1024px) {
  .dropdown-notification {
    right: 0;
  }
}

@media (max-width: 768px) {
  .notificationEntity-btn {
    height: 32px;
    padding: 0 10px;
  }

  .bell-icon {
    width: 1rem;
    height: 1rem;
  }
}
</style>