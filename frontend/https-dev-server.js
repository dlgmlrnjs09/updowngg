import { createServer } from 'vite'
import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// HTTPS 인증서 설정
const httpsOptions = {
    key: fs.readFileSync('C:\\Users\\leeheekwon\\Desktop\\backup\\dev.updowngg.lol-key.pem'),
    cert: fs.readFileSync('C:\\Users\\leeheekwon\\Desktop\\backup\\dev.updowngg.lol.pem')
}

async function startDevServer() {
    console.log('🚀 HTTPS 개발 서버를 시작합니다...')
    console.log('🔧 환경: development')

    try {
        // Vite 서버 생성
        const server = await createServer({
            // Vite 설정
            configFile: path.resolve(__dirname, 'vite.config.js'),
            root: process.cwd(),
            mode: 'development',
            server: {
                https: httpsOptions,
                host: '0.0.0.0',
                port: 4173
            }
        })

        // 서버 시작
        await server.listen()

        server.printUrls()
        console.log('✅ HTTPS 개발 서버가 성공적으로 시작되었습니다!')
    } catch (error) {
        console.error('❌ 서버 시작 중 오류가 발생했습니다:', error)
        process.exit(1)
    }
}

startDevServer()