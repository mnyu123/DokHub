<template>
  <div :data-theme="theme" class="drawer lg:drawer-open">
    <input id="drawer-left" type="checkbox" class="drawer-toggle" />

    <!-- 메인 콘텐츠 -->
    <div class="drawer-content flex flex-col min-h-screen">
      <HeaderComponent :theme="theme" @toggle-theme="toggleTheme" />
      <router-view class="flex-1 p-4" />
      <FooterComponent :isDark="theme==='dark'" />
    </div>

    <!-- 좌측 사이드바 -->
    <div class="drawer-side">
      <SidebarMenu :theme="theme" @toggle-theme="toggleTheme" />
    </div>

    <!-- 플로팅 액션 버튼 & LIVE 버튼 & 토스트 -->
    <Teleport to="body">
      <!-- 독채팅 FAB -->
      <button
        @click="goChat"
        class="fixed bottom-6 right-6 p-4 bg-primary text-white rounded-full shadow-lg hover:bg-primary-focus transition"
      >
        <i class="fa-solid fa-message"></i>
      </button>

      <!-- LIVE FAB (방송중일 때만) -->
      <button
        v-if="liveOn"
        @click="goLive"
        class="fixed bottom-20 right-6 p-4 bg-red-500 text-white rounded-full shadow-lg animate-pulse transition"
      >
        LIVE
      </button>

      <!-- 방송중 토스트 배너 -->
      <div
        v-if="showToast"
        class="fixed top-4 inset-x-0 flex justify-center pointer-events-none"
      >
        <div class="bg-green-500 text-white px-4 py-2 rounded shadow-lg">
          방송 중입니다! <button @click="goLive" class="underline">시청하기</button>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import HeaderComponent from '@/components/HeaderComponent.vue';
import SidebarMenu     from '@/components/SidebarMenu.vue';
import FooterComponent from '@/components/FooterComponent.vue';

const theme = ref(localStorage.getItem('theme') || 'dark');
function toggleTheme() {
  theme.value = theme.value === 'dark' ? 'light' : 'dark';
  localStorage.setItem('theme', theme.value);
}

function goChat() {
  window.location.href = '/chat/dokchat';
}
function goLive() {
  window.open('https://chzzk.naver.com/live/b68af124ae2f1743a1dcbf5e2ab41e0b', '_blank');
}

const liveOn = ref(false);
const showToast = ref(false);

async function fetchLiveStatus() {
  try {
    const { data } = await axios.get('/api/live/status');
    liveOn.value = data.livestatus === 'on';
    if (liveOn.value) {
      showToast.value = true;
      setTimeout(() => (showToast.value = false), 3000);
    }
  } catch (e) {
    console.error('fetchLiveStatus error:', e);
  }
}

onMounted(() => {
  fetchLiveStatus();
  setInterval(fetchLiveStatus, 60000);
});
</script>