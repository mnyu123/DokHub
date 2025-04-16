<!-- 탭 네비게이션 영역(클립, 클립(중단), 리플레이, 노래, 본채널)을 담당 -->
<template>
    <nav class="mb-3 d-flex justify-content-center align-items-center tab-nav">
      <button
        class="tab-btn me-2"
        :class="{ active: selectedTab === 'clip' }"
        @click="$emit('update:tab', 'clip')"
      >
        클립(활성화)
      </button>
      <button
        class="tab-btn me-2"
        :class="{ active: selectedTab === 'stclip' }"
        @click="$emit('update:tab', 'stclip')"
      >
        클립(중단)
      </button>
      <button
        class="tab-btn me-2"
        :class="{ active: selectedTab === 'replay' }"
        @click="$emit('update:tab', 'replay')"
      >
        리플레이
      </button>
      <button
        class="tab-btn me-2"
        :class="{ active: selectedTab === 'song' }"
        @click="$emit('update:tab', 'song')"
      >
        노래
      </button>
      <button
        class="tab-btn"
        :class="{ active: selectedTab === 'main' }"
        @click="$emit('update:tab', 'main')"
      >
        본채널
      </button>
    </nav>
  </template>
  
  <script>
  export default {
    name: "TabNavigation",
    props: {
      selectedTab: {
        type: String,
        required: true,
      },
    },
  };
  </script>
  
  <style scoped>
  .tab-nav {
    gap: 10px;
  }
  .tab-btn {
    padding: 8px 16px;
    border: none;
    background-color: transparent;
    color: inherit;
    font-weight: bold;
    border-radius: 4px;
    transition: background-color 0.3s ease, transform 0.3s ease;
  }
  .tab-btn:hover {
    background-color: rgba(13, 110, 253, 0.2);
    transform: scale(1.05);
  }
  .tab-btn.active {
    background-color: #0d6efd;
    color: white;
    transform: scale(1.05);
  }
  </style>
  