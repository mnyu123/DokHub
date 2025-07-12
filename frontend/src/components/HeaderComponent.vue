<template>
  <header class="navbar bg-base-100 shadow-md">
    <!-- 햄버거 → drawer 토글 -->
    <label for="drawer-left" class="btn btn-ghost lg:hidden">
      <i class="fa-solid fa-bars"></i>
    </label>

    <!-- 가운데 로고 -->
    <router-link to="/" class="flex items-center gap-2">
      <img src="@/assets/dokhublogo.png" class="h-12 w-auto logo-anim" />
      <span class="text-2xl font-bold">독허브</span>
    </router-link>
  </header>
</template>

<script setup>
defineProps({ theme:String });
</script>