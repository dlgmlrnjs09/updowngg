<template>
  <div class="min-h-screen bg-black text-white flex items-center justify-center p-4">
    <div class="w-full max-w-[400px]">
      <div class="p-8">
        <h1 class="text-2xl font-bold mb-2">회원가입</h1>
        <p class="text-gray-400 text-sm mb-8">updownGG와 함께하시겠습니까?</p>

        <div class="space-y-5">
          <!-- 이메일 입력 -->
          <div class="group">
            <label class="block mb-2 text-sm font-medium text-gray-300">이메일</label>
            <input
                type="email"
                v-model="form.email"
                placeholder="이메일을 입력하세요"
                @blur="validateEmail"
                class="w-full px-4 py-3 bg-[#111111] border border-[#333] rounded-md focus:border-blue-500 outline-none transition-colors text-sm"
                :class="{ 'border-red-500': errors.email }"
            />
            <p v-if="errors.email" class="mt-1 text-xs text-red-500">{{ errors.email }}</p>
          </div>

          <!-- 비밀번호 입력 -->
          <div class="group">
            <label class="block mb-2 text-sm font-medium text-gray-300">비밀번호</label>
            <input
                type="password"
                v-model="form.password"
                placeholder="비밀번호를 입력하세요"
                @blur="validatePassword"
                class="w-full px-4 py-3 bg-[#111111] border border-[#333] rounded-md focus:border-blue-500 outline-none transition-colors text-sm"
                :class="{ 'border-red-500': errors.password }"
            />
            <p v-if="errors.password" class="mt-1 text-xs text-red-500">{{ errors.password }}</p>
          </div>

          <!-- 비밀번호 확인 -->
          <div class="group">
            <label class="block mb-2 text-sm font-medium text-gray-300">비밀번호 확인</label>
            <input
                type="password"
                v-model="form.passwordConfirm"
                placeholder="비밀번호를 다시 입력하세요"
                @blur="validatePasswordConfirm"
                class="w-full px-4 py-3 bg-[#111111] border border-[#333] rounded-md focus:border-blue-500 outline-none transition-colors text-sm"
                :class="{ 'border-red-500': errors.passwordConfirm }"
            />
            <p v-if="errors.passwordConfirm" class="mt-1 text-xs text-red-500">{{ errors.passwordConfirm }}</p>
          </div>

          <button
              @click="handleSignup"
              :disabled="isLoading || hasErrors"
              class="w-full px-4 py-3 bg-blue-500 hover:bg-blue-600 text-white font-medium rounded-md transition-colors text-sm disabled:bg-gray-600 disabled:cursor-not-allowed"
          >
            {{ isLoading ? '처리중...' : '회원가입하기' }}
          </button>
        </div>

        <div class="mt-8 text-center">
          <p class="text-gray-400 text-sm">
            이미 계정이 있으신가요?
            <RouterLink to="/login" class="text-blue-500 hover:text-blue-400 ml-1">
              로그인하기
            </RouterLink>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from "@/api/axios.ts";
import {authApi} from "@/api/auth.ts";
import { useToast } from 'vue-toastification'


interface SignupForm {
  email: string;
  password: string;
  passwordConfirm: string;
}

const router = useRouter();
const isLoading = ref(false);
const toast = useToast()

const form = ref<SignupForm>({
  email: 'heekwon0867@naver.com',
  password: 'gmlrnjs0',
  passwordConfirm: 'gmlrnjs0',
});

const errors = ref({
  email: '',
  password: '',
  passwordConfirm: '',
});

const hasErrors = computed(() => {
  return Object.values(errors.value).some(error => error !== '');
});

const validateEmail = () => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!form.value.email) {
    errors.value.email = '이메일을 입력해주세요.';
  } else if (!emailRegex.test(form.value.email)) {
    errors.value.email = '올바른 이메일 형식이 아닙니다.';
  } else {
    errors.value.email = '';
  }
};

const validatePassword = () => {
  if (!form.value.password) {
    errors.value.password = '비밀번호를 입력해주세요.';
  } else if (form.value.password.length < 8) {
    errors.value.password = '비밀번호는 8자 이상이어야 합니다.';
  } else {
    errors.value.password = '';
  }

  if (form.value.passwordConfirm) {
    validatePasswordConfirm();
  }
};

const validatePasswordConfirm = () => {
  if (!form.value.passwordConfirm) {
    errors.value.passwordConfirm = '비밀번호 확인을 입력해주세요.';
  } else if (form.value.password !== form.value.passwordConfirm) {
    errors.value.passwordConfirm = '비밀번호가 일치하지 않습니다.';
  } else {
    errors.value.passwordConfirm = '';
  }
};

const handleSignup = async () => {
  validateEmail();
  validatePassword();
  validatePasswordConfirm();

  if (hasErrors.value) return;

  try {
    isLoading.value = true;
    const response = await authApi.signup(form.value);

    toast.success('회원가입이 완료되었습니다! 로그인해주세요.');
    await router.push('/login');

  } catch (error: any) {
    toast.error('회원가입 중 오류가 발생했습니다.');
  } finally {
    isLoading.value = false;
  }
};
</script>