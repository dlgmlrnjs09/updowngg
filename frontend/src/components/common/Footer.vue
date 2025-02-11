# Footer.vue
<template>
  <footer class="bg-[#0A0A0A] border-t border-gray-800">
    <div class="max-w-7xl mx-auto px-4 py-4">
      <!-- 링크 -->
      <div class="flex items-center justify-center space-x-6 text-sm text-gray-400 mb-4">
        <RouterLink to="/terms" class="hover:text-white transition-colors">
          이용약관
        </RouterLink>
        <RouterLink to="/privacy" class="hover:text-white transition-colors">
          개인정보처리방침
        </RouterLink>
        <a href="mailto:lcb0867@naver.com"
           class="hover:text-white transition-colors">
          <Mail :size="18" />
        </a>
      </div>

      <!-- 저작권 및 고지 -->
      <div class="text-gray-500 text-sm text-center">
        <p>© 2025 updownGG. All rights reserved.</p>
        <p class="mt-1 text-xs">
          updownGG은 Riot Games의 공식 서비스가 아니며, Riot Games 또는 LoL e스포츠 관계자들과 직접적인 관련이 없습니다.
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { Mail } from 'lucide-vue-next'
</script>