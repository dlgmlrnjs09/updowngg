<!-- src/views/AuthSuccess.vue -->
<template>
  <div class="flex items-center justify-center min-h-screen bg-[#0A0A0A] text-white">
    <div class="text-center">
      <div class="mb-4">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-white mx-auto"></div>
      </div>
      <h1 class="text-xl font-semibold mb-2">로그인 처리 중...</h1>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useToast } from 'vue-toastification';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const toast = useToast();

onMounted(async () => {
  try {
    const accessToken = route.query.accessToken as string;
    const refreshToken = route.query.refreshToken as string;

    if (!accessToken || !refreshToken) {
      toast.error('인증 정보가 올바르지 않습니다.');
      router.push('/login');
      return;
    }

    // 토큰 저장 및 로그인 처리
    await authStore.socialLogin({
      accessToken,
      refreshToken
    });

    toast.success('로그인 되었습니다.');

    // 홈 페이지로 이동
    router.push('/');
  } catch (error) {
    console.error('로그인 처리 중 오류:', error);
    toast.error('로그인 처리 중 오류가 발생했습니다.');
    router.push('/login');
  }
});
</script>