import express from 'express';
import https from 'https';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const port = process.env.PORT || 4173;

// HTTPS 옵션 설정
const httpsOptions = {
    key: fs.readFileSync('C:\\Users\\leeheekwon\\Desktop\\backup\\dev.updowngg.lol-key.pem'),  // 인증서 키 파일 경로로 수정
    cert: fs.readFileSync('C:\\Users\\leeheekwon\\Desktop\\backup\\dev.updowngg.lol.pem')      // 인증서 파일 경로로 수정
};

// 정적 파일 제공 (Vue 앱)
app.use(express.static(path.join(__dirname, 'dist')));

// 모든 요청을 Vue 앱으로 라우팅
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

// HTTPS 서버 시작
https.createServer(httpsOptions, app).listen(port, () => {
    console.log(`HTTPS 서버가 포트 ${port}에서 실행 중입니다. https://dev.updowngg.lol:${port}`);
});