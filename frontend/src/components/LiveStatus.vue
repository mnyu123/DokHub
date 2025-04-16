<template>
  <div class="live-status" :class="{ 'live-on': liveOn, 'live-off': !liveOn }">
    <h3>독케익 라이브 상태</h3>
    <div v-if="loading">
      <p>상태를 불러오는 중...</p>
    </div>
    <div v-else>
      <p>{{ liveStatusText }}</p>
      <p v-if="liveOn" class="extra-info">
        왼쪽 아이콘을 누르시면 채널로 이동합니다.
      </p>
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
        // 응답 예시: { livestatus: "on" } 또는 { livestatus: "off" }
        this.liveOn = response.data.livestatus === "on";
      } catch (error) {
        console.error("라이브 상태 조회 실패:", error);
      } finally {
        this.loading = false;
      }
    },
  },
  mounted() {
    this.fetchLiveStatus();
    // 1시간마다 갱신 (3600000ms)
    this.intervalId = setInterval(this.fetchLiveStatus, 3600000);
  },
  unmounted() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
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
  transition: background-color 0.3s ease, color 0.3s ease;
}

/* 방송 중일 때: 부드러운 연두색 배경과 어두운 녹색 텍스트 */
.live-on {
  background-color: #e6f9ea;
  color: #2c662d;
}

/* 방송 종료일 때: 부드러운 연분홍색 배경과 어두운 적색 텍스트 */
.live-off {
  background-color: #f9e6e6;
  color: #8a2a2a;
}

/* 추가 안내 문구: 폰트 크기 축소 */
.extra-info {
  font-size: 0.8em;
  margin-top: 8px;
  color: inherit;
}
</style>
