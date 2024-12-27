<template>
  <div class="min-h-screen bg-black text-white flex items-center justify-center p-4">
    <div class="w-full max-w-[400px]">
      <div class="p-8">
        <h1 class="text-2xl font-bold mb-2">로그인</h1>
        <p class="text-gray-400 text-sm mb-8">게임 전적을 확인하러 오셨나요?</p>

        <div class="space-y-5">
          <div class="group">
            <label class="block mb-2 text-sm font-medium text-gray-300">이메일</label>
            <input
                type="email"
                v-model="email"
                placeholder="이메일을 입력하세요"
                class="w-full px-4 py-3 bg-[#111111] border border-[#333] rounded-md focus:border-blue-500 outline-none transition-colors text-sm"
            />
          </div>

          <div class="group">
            <label class="block mb-2 text-sm font-medium text-gray-300">비밀번호</label>
            <input
                type="password"
                v-model="password"
                placeholder="비밀번호를 입력하세요"
                class="w-full px-4 py-3 bg-[#111111] border border-[#333] rounded-md focus:border-blue-500 outline-none transition-colors text-sm"
            />
          </div>

          <div class="flex items-center space-x-2">
            <input
                type="checkbox"
                v-model="rememberMe"
                id="remember"
                class="w-4 h-4 accent-blue-500 bg-[#111111] border-[#333]"
            />
            <label for="remember" class="text-sm text-gray-400">자동 로그인</label>
          </div>

          <button
              type="button"
              @click="handleLogin"
              class="w-full px-4 py-3 bg-blue-500 hover:bg-blue-600 text-white font-medium rounded-md transition-colors text-sm"
          >
            로그인하기
          </button>
        </div>

        <div class="mt-6 space-y-4">
          <div class="relative flex items-center gap-4">
            <div class="border-t flex-grow border-[#333]"></div>
            <span class="text-gray-500 text-sm">소셜 로그인</span>
            <div class="border-t flex-grow border-[#333]"></div>
          </div>

          <div class="flex gap-3 justify-center">
            <button class="p-2.5 bg-[#111111] rounded-md hover:bg-[#222] border border-[#333] transition-colors">
              <div class="w-5 h-5 bg-white/80"></div>
            </button>
            <button class="p-2.5 bg-[#111111] rounded-md hover:bg-[#222] border border-[#333] transition-colors">
              <div class="w-5 h-5 bg-white/80"></div>
            </button>
            <button class="p-2.5 bg-[#111111] rounded-md hover:bg-[#222] border border-[#333] transition-colors">
              <div class="w-5 h-5 bg-white/80"></div>
            </button>
          </div>
        </div>

        <div class="mt-8 text-center">
          <p class="text-gray-400 text-sm">
            아직 계정이 없으신가요?
            <router-link to="/signup" class="text-blue-500 hover:text-blue-400 ml-1">가입하기</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue'
import {authApi} from "@/api/auth.ts";
import type {LoginCredentials} from "@/types/auth.ts";
import {useAuthStore} from "@/stores/auth.ts";
import {useToast} from "vue-toastification";
import {useRouter} from "vue-router";

const email = ref('heekwon0867@naver.com')
const password = ref('gmlrnjs0')
const rememberMe = ref(false)
const authStore = useAuthStore()
const toast = useToast()
const router = useRouter()

const handleLogin = async () => {
  try {
    const credentials: LoginCredentials = {
      email: email.value,
      password: password.value
    };

    await authApi.login(credentials);
    const response = await authStore.login(credentials)

    if (!response) {
      toast.error('로그인에 실패했습니다.');
    } else {
      toast.success(credentials.email + ' 님, 환영합니다!')
      await router.push('/')
    }
  } catch (error) {
    console.log(import.meta.env.VITE_API_URL)
    console.log('로그인 실패:,', error);
    toast.error('로그인에 실패했습니다.');
    return false;
  }
}
</script>