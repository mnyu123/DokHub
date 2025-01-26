<template>
  <!-- 최상위 div :class="themeClass" 사용 -->
  <div id="app" :class="themeClass">
    <!-- 헤더 -->
    <header :class="[headerClass, 'p-3', 'mb-3', 'd-flex', 'align-items-center', 'justify-content-between']">
      <!-- 로고/아이콘 -->
      <div class="d-flex align-items-center">
        <img
          src="@/assets/doksaem.png"
          alt="독쌤 캐릭터"
          style="max-height: 80px;"
          class="img-fluid me-3"
        />
        <!-- 텍스트 (독 허 브) -->
        <h1 class="m-0" style="font-size: 1.5rem;">독 허 브</h1>
      </div>

      <!-- 라이트 / 다크 모드 전환 버튼 -->
      <button class="btn btn-outline-secondary" @click="toggleTheme">
        {{ theme === 'dark' ? '라이트 모드' : '다크 모드' }}
      </button>
    </header>

    <!-- 좌 / 중앙 / 우 레이아웃 -->
    <div class="container">
      <div class="row">
        <!-- 좌측 GIF -->
        <div class="col-12 col-md-2 text-center mb-3 mb-md-0">
          <img
            src="@/assets/newleft.gif"
            alt="좌측GIF"
            class="img-fluid"
          />
        </div>

        <!-- 중앙 콘텐츠 (ChannelList) -->
        <div class="col-12 col-md-8">
          <nav class="mb-3">
            <button
              class="btn btn-primary me-2"
              @click="selectedTab = 'clip'"
            >
              클립
            </button>
            <button
              class="btn btn-success me-2"
              @click="selectedTab = 'song'"
            >
              노래
            </button>
            <button
              class="btn btn-warning"
              @click="selectedTab = 'main'"
            >
              본채널
            </button>
          </nav>

          <ChannelList
            :selectedTab="selectedTab"
            :key="selectedTab"
          />
        </div>

        <!-- 우측 GIF -->
        <div class="col-12 col-md-2 text-center mt-3 mt-md-0">
          <img
            src="@/assets/right.gif"
            alt="우측GIF"
            class="img-fluid"
          />
          <div class="update-box mt-3 p-3 text-white bg-secondary rounded">
            <h2>v0.4 업데이트 내역</h2>
            <ul>
              <li>신규 키리누키 추가</li>
              <li>본채널 추가</li>
              <li>채널이름 최신순,이름순 정렬</li>
              <li>채널별 최신영상 3개 조회</li>
              <li>움짤바꾼건 귀여워서 넣었습니다.[진지]</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- 푸터 -->
    <!-- 다크 모드 여부를 prop으로 전달 -->
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
      theme: localStorage.getItem("theme") || "dark", // 초기값 복원
    };
  },
  computed: {
    themeClass() {
      return this.theme === "dark" ? "bg-dark text-white" : "bg-white";
    },
    headerClass() {
      return this.theme === "dark" ? "bg-secondary" : "bg-light";
    },
  },
  methods: {
    toggleTheme() {
      this.theme = this.theme === "dark" ? "light" : "dark";
      localStorage.setItem("theme", this.theme); // 테마 상태 저장
    },
  },
};
</script>

<style>
/* 여기에 Bootstrap 클래스로도 커버 안 되는 부분을 추가로 커스텀 CSS */
/* 전역/공용 스타일 */
body, html {
  margin: 0;
  padding: 0;
  font-family: 'Jua', sans-serif !important;
}
.update-box {
  background-color: #343a40;
  border: 1px solid #ccc;
  border-radius: 8px;
  margin-right: -20px;  /* 오른쪽으로 20px 밀어냄 */
  width: calc(100% + 20px); /* 너비 보정 */
}
</style>