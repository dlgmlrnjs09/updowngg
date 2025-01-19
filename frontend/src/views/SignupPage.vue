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

          <!-- 롤 닉네임과 태그 입력 -->
          <div class="group">
            <label class="block mb-2 text-sm font-medium text-gray-300">롤 닉네임</label>
            <div class="flex gap-3">
              <div class="flex-1">
                <input
                    type="text"
                    v-model="form.riotId"
                    placeholder="게임 닉네임을 입력하세요"
                    @blur="validateRiotId"
                    class="w-full px-4 py-3 bg-[#111111] border border-[#333] rounded-md focus:border-blue-500 outline-none transition-colors text-sm"
                    :class="{ 'border-red-500': errors.riotId }"
                />
                <p v-if="errors.riotId" class="mt-1 text-xs text-red-500">{{ errors.riotId }}</p>
              </div>
              <div class="w-32">
                <div class="relative">
                  <span class="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400">#</span>
                  <input
                      type="text"
                      v-model="form.riotTag"
                      placeholder="KR1"
                      @blur="validateRiotTag"
                      class="w-full pl-8 pr-4 py-3 bg-[#111111] border border-[#333] rounded-md focus:border-blue-500 outline-none transition-colors text-sm"
                      :class="{ 'border-red-500': errors.riotTag }"
                  />
                </div>
                <p v-if="errors.riotTag" class="mt-1 text-xs text-red-500">{{ errors.riotTag }}</p>
              </div>
            </div>
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
import { authApi } from "@/api/auth.ts";
import { useToast } from 'vue-toastification'

interface SignupForm {
  email: string;
  password: string;
  passwordConfirm: string;
  riotId: string;
  riotTag: string;
}

const router = useRouter();
const isLoading = ref(false);
const toast = useToast()

const form = ref<SignupForm>({
  email: '',
  password: '',
  passwordConfirm: '',
  riotId: '',
  riotTag: '',
});

const errors = ref({
  email: '',
  password: '',
  passwordConfirm: '',
  riotId: '',
  riotTag: '',
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

const validateRiotId = () => {
  if (!form.value.riotId) {
    errors.value.riotId = '롤 닉네임을 입력해주세요.';
  } else if (form.value.riotId.length < 3 || form.value.riotId.length > 16) {
    errors.value.riotId = '닉네임은 3-16자 사이여야 합니다.';
  } else {
    errors.value.riotId = '';
  }
};

const validateRiotTag = () => {
  if (!form.value.riotTag) {
    errors.value.riotTag = '태그를 입력해주세요.';
  } else if (!/^[A-Za-z0-9]{3,5}$/.test(form.value.riotTag)) {
    errors.value.riotTag = '태그는 3-5자의 영문과 숫자로만 구성되어야 합니다.';
  } else {
    errors.value.riotTag = '';
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
  validateRiotId();
  validateRiotTag();
  validatePassword();
  validatePasswordConfirm();

  if (hasErrors.value) return;

  try {
    isLoading.value = true;
    const response = await authApi.signup({
      ...form.value,
      riotId: `${form.value.riotId}#${form.value.riotTag}`
    });

    toast.success('회원가입이 완료되었습니다! 로그인해주세요.');
    await router.push('/login');

  } catch (error: any) {
    toast.error('회원가입 중 오류가 발생했습니다.');
  } finally {
    isLoading.value = false;
  }
};
</script>