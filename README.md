# DokHub

독케익 관련 YouTube 클립 채널, 다시보기, Chzzk 방송 상태와 채팅을 한곳에서 제공하는 서비스입니다.

## 구성

- `frontend`: Vue 3, Vue Router, Tailwind CSS, DaisyUI, Swiper
- `backend`: Java 17, Spring Boot, Spring Data JPA, Caffeine, Flyway
- 저장소: MySQL 8
- 외부 연동: YouTube Data API, Chzzk Open API 및 채팅
- 배포: Netlify 프런트엔드, Render 백엔드

## 로컬 실행

### 백엔드

필수 환경변수를 설정한 뒤 MySQL 프로필로 실행합니다.

```powershell
$env:SPRING_PROFILES_ACTIVE='mysql'
$env:DB_URL='jdbc:mysql://localhost:3306/dokhub?serverTimezone=Asia/Seoul&characterEncoding=UTF-8'
$env:DB_USERNAME='root'
$env:DB_PASSWORD='change-me'
$env:YOUTUBE_API_KEY='change-me'
$env:CHZZK_LIVE_ENABLED='false'
$env:CHZZK_CHAT_ENABLED='false'
./backend/mvnw.cmd spring-boot:run
```

Chzzk 기능을 사용할 때만 `CHZZK_CLIENT_ID`, `CHZZK_CLIENT_SECRET`, `CHZZK_NID_AUT`, `CHZZK_NID_SES`를 설정하고 두 기능 플래그를 `true`로 바꿉니다.

### 프런트엔드

```powershell
cd frontend
npm ci
npm run serve
```

개발 서버는 `/api` 요청을 `http://localhost:8080`으로 프록시합니다. 운영에서는 Netlify 프록시를 사용합니다.
