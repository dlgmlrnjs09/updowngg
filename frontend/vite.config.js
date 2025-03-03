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

  // 환경에 따른 호스트 이름 설정
  const hmrHost = mode === 'development' ? 'dev.updowngg.lol' : 'updowngg.lol'

  return {
    plugins: [
      vue(),
      VueDevTools(),
      {
        name: 'env-checker',
        configResolved(config) {
          console.log('=== Config Resolved ===')
          console.log('VITE_API_URL:', config.env.VITE_API_URL)
          console.log('HMR Host:', hmrHost)
          console.log('=====================')
        }
      }
    ],
    server: {
      host: '0.0.0.0',  // 모든 네트워크 인터페이스에서 수신
      port: 4173,
      hmr: {
        host: hmrHost,  // 환경에 따라 다른 호스트 사용
        protocol: 'https' ? 'wss' : 'ws'  // HTTPS 사용 여부에 따라 설정
      }
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