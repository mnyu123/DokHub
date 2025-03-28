<!-- 업데이트 박스 영역(업데이트 내역 리스트)을 담당 -->
<template>
    <div class="update-box mt-3 p-3">
      <h2 class="update-title">v0.8 업데이트 내역</h2>
      <ul class="update-list">
        <li>아이콘 변경</li>
        <li>아이콘 클릭시 독허브 사이트 이동</li>
        <li>카테고리 변경 (클립(활성화), 클립(중단), 리플레이, 노래, 본채널)</li>
        <li>독쌤 방송중인지 확인 가능</li>
      </ul>
    </div>
  </template>
  
  <script>
  export default {
    name: "UpdateBox",
  };
  </script>
  
  <style scoped>
  .update-box {
    background-color: #141414;
    color: #fff;
    border: 1px solid #444;
    border-radius: 8px;
    transition: transform 0.3s ease;
  }
  .update-box:hover {
    transform: translateY(-5px);
  }
  .update-title {
    font-size: 1.2rem;
    margin-bottom: 10px;
  }
  .update-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }
  </style>
  