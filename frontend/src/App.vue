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
        class="fixed bottom-6 right-6 p-4 bg-primary text-white rounded-full shadow-lg hover:bg-primary-focus transition z-[95]"
      >
        <svg
          aria-hidden="true"
          viewBox="0 0 24 24"
          class="w-5 h-5"
          fill="none"
          stroke="currentColor"
          stroke-width="1.8"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path d="M21 12a8 8 0 0 1-8 8H6l-4 2 1.4-4.2A9 9 0 1 1 21 12Z" />
        </svg>
      </button>

      <!-- LIVE FAB (방송중일 때만) -->
      <button
        v-if="liveStatus.liveOn"
        @click="goLive"
        class="fixed bottom-20 right-6 p-4 bg-red-500 text-white rounded-full shadow-lg animate-pulse transition z-[100]"
      >
        LIVE
      </button>

      <!-- 방송중 토스트 배너 -->
      <div
        v-if="showToast"
        class="fixed top-4 inset-x-0 flex justify-center z-[70]"
      >
        <div class="bg-green-500 text-white px-4 py-2 rounded shadow-lg">
          방송 중입니다! <button @click="goLive" class="underline">시청하기</button>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import HeaderComponent from '@/components/HeaderComponent.vue';
import SidebarMenu     from '@/components/SidebarMenu.vue';
import FooterComponent from '@/components/FooterComponent.vue';
import { liveStatus, refreshLiveStatus } from '@/state/liveStatus';

const router = useRouter();
const theme = ref(localStorage.getItem('theme') || 'dark');
function toggleTheme() {
  theme.value = theme.value === 'dark' ? 'light' : 'dark';
  localStorage.setItem('theme', theme.value);
}

function goChat() {
  router.push('/chat/dokchat');
}
function goLive() {
  window.open('https://chzzk.naver.com/b68af124ae2f1743a1dcbf5e2ab41e0b', '_blank', 'noopener,noreferrer');
}

const showToast = ref(false);
let liveTimer;
let toastTimer;

async function fetchLiveStatus() {
  const { started } = await refreshLiveStatus();
  if (started) {
    showToast.value = true;
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => (showToast.value = false), 3000);
  }
}

onMounted(() => {
  fetchLiveStatus();
  liveTimer = setInterval(fetchLiveStatus, 60000);
});

onUnmounted(() => {
  clearInterval(liveTimer);
  clearTimeout(toastTimer);
});
</script>
