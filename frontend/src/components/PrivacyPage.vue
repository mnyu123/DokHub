<template>
  <div class="container mx-auto px-6 lg:px-12 py-10 max-w-3xl space-y-6">
    <h1 class="text-3xl font-bold mb-4">독허브(DOKHUB) 개인정보처리방침</h1>
    <p class="text-sm opacity-70">시행일: 2026년 10월 19일</p>

    <!-- 1. 개요 -->
    <section class="space-y-2">
      <h2 class="text-xl font-semibold">1. 개요</h2>
      <p class="text-sm leading-relaxed">
        본 개인정보처리방침은 독허브(DOKHUB, 이하 “서비스”)가
        어떤 정보를 수집·이용·보관·공유하는지,
        그리고 YouTube API Services를 어떻게 사용하는지를 설명합니다.
      </p>
    </section>

    <!-- 2. YouTube API Services 사용 고지 (III.A.2b) -->
    <section class="space-y-2">
      <h2 class="text-xl font-semibold">2. YouTube API Services 사용</h2>
      <p class="text-sm leading-relaxed">
        이 서비스는 Google이 제공하는
        <strong>YouTube API Services</strong>를 사용하여
        공개된 동영상, 채널, 재생목록 정보를 조회합니다.
      </p>
      <p class="text-sm leading-relaxed">
        서비스 이용에는 YouTube 서비스 이용약관(YouTube Terms of Service)이 적용됩니다.
        Google의 정보 처리 방식은 Google 개인정보처리방침(Google Privacy Policy)에서 확인할 수 있습니다.
      </p>
      <ul class="list-disc list-inside text-sm leading-relaxed">
        <li>
          YouTube 서비스 이용약관:
          <a
            href="https://www.youtube.com/t/terms"
            target="_blank"
            rel="noopener noreferrer"
            class="underline"
          >https://www.youtube.com/t/terms</a>
        </li>
        <li>
          Google 개인정보처리방침:
          <a
            href="https://policies.google.com/privacy"
            target="_blank"
            rel="noopener noreferrer"
            class="underline"
          >https://policies.google.com/privacy</a>
        </li>
      </ul>
    </section>

    <!-- 3. 수집하는 정보 (III.A.2d,g) -->
    <section class="space-y-2">
      <h2 class="text-xl font-semibold">3. 수집·저장되는 정보의 종류</h2>
      <p class="text-sm leading-relaxed">
        서비스는 다음과 같은 정보를 처리할 수 있습니다.
      </p>
      <ul class="list-disc list-inside text-sm leading-relaxed">
        <li>
          <strong>YouTube API Data</strong>:
          동영상 ID, 채널 ID, 썸네일 URL, 제목, 설명, 공개일시 등
          YouTube API를 통해 제공되는 공개 메타데이터.
        </li>
        <li>
          <strong>영상 링크 클릭 기록</strong>:
          클릭한 영상의 ID·제목, 분류, 채널명·채널 ID, 클릭 시각을
          서버 데이터베이스에 저장합니다. 클릭 기록에는 방문자의 이름, 계정 ID, IP 주소를 저장하지 않습니다.
        </li>
        <li>
          <strong>치지직 채팅 기록</strong>:
          독케익님의 채팅 내용, 서비스에서 기록한 시각, 관련 채널 ID를
          서버 데이터베이스에 저장하며, 채팅 내용이 서버 운영 로그에도 기록될 수 있습니다.
        </li>
        <li>
          <strong>서버 로그 정보</strong>:
          서비스가 배포된 서버/호스팅(Netlify, Render 등)의 웹 서버 로그를 통해
          IP 주소, 브라우저 종류, 요청 URL, 시간 정보가 자동으로 기록될 수 있습니다.
          또한 과도한 요청을 제한하기 위해 IP 주소에 기반한 요청 빈도 정보를 서버 메모리에서 처리합니다.
          이 정보는 장애 분석, 요청 제한 및 보안 목적으로 사용되며,
          서비스 제공자가 별도로 사용자 프로파일링을 위해 가공하지 않습니다.
        </li>
        <li>
          <strong>기기 저장소(Local Storage 등)</strong>:
          사용자 편의를 위해 브라우저 <code>localStorage</code> 에
          테마(다크/라이트) 설정 값 등을 저장할 수 있습니다.
          이는 서비스 화면 모드를 기억하는 용도로만 사용됩니다.
        </li>
      </ul>
      <p class="text-xs text-gray-500">
        서비스는 영상 링크의 클릭을 집계합니다. 방문자별 계정을 만들거나
        개인별 시청 이력을 구성하는 용도로 사용하지 않으며,
        광고 추적 목적의 제3자 쿠키를 직접 설정하지 않습니다.
      </p>
    </section>

    <!-- 4. 정보 이용·처리·공유 방식 (III.A.2e) -->
    <section class="space-y-2">
      <h2 class="text-xl font-semibold">4. 정보의 이용, 처리 및 공유</h2>
      <ul class="list-disc list-inside text-sm leading-relaxed">
        <li>
          YouTube API Data는
          독허브 화면에서 클립 목록, 리플레이 목록, 채널 정보 등을 표시하기 위해 사용됩니다.
        </li>
        <li>
          영상 링크 클릭 기록은 인기 영상·채널과 날짜별 클릭 흐름을 집계하는 데 사용됩니다.
          집계 결과는 클릭 인사이트 화면과 공개 API에 제공됩니다.
          이 통계는 독허브에서 발생한 클릭에 관한 자체 통계이며, YouTube의 조회수나 실제 시청 횟수가 아닙니다.
        </li>
        <li>
          치지직 채팅 내용은 독채팅 화면과 공개 API에서 로그인 없이 조회할 수 있습니다.
          화면과 API에서는 최근 100개의 기록을 제공하며, 이 조회 개수는 데이터베이스의 보관 개수 제한을 뜻하지 않습니다.
        </li>
        <li>
          서버 로그 정보는 장애 대응, 악성 트래픽 차단, 서비스 품질 개선을 위해
          내부적으로만 사용됩니다.
        </li>
        <li>
          서비스는 사용자의 개인정보 및 YouTube API Data를
          제3자에게 판매하거나, 광고 타겟팅 목적으로 제공하지 않습니다.
        </li>
        <li>
          서비스 운영에 사용하는 Netlify(프런트 호스팅), Render(서버 호스팅),
          Aiven(데이터베이스) 등 인프라 제공자는 서비스 제공 범위에서 로그·저장 정보를 처리할 수 있습니다.
        </li>
        <li>
          썸네일 등 외부 콘텐츠를 불러오거나 YouTube·치지직 링크로 이동하면
          해당 제공자에게 IP 주소 등 접속 정보가 전달될 수 있으며, 각 제공자의 개인정보처리방침이 적용됩니다.
        </li>
      </ul>
    </section>

    <!-- 5. YouTube Data 갱신·보관 주기 (III.E.4a-g 대응 설명) -->
    <section class="space-y-2">
      <h2 class="text-xl font-semibold">5. 정보의 갱신 및 보관 기간</h2>
      <p class="text-sm leading-relaxed">
        조회 성능을 위한 임시 캐시와 데이터베이스에 저장하는 기록은 보관 방식이 다릅니다.
      </p>
      <ul class="list-disc list-inside text-sm leading-relaxed">
        <li>
          <strong>YouTube 조회 캐시</strong>:
          영상·썸네일·재생목록 등의 조회 결과는 서버 임시 캐시에 최대 약 6시간 보관되며,
          만료 후 다음 조회 시 API를 통해 다시 가져옵니다.
        </li>
        <li>
          <strong>데이터베이스 기록</strong>:
          등록된 채널 정보, 영상 링크 클릭 기록과 그 안의 영상 제목·채널 정보,
          치지직 채팅 기록은 현재 자동 만료가 적용되지 않으며,
          삭제 요청 처리 또는 운영상 정리 시까지 보관됩니다.
          임시 캐시의 6시간 만료는 이 기록의 삭제를 의미하지 않습니다.
        </li>
        <li>
          <strong>서버·호스팅 로그</strong>:
          각 인프라 제공자의 로그 보관 설정과 정책에 따라 보관됩니다.
        </li>
        <li>
          <strong>브라우저 테마 설정</strong>:
          사용자가 브라우저의 사이트 데이터를 삭제할 때까지 해당 브라우저에 보관됩니다.
        </li>
      </ul>
      <p class="text-xs text-gray-500">
        보관 기간이나 처리 방식이 변경되면 이 방침을 갱신하고 서비스 화면에 안내합니다.
      </p>
    </section>

    <!-- 6. 사용자 권리 및 문의 -->
    <section class="space-y-2">
      <h2 class="text-xl font-semibold">6. 사용자 권리 및 문의</h2>
      <p class="text-sm leading-relaxed">
        저장된 정보의 열람·정정·삭제를 요청하거나 공개 중인 채팅 기록에 관해 문의하려면
        아래 이메일로 연락해 주세요. 처리 대상을 확인할 수 있도록 영상·채널 ID,
        메시지 내용 또는 대략적인 기록 시각을 함께 알려 주세요.
      </p>
      <p class="text-sm leading-relaxed">
        요청은 가능한 한 신속하게 처리하며, YouTube API 관련 데이터의 유효한 삭제 요청은
        접수 후 7일 이내 처리합니다. 본인 또는 권한 확인이 필요한 경우 최소한의 추가 정보를 요청할 수 있습니다.
        클릭 기록에는 방문자 식별 정보가 없으므로 특정 방문자의 기록을 구분하기 어려울 수 있습니다.
      </p>
      <p class="text-sm leading-relaxed">
        독허브에 저장된 기록을 삭제해도 YouTube·치지직의 원본 콘텐츠는 삭제되지 않습니다.
        원본의 삭제·변경은 해당 플랫폼에서 진행해야 합니다.
      </p>
      <p class="text-sm leading-relaxed">
        이메일:
        <a href="mailto:roqkfejrdl@gmail.com" class="underline">
          roqkfejrdl@gmail.com
        </a>
      </p>
    </section>

    <!-- 7. 정책 변경 -->
    <section class="space-y-2">
      <h2 class="text-xl font-semibold">7. 개인정보처리방침 변경</h2>
      <p class="text-sm leading-relaxed">
        본 개인정보처리방침은 관련 법령 및 서비스 변경에 따라 개정될 수 있으며,
        중요한 변경 사항은 서비스 화면 또는 공지 등을 통해 안내합니다.
      </p>
    </section>
  </div>
</template>

<script setup>
</script>
