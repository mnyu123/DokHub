<template>
  <div v-if="showPopup" class="popup-overlay">
    <div class="popup-content">
      <img src="@/assets/dok3.gif" alt="Chzzk Partner" class="popup-image" />
      <h2>독케익 복귀</h2>
      <p>{{ displayText }}</p>
      <button class="close-button" @click="closePopup">닫기</button>
    </div>
  </div>
</template>

<script>
export default {
  name: "CelebrationPopup",
  data() {
    return {
      showPopup: true,                     // 팝업 표시 여부
      daysRemaining: this.calculateDays(), // 남은 일수 초기값
      timerId: null                        // 타이머 ID (정리용)
    };
  },
  computed: {
    // 남은 일수가 1 이상이면 D-#, 0 이하면 환영 문구
    displayText() {
      return this.daysRemaining > 0
        ? `D-${this.daysRemaining}`
        : '독쌤 반가워~~~';
    }
  },
  methods: {
    closePopup() {
      this.showPopup = false;
    },
    // 오늘 자정 기준으로 목표일까지 남은 일수를 계산
    calculateDays() {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      // 2025년 7월 26일 (월은 0부터 시작하므로 6)
      const target = new Date(2025, 6, 26);
      const diffMs = target - today;
      const msPerDay = 1000 * 60 * 60 * 24;
      return Math.floor(diffMs / msPerDay);
    },
    // 다음 자정이 되면 daysRemaining 을 다시 계산하고, 또 예약
    scheduleNext() {
      const now = new Date();
      // 내일 자정 시각 생성
      const nextMidnight = new Date(
        now.getFullYear(),
        now.getMonth(),
        now.getDate() + 1,
        0, 0, 0, 0
      );
      const msUntilMidnight = nextMidnight - now;
      this.timerId = setTimeout(() => {
        this.daysRemaining = this.calculateDays();
        this.scheduleNext();
      }, msUntilMidnight);
    }
  },
  mounted() {
    // 컴포넌트 마운트 시 자정 업데이트 예약 시작
    this.scheduleNext();
  },
  beforeUnmount() {
    // 언마운트 시 타이머 정리
    if (this.timerId) {
      clearTimeout(this.timerId);
    }
  }
};
</script>

<style scoped>
.popup-overlay {
  position: fixed; top: 0; left: 0;
  width: 100%; height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000;
}
.popup-content {
  background-color: #fff; color: #000;
  border-radius: 8px; padding: 20px; text-align: center;
  max-width: 400px; width: 90%;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
}
.popup-image {
  max-width: 100%; height: auto; margin-bottom: 15px;
}
.close-button {
  margin-top: 15px; padding: 8px 16px;
  border: none; background-color: #0d6efd; color: #fff;
  border-radius: 4px; cursor: pointer;
  transition: background-color 0.3s ease;
}
.close-button:hover {
  background-color: #0b5ed7;
}
</style>