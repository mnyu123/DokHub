<template>
  <div>
    <HeaderComponent :theme="theme" @toggle-theme="toggleTheme" />
    <div class="chat-page-content">
      <!-- ChatHistory 컴포넌트를 사용하여 채팅 내역 표시 -->
      <ChatHistory />
    </div>
  </div>
</template>

<script>
import HeaderComponent from "@/components/HeaderComponent.vue";
import ChatHistory from "@/components/ChatHistory.vue";

export default {
  name: "ChatPage",
  components: {
    HeaderComponent,
    ChatHistory,
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
.chat-page-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
