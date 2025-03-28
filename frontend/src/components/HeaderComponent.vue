<template>
  <header :class="[headerClass, 'p-3', 'mb-3', 'd-flex', 'align-items-center', 'justify-content-between']">
    <a href="http://localhost:3000/" class="d-flex align-items-center text-decoration-none">
      <!-- 배포 시 주소는 변경 -->
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
    <div class="header-buttons">
      <!-- 독채팅 버튼 -->
      <button class="btn chat-btn" @click="goToChat">독채팅</button>
      <!-- 테마 전환 버튼 -->
      <button class="btn theme-btn" @click="$emit('toggle-theme')">
        {{ theme === 'dark' ? '라이트 모드' : '다크 모드' }}
      </button>
    </div>
  </header>
</template>

<script>
import router from '@/router'; // router 인스턴스 직접 임포트
export default {
  name: "HeaderComponent",
  props: {
    theme: {
      type: String,
      required: true,
    },
  },
  computed: {
    headerClass() {
      return this.theme === "dark" ? "custom-header-dark" : "bg-light";
    },
  },
  methods: {
    goToChat() {
      // 지정한 경로로 이동
      router.push('/api/chat/dokchat');
    },
  },
};
</script>

<style scoped>
.custom-header-dark {
  background-color: #141414; /* dark 모드 배경색 */
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
.header-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
}
.chat-btn {
  background: none;
  border: none;
  color: inherit;
  font-weight: bold;
  cursor: pointer;
}
.chat-btn:hover {
  text-decoration: underline;
}

/* 로고 애니메이션 (유지) */
.logo-anim {
  animation: fadeIn 1s ease-in-out;
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
