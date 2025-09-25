<template>
  <div class="alert" :class="liveOn ? 'alert-success' : 'alert-error'">
    <i :class="liveOn ? 'fa-solid fa-signal' : 'fa-solid fa-circle-xmark'"></i>
    <span>{{ liveOn ? '방송 중 (On Air)' : '지금은 오프라인입니다' }}</span>
  </div>
</template>

<script setup>
import { reactive, computed, onMounted } from 'vue';
import axios from 'axios';

// 전역 반응형 상태(window에 보관) - JS 전용으로 수정
let state = reactive({ liveOn: false, showToast: false, _last: false });
if (typeof window !== 'undefined') {
  if (window.__DOKHUB_LIVE) {
    state = window.__DOKHUB_LIVE;
  } else {
    window.__DOKHUB_LIVE = state;
  }
  // 라우터에서 호출할 전역 체크 함수
  window.__DOKHUB_CHECK_LIVE = async function () {
    try {
      const res = await axios.get('/api/live/status', { timeout: 4000 });
      const data = res && res.data ? res.data : null;
      const next = data && data.livestatus === 'on';
      if (!state._last && next) {
        state.showToast = true;
        setTimeout(function () { state.showToast = false; }, 3000);
      }
      state._last = next;
      state.liveOn = next;
    } catch (e) {
      console.error('checkLive error:', e);
    }
  };
}

const liveOn = computed(function () { return state.liveOn; });

// 최초 1회 동기화(라우터 호출 전 대비)
onMounted(function () {
  if (typeof window !== 'undefined' && typeof window.__DOKHUB_CHECK_LIVE === 'function') {
    window.__DOKHUB_CHECK_LIVE(true);
  }
});
</script>