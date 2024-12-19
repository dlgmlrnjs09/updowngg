<!-- src/App.vue -->
<template>
  <Header />
  <RouterView />
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router';
import {onMounted} from 'vue'
import Header from '@/components/common/Header.vue';
import {useAuthStore} from "@/stores/auth.ts";

const auth = useAuthStore()

onMounted(async () => {
  await auth.initializeAuth();
});
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Pretendard', sans-serif;
}

body {
  background: #0A0A0A;
  color: white;
  line-height: 1.5;
}

.app {
  min-height: 100vh;
  background: #0A0A0A;
  display: flex;
  flex-direction: column; /* test 26*/
}
</style>