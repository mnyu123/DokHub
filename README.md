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

## 주요 환경변수

| 변수 | 용도 |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | MySQL 연결 |
| `YOUTUBE_API_KEY` | YouTube Data API |
| `CHZZK_CLIENT_ID`, `CHZZK_CLIENT_SECRET` | Chzzk Open API |
| `CHZZK_NID_AUT`, `CHZZK_NID_SES` | Chzzk 채팅 로그인 |
| `CHZZK_LIVE_ENABLED`, `CHZZK_CHAT_ENABLED` | Chzzk 기능 활성화 |
| `ADMIN_API_KEY` | 캐시 관리 API의 `X-Admin-Key` 값 |
| `CORS_ALLOWED_ORIGINS` | 쉼표로 구분한 허용 프런트 도메인 |

실제 값은 Git에 커밋하지 않습니다.

## 검증

```powershell
./backend/mvnw.cmd test
cd frontend
npm run lint
npm run build
```

DB 스키마 기준은 `backend/src/main/resources/db/migration`의 Flyway 마이그레이션입니다. 기존 DB는 처음 실행할 때 현재 상태를 버전 1 베이스라인으로 등록합니다.
