<!-- src/components/HeaderComponent.vue -->
<template>
  <header
    class="navbar bg-base-100 shadow-md px-6 flex items-center justify-between overflow-visible"
  >
    <!-- 햄버거 토글 (작게) -->
    <label for="drawer-left" class="btn btn-ghost lg:hidden p-2 mr-4">
      <i class="fa-solid fa-bars text-xl"></i>
    </label>

    <!-- 로고 (클릭하면 전체 리로드) -->
    <!-- 개발 중 -->
    <a href="https://dokhub.site/" class="flex items-center gap-2">
      <img src="@/assets/dokhublogo.png" class="h-8 w-auto logo-anim" />
      <span class="text-xl font-bold">독허브</span>
    </a>
    <!-- 배포 시 -->
    <!--
    <a href="/" class="flex items-center gap-2">
      <img src="@/assets/dokhublogo.png" class="h-8 w-auto logo-anim" />
      <span class="text-xl font-bold">독허브</span>
    </a>
    -->

    <!-- 가운데 빈 공간 확보 -->
    <div class="flex-1"></div>

    <!-- 우측: 업데이트 호버 영역 -->
    <div
      class="relative"
      @mouseenter="hover = true"
      @mouseleave="hover = false"
    >
      <span class="text-sm font-medium cursor-pointer hover:text-primary">
        업데이트
      </span>

      <!-- hover 시에만 보이는 팝업 -->
      <div
        v-if="hover"
        class="absolute right-0 top-full mt-2 w-64 bg-white text-gray-800 p-4 rounded-lg shadow-lg z-50"
      >
        <h3 class="font-semibold mb-2">1.0 업데이트</h3>
        <ul class="list-disc list-inside text-sm space-y-1">
          <li>클립 제공화면 변경</li>
          <li>바로가기 아이콘 추가</li>
          <li>독채팅 아이콘 위치변경</li>
          <li>클립 정렬방식 변경</li>
        </ul>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
defineProps({ theme: String })

// hover 상태 관리
const hover = ref(false)
</script>