<!-- 메인 App.vue -->
<template>
  <div id="app" :class="themeClass">
    <HeaderComponent :theme="theme" @toggle-theme="toggleTheme" />
    <div class="container">
      <div class="row">
        <!-- 좌측 사이드바 -->
        <div class="col-12 col-md-2">
          <LeftSidebar />
        </div>
        <!-- 중앙 콘텐츠 영역 -->
        <div class="col-12 col-md-8">
          <TabNavigation :selectedTab="selectedTab" @update:tab="selectedTab = $event" />
          <LiveStatus />
          <ChannelList :selectedTab="selectedTab" :key="selectedTab" />
        </div>
        <!-- 우측 사이드바 -->
        <div class="col-12 col-md-2">
          <RightSidebar />
        </div>
      </div>
    </div>
    <FooterComponent :isDark="theme === 'dark'" />
  </div>
</template>

<script>
import HeaderComponent from "@/components/HeaderComponent.vue";
import TabNavigation from "@/components/TabNavigation.vue";
import LeftSidebar from "@/components/LeftSidebar.vue";
import RightSidebar from "@/components/RightSidebar.vue";
import LiveStatus from "@/components/LiveStatus.vue";
import ChannelList from "@/components/ChannelList.vue";
import FooterComponent from "@/components/FooterComponent.vue";

export default {
  name: "App",
  components: {
    HeaderComponent,
    TabNavigation,
    LeftSidebar,
    RightSidebar,
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
/* 전역 스타일 (기존 App.vue 스타일 일부 포함) */
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
</style>
