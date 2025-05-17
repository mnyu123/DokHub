<template>
  <div class="w-full max-w-xl mx-auto bg-base-200 p-6 rounded-xl shadow-md">
    <!-- 새로고침 -->
    <div class="flex justify-end items-center gap-2 mb-2">
      <button class="btn btn-sm" @click="refreshChat">새로고침</button>
      <span class="text-sm opacity-60">({{ refreshCount }}/{{ refreshLimit }})</span>
    </div>

    <!-- 로딩 -->
    <div v-if="loading" class="flex flex-col items-center my-10">
      <span class="loading loading-spinner loading-lg"></span>
      <p class="mt-4">채팅 내역을 불러오는 중입니다…</p>
    </div>

    <!-- 메시지 -->
    <div v-else class="flex flex-col gap-3 max-h-96 overflow-y-auto">
      <div v-for="(m,i) in messages" :key="i" class="chat chat-start">
        <div class="chat-header">독케익</div>
        <div class="chat-bubble">{{ m }}</div>
      </div>

      <!-- 안내 -->
      <p v-if="messages.length===0" class="text-center text-warning mt-6">
        채팅 내역이 없습니다. 새로고침해 보세요!
      </p>
      <p v-else-if="messages.length<5" class="text-center text-warning mt-6">
        채팅이 부족합니다. 새로고침해 보세요!
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const messages      = ref([]);
const loading       = ref(true);
const refreshCount  = ref(0);
const refreshLimit  = 5;

async function fetchChat(){
  loading.value = true;
  try{
    const { data } = await axios.get('/api/chat/history');
    messages.value = data;
  }catch(e){ console.error(e);}
  finally{ loading.value=false; }
}
async function refreshChat(){
  if(refreshCount.value >= refreshLimit){
    alert('새로고침이 너무 잦습니다. 잠시 후 다시!');
    return;
  }
  refreshCount.value++;
  await fetchChat();
}

onMounted(fetchChat);
</script>