<template>
  <div class="relative" ref="notificationDropdown">
    <button class="notificationEntity-btn" @click="toggleDropdown">
      <Bell class="w-5 h-5" />
      <div v-if="store.unreadCount" class="badge">
        {{ store.unreadCount }}
      </div>
    </button>

    <div v-if="isDropdownOpen" class="dropdown-menu">
      <div v-for="notificationEntity in store.notifications.slice(0,10)"
           :key="notificationEntity.notificationId"
           class="notificationEntity-item"
           :class="{ 'unread': !notificationEntity.readYn }"
           @click="handleRead(notificationEntity.notificationId)">
        <div class="flex items-center gap-3">
          <img :src="notificationEntity.championIconUrl" class="w-8 h-8 rounded-full"  alt=""/>
          <span class="content">{{ notificationEntity.content }}</span>
        </div>
        <span class="time">{{ formatTime(notificationEntity.regDt) }}</span>
      </div>
      <div v-if="!store.notifications" class="empty-state">
        알림이 없습니다
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useNotificationStore } from '@/stores/notification.ts'
import { onMounted, ref, onUnmounted } from 'vue'
import { Bell } from 'lucide-vue-next'

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

const handleClickOutside = (event: MouseEvent) => {
  if (notificationDropdown.value && !notificationDropdown.value.contains(event.target as Node)) {
    isDropdownOpen.value = false
  }
}

const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value
}

const handleRead = (id: string) => {
  store.markAsRead(id)
}

const formatTime = (dateString: string) => {
  const now = new Date();
  const date = new Date(dateString);
  const hours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60));

  if (hours < 24) {
    return `${hours}시간 전`;
  }

  return date.toLocaleDateString();
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

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: #141414;
  border: 1.5px solid #333;
  border-radius: 8px;
  width: 320px;
  max-height: 400px;
  overflow-y: auto;
}

.notifications-list {
  padding: 8px;
}

.notificationEntity-item {
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.notificationEntity-item:hover {
  background: #1a1a1a;
}

.notificationEntity-item.unread {
  background: rgba(255,255,255,0.05);
}

.content {
  font-size: 14px;
  color: #fff;
}

.time {
  font-size: 12px;
  color: #666;
}

.empty-state {
  padding: 24px;
  text-align: center;
  color: #666;
  font-size: 14px;
}
</style>