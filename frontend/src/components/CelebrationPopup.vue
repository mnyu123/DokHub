<template>
    <div v-if="showPopup" class="popup-overlay">
      <div class="popup-content">
        <img src="@/assets/chzzk_partner.png" alt="Chzzk Partner" class="popup-image" />
        <h2>축하합니다!</h2>
        <p>독케익 치지직 파트너 달성!</p>
        <button class="close-button" @click="closePopup">닫기</button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "CelebrationPopup",
    data() {
      return {
        showPopup: true, // 초기에는 팝업을 표시합니다.
      };
    },
    methods: {
      closePopup() {
        this.showPopup = false;
      },
    },
  };
  </script>