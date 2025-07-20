<template>
  <header class="navbar bg-base-100 shadow-md px-6">
    <!-- 햄버거 토글 (작게) -->
    <label for="drawer-left" class="btn btn-ghost lg:hidden p-2 mr-4">
      <i class="fa-solid fa-bars text-xl"></i>
    </label>

    <!-- 로고 (클릭하면 전체 리로드) -->
    <!-- 개발 중 -->
    <a href="http://localhost:3000" class="flex items-center gap-2">
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

    <!-- 우측 여유 -->
    <div class="flex-1"></div>
  </header>
</template>

<script setup>
defineProps({ theme: String });
</script>