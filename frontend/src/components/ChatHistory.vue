<template>
    <div class="chat-history-container">
      <!-- 새로고침 버튼 및 요청 횟수 표시 -->
      <div class="refresh-button-container">
        <button class="refresh-btn" @click="refreshChat">새로고침</button>
        <span class="refresh-count">({{ refreshCount }}/{{ refreshLimit }})</span>
      </div>
  
      <!-- 로딩 상태일 때 -->
      <div v-if="loading" class="loading-container">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p>채팅 내역을 불러오는 중입니다...</p>
      </div>
  
      <!-- 채팅 메시지 영역 -->
      <div v-else class="chat-messages">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          class="chat-message"
        >
          {{ msg }}
        </div>
      </div>
  
      <!-- 채팅 내역이 없거나 부족한 경우 안내 메시지 -->
      <div v-if="messages.length === 0" class="alert-message">
        채팅 내역이 없습니다. 새로고침을 해보세요.
      </div>
      <div v-else-if="messages.length > 0 && messages.length < 5" class="alert-message">
        채팅 내용이 부족합니다. 새로고침을 해보세요.
      </div>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    name: "ChatHistory",
    data() {
      return {
        messages: [],
        loading: true,
        refreshCount: 0,
        refreshLimit: 5,
      };
    },
    async mounted() {
      await this.fetchChatHistory();
    },
    methods: {
      async fetchChatHistory() {
        this.loading = true;
        try {
          const response = await axios.get("/api/chat/history");
          this.messages = response.data;
        } catch (error) {
          console.error("채팅 내역 로드 실패:", error);
        } finally {
          this.loading = false;
        }
      },
      async refreshChat() {
        if (this.refreshCount >= this.refreshLimit) {
          alert("새로고침 요청 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요.");
          return;
        }
        this.refreshCount++;
        await this.fetchChatHistory();
      },
    },
  };
  </script>
  
  <style scoped>
  /* 내장 애니메이션 키프레임 정의 */
  @keyframes fadeInEffect {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }
  
  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  
  /* 채팅 내역 컨테이너 스타일 */
  .chat-history-container {
    padding: 20px;
    width: 100%;
    max-width: 800px;
    margin: auto;
    /* 연한 빨강과 검정의 그라데이션 적용 */
    background: linear-gradient(135deg, #ff9999, #141414);
    border-radius: 10px;
    color: #fff;
    animation: fadeInEffect 2s ease-in-out;
  }
  
  .refresh-button-container {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin-bottom: 10px;
  }
  
  .refresh-btn {
    background-color: #ff9999;
    color: #fff;
    padding: 8px 16px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .refresh-btn:hover {
    background-color: #e08888;
  }
  
  .refresh-count {
    margin-left: 8px;
    font-size: 0.9rem;
  }
  
  .loading-container {
    text-align: center;
    margin: 20px;
  }
  
  .chat-messages {
    display: flex;
    flex-direction: column;
    gap: 10px;
    max-height: 500px;
    overflow-y: auto;
    padding: 10px;
    background-color: rgba(0, 0, 0, 0.2);
    border-radius: 8px;
  }
  
  .chat-message {
    background-color: rgba(255, 255, 255, 0.2);
    padding: 10px;
    border-radius: 8px;
    text-align: center;
    font-size: 1rem;
    /* 두 애니메이션 효과 적용 */
    animation: fadeInUp 0.5s ease-out, fadeInEffect 0.5s ease-out;
  }
  
  .alert-message {
    text-align: center;
    margin-top: 10px;
    font-size: 1.1rem;
    color: #ffeb3b;
  }
  
  /* 반응형: 화면이 좁아질 때 폰트 사이즈 조정 */
  @media (max-width: 768px) {
    .chat-message {
      font-size: 0.9rem;
    }
  }
  </style>
  