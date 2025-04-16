<!-- 업데이트 박스 영역(업데이트 내역 리스트)을 담당 -->
<template>
    <div class="update-box mt-3 p-3">
      <h2 class="update-title">v0.9 업데이트 내역</h2>
      <ul class="update-list">
        <li>독채팅(Beta) 신규추가</li>
        <li>업로드중단 채널 탭 변경</li>
        <li>이미지 변경</li>
        <li>하단 공지내용 추가</li>
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
  