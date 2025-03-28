<template>
    <header :class="[headerClass, 'p-3', 'mb-3', 'd-flex', 'align-items-center', 'justify-content-between']">
      <a href="http://localhost:3000/" class="d-flex align-items-center text-decoration-none">
        <!-- https://dokhub-love-doksaem.netlify.app/ 배포할땐 주소 바꿔요 -->
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
      <button class="btn theme-btn" @click="$emit('toggle-theme')">
        {{ theme === 'dark' ? '라이트 모드' : '다크 모드' }}
      </button>
    </header>
  </template>
  
  <script>
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
  };
  </script>
  
  <style scoped>
  /* dark 모드일 때 헤더에 적용할 배경색 정의 */
  .custom-header-dark {
    background-color: #141414; /* 원하는 검은색 또는 다크 색상 지정 */
  }
  
  /* 기존 애니메이션 (필요한 경우 유지) */
  .header-anim {
    animation: slideDown 0.3s ease-out;
    transition: background-color 0.1s ease;
  }
  @keyframes slideDown {
    from { opacity: 1; transform: translateY(-10px); }
    to { opacity: 1; transform: translateY(0); }
  }
  
  /* 로고 애니메이션은 유지 (원하는 경우 제거 가능) */
  .logo-anim {
    animation: fadeIn 1s ease-in-out;
  }
  @keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
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
  </style>
  