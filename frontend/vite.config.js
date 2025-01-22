import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueDevTools from 'vite-plugin-vue-devtools'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  console.log('=== Build Environment ===')
  console.log('Mode:', mode)
  console.log('API URL:', env.VITE_API_URL)
  console.log('Current Directory:', process.cwd())
  console.log('=======================')

  return {
    plugins: [
      vue(),
      VueDevTools(),
      {
        name: 'env-checker',
        configResolved(config) {
          console.log('=== Config Resolved ===')
          console.log('VITE_API_URL:', config.env.VITE_API_URL)
          console.log('=====================')
        }
      }
    ],
    server: {
      host: '0.0.0.0',  // 모든 네트워크 인터페이스에서 수신
      port: 4173
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    define: {
      // 전역 변수로 환경변수 정의
      __API_URL__: JSON.stringify(env.VITE_API_URL)
    },
  }
})