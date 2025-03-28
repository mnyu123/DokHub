<!-- 좌측 사이드바 -->
<template>
    <div class="text-center mb-3 mb-md-0">
      <a href="https://chzzk.naver.com/b68af124ae2f1743a1dcbf5e2ab41e0b" target="_blank">
        <img src="@/assets/right.gif" alt="좌측GIF" class="img-fluid left-gif" />
      </a>
    </div>
  </template>
  
  <script>
  export default {
    name: "LeftSidebar",
  };
  </script>
  
  <style scoped>
  .left-gif {
    transition: transform 0.3s ease;
  }
  .left-gif:hover {
    transform: scale(1.05);
  }
  </style>
  