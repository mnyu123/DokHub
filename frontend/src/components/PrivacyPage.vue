<template>
  <div class="container mx-auto px-6 lg:px-12 py-10 max-w-3xl space-y-6">
    <h1 class="text-3xl font-bold mb-4">독허브(DOKHUB) 개인정보처리방침</h1>

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
        사용자는 이 서비스를 이용함으로써
        YouTube 서비스 이용약관(YouTube Terms of Service) 및
        Google 개인정보처리방침(Google Privacy Policy)에 동의하게 됩니다.
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
          <strong>서버 로그 정보</strong>:
          서비스가 배포된 서버/호스팅(Netlify 등)의 웹 서버 로그를 통해
          IP 주소, 브라우저 종류, 요청 URL, 시간 정보가 자동으로 기록될 수 있습니다.
          이 정보는 장애 분석 및 보안 목적으로만 사용되며,
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
        서비스는 광고/추적 목적의 제3자 쿠키를 직접 사용하지 않으며,
        호스팅/인프라 운영자가 설정하는 최소한의 기술적 쿠키·로그 이외에는
        별도의 트래킹을 하지 않습니다.
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
          서버 로그 정보는 장애 대응, 악성 트래픽 차단, 서비스 품질 개선을 위해
          내부적으로만 사용됩니다.
        </li>
        <li>
          서비스는 사용자의 개인정보 및 YouTube API Data를
          제3자에게 판매하거나, 광고 타겟팅 목적으로 제공하지 않습니다.
        </li>
        <li>
          다만, 서비스 운영에 필요한 호스팅·인프라 제공자(Netlify, 클라우드 공급자 등)는
          인프라 운영 범위 내에서 로그·정보에 접근할 수 있습니다.
        </li>
      </ul>
    </section>

    <!-- 5. YouTube Data 갱신·보관 주기 (III.E.4a-g 대응 설명) -->
    <section class="space-y-2">
      <h2 class="text-xl font-semibold">5. YouTube 데이터의 갱신 및 보관 기간</h2>
      <p class="text-sm leading-relaxed">
        서비스는 동일한 YouTube 데이터를 장기간 보관하지 않도록
        서버 측 캐시를 사용하여 일정 주기로 데이터를 갱신합니다.
      </p>
      <ul class="list-disc list-inside text-sm leading-relaxed">
        <li>
          YouTube API Data는 서버 캐시에 보통
          <strong>최대 약 6시간</strong> 정도 보관된 후,
          새로운 API 호출 시 이전 데이터가 갱신(덮어쓰기)됩니다.
        </li>
        <li>
          이 과정에서 오래된 캐시 데이터는 자동으로 삭제되거나
          더 이상 서비스 화면에서 사용되지 않습니다.
        </li>
      </ul>
      <p class="text-xs text-gray-500">
        즉, 서비스는 YouTube API 정책에 맞추어 데이터 보관 기간을 제한하고,
        일정 주기로 데이터를 새로 가져오도록 설계되어 있습니다.
      </p>
    </section>

    <!-- 6. 사용자 권리 및 문의 -->
    <section class="space-y-2">
      <h2 class="text-xl font-semibold">6. 사용자 권리 및 문의</h2>
      <p class="text-sm leading-relaxed">
        서비스 이용에 대한 문의, 개인정보 관련 요청이 있는 경우
        아래 이메일로 연락해 주시면 가능한 범위 내에서 도움을 드립니다.
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