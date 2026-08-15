# DokHub 운영 배포 체크리스트

## 1. 노출된 인증정보 교체

과거 Git 이력에 들어간 값은 현재 파일에서 제거했더라도 이미 노출된 것으로 간주합니다.

- YouTube Data API 키 폐기 후 새 키 발급
- Chzzk Client ID/Secret 재발급
- NID_AUT, NID_SES 세션 폐기 후 새 세션 발급
- MySQL 사용자 암호 변경
- Git 이력을 `git filter-repo` 또는 BFG로 정리한 뒤 원격 저장소 강제 푸시

이력 재작성 전에는 저장소 백업과 협업자 공지가 필요합니다.

## 2. Render 백엔드 환경변수

다음 값을 Render의 Secret/Environment 설정에 등록합니다.

- `DB_URL`: `jdbc:mysql://.../dokhub?...` 형식
- `DB_USERNAME`
- `DB_PASSWORD`
- `YOUTUBE_API_KEY`
- `CHZZK_CLIENT_ID`
- `CHZZK_CLIENT_SECRET`
- `CHZZK_NID_AUT`
- `CHZZK_NID_SES`
- `CHZZK_LIVE_ENABLED=true`
- `CHZZK_CHAT_ENABLED=true`
- `ADMIN_API_KEY`: 충분히 긴 임의 문자열
- `CORS_ALLOWED_ORIGINS`: 실제 프런트 도메인을 쉼표로 구분

Docker 이미지는 `prod` 프로필로 실행하며 Render가 제공하는 `PORT` 값을 자동으로 사용합니다.

## 3. 데이터베이스

1. 운영 DB를 먼저 백업합니다.
2. 제공된 세 테이블이 `V1__baseline.sql`과 일치하는지 확인합니다.
3. 애플리케이션 DB 사용자에게 테이블 조회 권한과 `flyway_schema_history` 생성 권한을 부여합니다.
4. 첫 배포 로그에서 Flyway baseline 등록과 Hibernate validation 성공을 확인합니다.

기존 비어 있지 않은 DB는 버전 1로 baseline 처리되며 기존 테이블을 다시 만들지 않습니다.

## 4. Netlify

- Base directory: `frontend`
- Build command: `npm run build`
- Publish directory: `dist`
- `/api/*` 프록시 대상이 현재 Render 백엔드 주소인지 확인
- 커스텀 도메인을 사용하면 해당 도메인을 `CORS_ALLOWED_ORIGINS`에도 추가

프런트엔드는 기본적으로 같은 도메인의 `/api`를 사용하므로 `VUE_APP_API_BASE_URL` 설정은 필요하지 않습니다.

## 5. 배포 후 확인

- `GET /api/health`가 `status: ok` 반환
- 홈의 각 채널 탭과 다음 페이지 동작
- AI 채널 활동 요약 표시
- 다시보기 목록 표시
- 방송 ON/OFF 상태 전환
- 독채팅 DB 저장 및 조회
- 영상 클릭 시 `video_click_log` 적재
- 관리 캐시 API가 키 없이 401/503을 반환하고 올바른 `X-Admin-Key`에서만 동작
