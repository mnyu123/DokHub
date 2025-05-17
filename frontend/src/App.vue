<template>
  <!-- ① drawer 컨테이너 -->
  <div :data-theme="theme" class="drawer lg:drawer-open">
    <!-- ② 토글 체크박스 -->
    <input id="drawer-left" type="checkbox" class="drawer-toggle" />

    <!-- ③ 메인 콘텐츠 -->
    <div class="drawer-content flex flex-col">
      <HeaderComponent :theme="theme" @toggle-theme="toggleTheme"/>
      <router-view class="p-4"/>
    </div>

    <!-- ④ 왼쪽 사이드바 -->
    <div class="drawer-side">
      <SidebarMenu :theme="theme" @toggle-theme="toggleTheme"/>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import HeaderComponent from '@/components/HeaderComponent.vue';
import SidebarMenu    from '@/components/SidebarMenu.vue';

const theme = ref(localStorage.getItem('theme') || 'dark');
function toggleTheme(){
  theme.value = theme.value === 'dark' ? 'light' : 'dark';
  localStorage.setItem('theme', theme.value);
}
</script>