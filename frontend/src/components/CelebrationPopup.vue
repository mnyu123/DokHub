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
  
  <style scoped>
  .popup-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5); /* 반투명 검은 오버레이 */
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }
  
  .popup-content {
    background-color: #fff;
    color: #000; /* 명시적으로 텍스트 색상을 검은색으로 지정 */
    border-radius: 8px;
    padding: 20px;
    text-align: center;
    max-width: 400px;
    width: 90%;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
  }
  
  .popup-image {
    max-width: 100%;
    height: auto;
    margin-bottom: 15px;
  }
  
  .close-button {
    margin-top: 15px;
    padding: 8px 16px;
    border: none;
    background-color: #0d6efd;
    color: #fff;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .close-button:hover {
    background-color: #0b5ed7;
  }
  </style>
  