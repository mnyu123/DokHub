<template>
  <div class="alert" :class="liveOn ? 'alert-success' : 'alert-error'">
    <i :class="liveOn ? 'fa-solid fa-signal' : 'fa-solid fa-circle-xmark'"></i>
    <span>{{ liveOn ? '방송 중 (On Air)' : '지금은 오프라인입니다' }}</span>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import axios from 'axios';

const liveOn  = ref(false);
const loading = ref(false);
let   timerId = null;

async function fetchLive(){
  loading.value = true;
  try{
    const { data } = await axios.get('/api/live/status');
    liveOn.value = data.livestatus === 'on';
  }catch(e){ console.error(e);}
  finally{ loading.value=false; }
}

onMounted(() => {
  fetchLive();
  timerId = setInterval(fetchLive, 3600000); // 1h
});
onUnmounted(() => clearInterval(timerId));
</script>