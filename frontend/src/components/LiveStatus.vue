<!-- src/components/LiveStatus.vue -->
<template>
    <div class="live-status">
      <h3>독케익 라이브 상태</h3>
      <div v-if="loading">
        <p>상태를 불러오는 중...</p>
      </div>
      <div v-else>
        <p>{{ liveStatusText }}</p>
      </div>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    name: "LiveStatus",
    data() {
      return {
        liveOn: false,
        loading: false,
      };
    },
    computed: {
      liveStatusText() {
        return this.liveOn ? "방송 중 (on)" : "방송 종료 (off)";
      },
    },
    methods: {
      async fetchLiveStatus() {
        this.loading = true;
        try {
          const response = await axios.get("/api/live/status");
          // 응답 예시: { liveStatus: "on" } 또는 { liveStatus: "off" }
          this.liveOn = response.data.liveStatus === "on";
        } catch (error) {
          console.error("라이브 상태 조회 실패:", error);
        } finally {
          this.loading = false;
        }
      },
    },
    mounted() {
      this.fetchLiveStatus();
      // 필요 시 주기적으로 라이브 상태를 갱신할 수 있습니다.
      // setInterval(this.fetchLiveStatus, 30000); // 30초마다 갱신
    },
  };
  </script>
  
  <style scoped>
  .live-status {
    border: 1px solid #ccc;
    padding: 16px;
    border-radius: 8px;
    text-align: center;
    margin-bottom: 20px;
  }
  </style>
  