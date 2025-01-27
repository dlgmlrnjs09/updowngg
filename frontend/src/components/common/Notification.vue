<template>
  <div class="relative" ref="notificationDropdown">
    <button class="notificationEntity-btn" @click="toggleDropdown">
      <Bell class="w-5 h-5" />
      <div v-if="store.unreadCount" class="badge">
        {{ store.unreadCount }}
      </div>
    </button>

    <div v-if="isDropdownOpen" class="dropdown-notification">
      <div class="dropdown-header">
        <h3>알림</h3>
        <button @click="handleReadAll(store.notifications.slice(0,10).filter(n => !n.readYn).map(n => n.notificationId))" class="read-all-btn">
          모두 확인
        </button>
      </div>
<!--      <div class="divider"></div>-->
      <div v-for="notificationEntity in store.notifications.slice(0,10)"
           :key="notificationEntity.notificationId"
           class="notification-item"
           :class="{ 'unread': !notificationEntity.readYn }"
           @click="!notificationEntity.readYn && handleRead(notificationEntity.notificationId)">
        <div class="flex items-center gap-3">
          <img :src="notificationEntity.championIconUrl" class="w-8 h-8 rounded-full" alt=""/>
          <span class="content">
       <span class="text-blue-400">{{ formatTimeAgo(notificationEntity.gameCreateDt) }}</span>에 플레이한
       <span class="text-green-400">{{notificationEntity.gameModeName}}</span> 게임의 평가가 도착했어요!
     </span>
        </div>
        <div class="notification-footer">
<!--          <button class="read-btn" @click.stop="handleRead(notificationEntity.notificationId)">-->
<!--            {{ notificationEntity.readYn ? '읽음' : '읽기' }}-->
<!--          </button>-->
          <span class="time">{{ formatTimeAgo(notificationEntity.regDt) }}</span>
        </div>
      </div>
      <div v-if="!store.notifications" class="empty-state">
        알림이 없습니다
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useNotificationStore } from '@/stores/notification.ts'
import { onMounted, ref, onUnmounted, watch } from 'vue'
import { Bell } from 'lucide-vue-next'
import {formatTimeAgo} from "@/common.ts";
import { useDropdownStore } from '@/stores/dropdown'

const dropdownStore = useDropdownStore()
const store = useNotificationStore()
const isDropdownOpen = ref(false)
const notificationDropdown = ref<HTMLElement | null>(null)

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
  }
  isDropdownOpen.value = !isDropdownOpen.value
}

const handleRead = (id: string) => {
  store.markAsRead(id)
}

const handleReadAll = (ids: string[]) => {
  store.markAsReadAll(ids)
}
</script>

<style scoped>
.notificationEntity-btn {
  position: relative;
  height: 36px;
  padding: 0 12px;
  color: #888;
  background: none;
  border: 1.5px solid #444;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
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
}

@media (max-width: 1024px) {
  .dropdown-notification {
    right: 0;
  }
}
</style>