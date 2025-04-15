<template>
  <div id="app" :class="themeClass">
    <!-- 팝업 컴포넌트: 페이지 로드 시 오버레이 팝업으로 표시 -->
    <CelebrationPopup />

    <!-- 기존 헤더 영역 -->
    <header :class="[headerClass, 'p-3', 'mb-3', 'd-flex', 'align-items-center', 'justify-content-between', 'header-anim']">
      <a href="https://dokhub-love-doksaem.netlify.app/" class="d-flex align-items-center text-decoration-none">
        <img
          src="@/assets/dokhublogo.png"
          alt="독허브 로고"
          style="max-height: 80px;"
          class="img-fluid me-3 logo-anim"
        />
        <div class="header-text">
          <h1 class="main-title m-0">독 허 브</h1>
          <p class="sub-title m-0">- 개떡이들을 위한 독케익 모아보기 사이트</p>
        </div>
      </a>
      <button class="btn theme-btn" @click="toggleTheme">
        {{ theme === 'dark' ? '라이트 모드' : '다크 모드' }}
      </button>
    </header>

    <!-- 기존 컨텐츠 영역 -->
    <div class="container">
      <div class="row">
        <!-- 좌측 GIF -->
        <div class="col-12 col-md-2 text-center mb-3 mb-md-0">
          <a href="https://chzzk.naver.com/b68af124ae2f1743a1dcbf5e2ab41e0b" target="_blank">
            <img
              src="@/assets/right.gif"
              alt="좌측GIF"
              class="img-fluid left-gif"
            />
          </a>
        </div>

        <!-- 중앙 콘텐츠 (채널 리스트 등) -->
        <div class="col-12 col-md-8">
          <nav class="mb-3 d-flex justify-content-center align-items-center tab-nav">
            <button class="tab-btn me-2" 
                    :class="{ active: selectedTab === 'clip' }" 
                    @click="selectedTab = 'clip'">
              클립(활성화)
            </button>
            <button class="tab-btn me-2" 
                    :class="{ active: selectedTab === 'stclip' }" 
                    @click="selectedTab = 'stclip'">
              클립(중단)
            </button>
            <button class="tab-btn me-2" 
                    :class="{ active: selectedTab === 'replay' }" 
                    @click="selectedTab = 'replay'">
              리플레이
            </button>
            <button class="tab-btn me-2" 
                    :class="{ active: selectedTab === 'song' }" 
                    @click="selectedTab = 'song'">
              노래
            </button>
            <button class="tab-btn" 
                    :class="{ active: selectedTab === 'main' }" 
                    @click="selectedTab = 'main'">
              본채널
            </button>
          </nav>
          <LiveStatus />
          <ChannelList :selectedTab="selectedTab" :key="selectedTab" />
        </div>

        <!-- 우측 GIF 및 업데이트 박스 -->
        <div class="col-12 col-md-2 text-center mt-3 mt-md-0">
          <img
            src="@/assets/bigcake.gif"
            alt="우측GIF"
            class="img-fluid right-gif"
          />
          <div class="update-box mt-3 p-3">
            <h2 class="update-title">v0.8 업데이트 내역</h2>
            <ul class="update-list">
              <li>아이콘 변경</li>
              <li>아이콘 클릭시 독허브 사이트 이동</li>
              <li>카테고리 변경 (클립(활성화), 클립(중단), 리플레이, 노래, 본채널)</li>
              <li>독쌤 방송중인지 확인 가능</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- 기존 푸터 영역 -->
    <FooterComponent :isDark="theme === 'dark'" />
  </div>
</template>

<script>
import LiveStatus from "@/components/LiveStatus.vue";
import ChannelList from "@/components/ChannelList.vue";
import FooterComponent from "@/components/FooterComponent.vue";

export default {
  name: "App",
  components: {
    LiveStatus,
    ChannelList,
    FooterComponent,
  },
  data() {
    return {
      selectedTab: "clip",
      theme: localStorage.getItem("theme") || "dark",
    };
  },
  computed: {
    themeClass() {
      return this.theme === "dark" ? "bg-dark text-white" : "bg-light text-dark";
    },
    headerClass() {
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

<style>
/* 전역 스타일 (기존 App.vue 스타일 유지) */
body, html {
  margin: 0;
  padding: 0;
  font-family: 'Jua', sans-serif;
}
.bg-dark {
  background: linear-gradient(to bottom, #ffcccc, #141414);
}
.bg-light {
  background-color: #f8f9fa;
}
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
.logo-anim {
  animation: fadeIn 1s ease-in-out;
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
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
  color: #ccc;
  margin-top: -5px;
}
.custom-header-dark {
  background-color: #2a2a2a;
}
.theme-btn {
  transition: background-color 0.3s ease, color 0.3s ease;
  background-color: transparent;
  border: 1px solid currentColor;
  color: inherit;
}
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
.left-gif, .right-gif {
  transition: transform 0.3s ease;
}
.left-gif:hover, .right-gif:hover {
  transform: scale(1.05);
}
</style>
