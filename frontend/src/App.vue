<template>
  <!-- 최상위 div에 themeClass 적용 -->
  <div id="app" :class="themeClass">
    <!-- 헤더: 애니메이션 효과와 부드러운 전환 -->
    <header :class="[headerClass, 'p-3', 'mb-3', 'd-flex', 'align-items-center', 'justify-content-between', 'header-anim']">
      <!-- 로고와 텍스트 -->
      <div class="d-flex align-items-center">
        <img
          src="@/assets/doksaem6.png"
          alt="독쌤 캐릭터"
          style="max-height: 80px;"
          class="img-fluid me-3 logo-anim"
        />
        <!-- 제목과 부제목 분리 -->
        <div class="header-text">
          <h1 class="main-title m-0">독 허 브</h1>
          <p class="sub-title m-0">- 유튜브에 있는 모든 독쌤 키리누키 영상을 한번에!</p>
        </div>
      </div>
      <!-- 테마 전환 버튼 -->
      <button class="btn theme-btn" @click="toggleTheme">
        {{ theme === 'dark' ? '라이트 모드' : '다크 모드' }}
      </button>
    </header>

    <!-- 좌/중앙/우 레이아웃 -->
    <div class="container">
      <div class="row">
        <!-- 좌측 GIF -->
        <div class="col-12 col-md-2 text-center mb-3 mb-md-0">
          <img
            src="@/assets/right.gif"
            alt="좌측GIF"
            class="img-fluid left-gif"
          />
        </div>

        <!-- 중앙 콘텐츠 (ChannelList 컴포넌트) -->
        <div class="col-12 col-md-8">
          <nav class="mb-3 d-flex justify-content-center align-items-center tab-nav">
            <button class="tab-btn me-2" :class="{ active: selectedTab === 'clip' }" @click="selectedTab = 'clip'">
              클립
            </button>
            <button class="tab-btn me-2" :class="{ active: selectedTab === 'song' }" @click="selectedTab = 'song'">
              노래
            </button>
            <button class="tab-btn" :class="{ active: selectedTab === 'main' }" @click="selectedTab = 'main'">
              본채널
            </button>
          </nav>
          <ChannelList :selectedTab="selectedTab" :key="selectedTab" />
        </div>

        <!-- 우측 GIF와 업데이트 박스 -->
        <div class="col-12 col-md-2 text-center mt-3 mt-md-0">
          <img
            src="@/assets/chzzkleft.gif"
            alt="우측GIF"
            class="img-fluid right-gif"
          />
          <div class="update-box mt-3 p-3">
            <h2 class="update-title">v0.6 업데이트 내역</h2>
            <ul class="update-list">
              <li>화면 애니메이션 추가</li>
              <li>오타 수정 / 움짤 교체</li>
              <li>페이지 넘김후 상단 고정</li>
              <li>채널 개수 표시</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- 푸터 (다크 모드 여부 prop 전달) -->
    <FooterComponent :isDark="theme === 'dark'" />
  </div>
</template>

<script>
import FooterComponent from "@/components/FooterComponent.vue";
import ChannelList from "./components/ChannelList.vue";

export default {
  name: "App",
  components: {
    FooterComponent,
    ChannelList,
  },
  data() {
    return {
      selectedTab: "clip",
      // 로컬 스토리지에서 테마 복원, 기본은 다크 모드
      theme: localStorage.getItem("theme") || "dark",
    };
  },
  computed: {
    themeClass() {
      // 다크/라이트 테마에 따른 전체 배경 클래스
      return this.theme === "dark" ? "bg-dark text-white" : "bg-light text-dark";
    },
    headerClass() {
      // 수정사항 1: 헤더 배경을 옅은 검은색(#2a2a2a)으로 지정 (다크 모드일 때)
      return this.theme === "dark" ? "custom-header-dark" : "bg-light";
    },
  },
  methods: {
    toggleTheme() {
      this.theme = this.theme === "dark" ? "light" : "dark";
      localStorage.setItem("theme", this.theme);
    },
  },
};
</script>

<!-- 글로벌 스타일은 scoped 없이 적용 -->
<style>
/* 전역/공용 스타일 - 폰트가 전역에 적용되도록 */
body, html {
  margin: 0;
  padding: 0;
  font-family: 'Jua', sans-serif;
}

/* 다크 테마 그라데이션 배경 (상단 옅은 빨강 → 하단 검정) */
.bg-dark {
  background: linear-gradient(to bottom, #ffcccc, #141414);
}

/* light 모드 배경 */
.bg-light {
  background-color: #f8f9fa;
}

/* 헤더 애니메이션 */
.header-anim {
  animation: slideDown 0.8s ease-out;
  transition: background-color 0.3s ease;
}
@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 로고 애니메이션 (페이드인) */
.logo-anim {
  animation: fadeIn 1s ease-in-out;
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* 헤더 텍스트 */
.header-text {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.main-title {
  font-size: 1.8rem;
  font-weight: bold;
}
.sub-title {
  font-size: 1rem;
  color: #ccc; /* 부제목은 약간 연하게 */
  margin-top: -5px;
}

/* custom-header-dark: 옅은 검은색 배경으로 설정 */
.custom-header-dark {
  background-color: #2a2a2a;
}

/* 테마 전환 버튼 */
.theme-btn {
  transition: background-color 0.3s ease, color 0.3s ease;
  /* 기본 스타일 초기화 */
  background-color: transparent;
  border: 1px solid currentColor;
  color: inherit;
}

/* 탭 내비게이션 및 버튼 (수정사항 3) */
.tab-nav {
  gap: 10px;
}
.tab-btn {
  padding: 8px 16px;
  border: none;
  background-color: transparent;
  color: inherit;
  font-weight: bold;
  border-radius: 4px;
  transition: background-color 0.3s ease, transform 0.3s ease;
}
.tab-btn:hover {
  background-color: rgba(13, 110, 253, 0.2);
  transform: scale(1.05);
}
.tab-btn.active {
  background-color: #0d6efd;
  color: white;
  transform: scale(1.05);
}

/* 업데이트 박스 */
.update-box {
  background-color: #141414;
  color: #fff;
  border: 1px solid #444;
  border-radius: 8px;
  transition: transform 0.3s ease;
}
.update-box:hover {
  transform: translateY(-5px);
}
.update-title {
  font-size: 1.2rem;
  margin-bottom: 10px;
}
.update-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

/* 좌/우 GIF 호버 효과 */
.left-gif, .right-gif {
  transition: transform 0.3s ease;
}
.left-gif:hover, .right-gif:hover {
  transform: scale(1.05);
}
</style>
