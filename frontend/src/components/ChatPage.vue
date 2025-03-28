<template>
    <div>
      <HeaderComponent :theme="theme" @toggle-theme="toggleTheme" />
      <div class="chat-content">
        <h2>독채팅 페이지</h2>
        <p>이곳은 독채팅 페이지입니다. 추후 기능을 추가할 예정입니다.</p>
      </div>
      <FooterComponent :isDark="theme === 'dark'" />
    </div>
  </template>
  
  <script>
  import HeaderComponent from "@/components/HeaderComponent.vue";
  import FooterComponent from "@/components/FooterComponent.vue";
  
  export default {
    name: "ChatPage",
    components: {
      HeaderComponent,
      FooterComponent,
    },
    data() {
      return {
        theme: localStorage.getItem("theme") || "dark",
      };
    },
    methods: {
      toggleTheme() {
        this.theme = this.theme === "dark" ? "light" : "dark";
        localStorage.setItem("theme", this.theme);
      },
    },
  };
  </script>
  
  <style scoped>
  .chat-content {
    min-height: 80vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    text-align: center;
  }
  </style>
  