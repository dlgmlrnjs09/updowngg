import FtpDeploy from 'ftp-deploy';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import * as fs from "node:fs"; // 'require' 대신 'import' 사용

const ftpDeploy = new FtpDeploy();

// __dirname 대신 import.meta.url을 사용하여 경로 구하기
const __dirname = path.dirname(fileURLToPath(import.meta.url));

const config = {
    user: "ec2-user",     // EC2 사용자명 (Amazon Linux의 경우 'ec2-user')
    password: "",         // SFTP 연결 시 비밀번호가 필요 없으므로 비워두기
    host: "13.124.117.180",  // EC2 인스턴스의 퍼블릭 IP 주소
    port: 22,             // SFTP는 기본적으로 포트 22를 사용
    localRoot: path.join(__dirname, "/dist"),  // 로컬에서 업로드할 폴더 경로
    remoteRoot: "/var/www/html", // 원격 서버의 배포할 경로
    include: ["*", "**/*"],      // 업로드할 모든 파일
    exclude: [
        "node_modules/**",  // node_modules 폴더 제외
        "node_modules/**/.*", // node_modules 내 숨김 파일 제외
        ".git/**",           // .git 폴더 제외
    ],
    deleteRemote: false,   // 원격 서버의 기존 파일을 삭제하지 않음
    forcePasv: true,       // Passive 모드 사용
    sftp: true,            // SFTP 연결을 사용하도록 설정
    privateKey: fs.readFileSync(path.join('C:', 'Users', 'heekw', 'Desktop', 'aws server', 'updowngg.pem'))
};

ftpDeploy
    .deploy(config)
    .then((res) => console.log("배포 완료:", res))
    .catch((err) => console.log("배포 오류:", err));
