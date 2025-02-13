<template>
  <div class="min-h-[50rem] bg-[#0A0A0A] text-white flex flex-col">
    <!-- 로그인 카드 컨테이너 -->
    <div class="flex-1 flex items-center justify-center p-4">
      <div class="w-full max-w-[460px] mx-4">
        <div class="bg-[#141414] rounded-2xl border border-[#333] p-8 sm:p-10">
          <!-- 로고 영역 -->
          <div class="flex flex-col items-center gap-6 mb-10">
            <div class="w-[72px] h-[72px] rounded-xl overflow-hidden">
              <img src="/favicon.ico" alt="logo" class="w-full h-full object-cover"/>
            </div>
            <h1 class="text-2xl sm:text-3xl font-bold">updownGG</h1>
          </div>

          <div class="space-y-6">
            <!-- 라이엇 시작하기 버튼 -->
            <button class="w-full flex items-center justify-center gap-3 px-4 py-3.5 bg-[#D13639] hover:bg-[#B22B2E] border border-[#ea443530] rounded-lg transition-all text-sm font-medium group">
              <img src="/src/assets/icon/riot_logo.svg" alt="Riot" class="w-5 h-5 group-hover:scale-110 transition-transform"/>
              <span>라이엇 계정으로 시작하기</span>
            </button>

            <div class="relative flex items-center gap-3 py-2">
              <div class="border-t flex-grow border-[#333]"></div>
              <span class="text-gray-500 text-sm">or login with</span>
              <div class="border-t flex-grow border-[#333]"></div>
            </div>

            <!-- 로그인 옵션 -->
            <div class="grid grid-cols-3 gap-3">
              <button class="flex items-center justify-center p-3 bg-[#D13639] hover:bg-[#B22B2E] border border-[#ea443530] rounded-lg transition-all group">
                <img src="/src/assets/icon/riot_logo.svg" alt="Riot" class="w-6 h-6 group-hover:scale-110 transition-transform"/>
              </button>

              <button
                  @click="handleDiscordLogin"
                  class="flex items-center justify-center p-3 bg-[#5865F2] hover:bg-[#4752C4] rounded-lg transition-all group"
              >
                <img src="/src/assets/icon/discord_logo.svg" alt="Discord" class="w-6 h-6 group-hover:scale-110 transition-transform"/>
              </button>

              <button class="flex items-center justify-center p-3 bg-white hover:bg-gray-100 rounded-lg transition-all group">
                <svg class="w-6 h-6 group-hover:scale-110 transition-transform" viewBox="0 0 24 24">
                  <path fill="#EA4335" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                  <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                  <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                  <path fill="#4285F4" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                </svg>
              </button>
            </div>
          </div>

          <div class="mt-8 text-center text-xs sm:text-sm text-gray-400">
            <p>
              로그인 시
              <a href="/terms" class="text-[#2979FF] hover:underline">이용약관</a>과
              <a href="/privacy" class="text-[#2979FF] hover:underline">개인정보처리방침</a>에 동의하게 됩니다.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import {authApi} from "@/api/auth.ts";
  import {useAuthStore} from "@/stores/auth.ts";
  import {useToast} from "vue-toastification";
  import {useRouter} from "vue-router";

  const authStore = useAuthStore();
  const toast = useToast();
  const router = useRouter();


  const handleDiscordLogin = async () => {
    /*const response = await authApi.loginDiscord()
    const data = response.data;
    const loginResponse = await authStore.socialLogin(data)
    if (!loginResponse) {
      toast.error('로그인에 실패했습니다.');
    } else {
      toast.success('환영합니다!')
      await router.push('/')
    }*/
    /*const response = await authApi.loginDiscord()*/
    const redirectUri = `${window.location.origin}/oauth2/discord/login`;
    window.location.href = `${import.meta.env.VITE_API_URL}/oauth2/authorization/discord?redirect_uri=${encodeURIComponent(redirectUri)}`;
  }
</script>