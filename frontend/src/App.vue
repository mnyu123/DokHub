<template>
  <div id="app" :class="themeClass">

    <CelebrationPopup />

    <HeaderComponent :theme="theme" @toggle-theme="toggleTheme" />
    <!-- 라우터에 의해 콘텐츠가 변경됨 -->
    <router-view />
    <FooterComponent :isDark="theme === 'dark'" />
  </div>
</template>

<script>
import HeaderComponent from "@/components/HeaderComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import CelebrationPopup from "@/components/CelebrationPopup.vue";

export default {
  name: "App",
  components: {
    HeaderComponent,
    FooterComponent,
    CelebrationPopup,
  },
  data() {
    return {
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
/* 전역 스타일 */
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