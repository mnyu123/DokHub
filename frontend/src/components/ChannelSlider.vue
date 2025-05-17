<template>
  <swiper :slides-per-view="1" :breakpoints="bp" class="w-full h-64">
    <swiper-slide v-for="c in channels" :key="c.channelId" class="relative">
      <img :src="c.thumbnailUrl" class="w-full h-full object-cover rounded-xl" />
      <div class="absolute inset-0 bg-black/40 flex items-end p-4">
        <h3 class="text-white text-xl font-semibold">{{ c.channelName }}</h3>
      </div>
    </swiper-slide>
  </swiper>
</template>

<script setup>
import { Swiper, SwiperSlide } from 'swiper/vue';
import 'swiper/css';

import { ref, onMounted, watch } from 'vue';
import axios from 'axios';

const props = defineProps({ selectedTab: String });

/* ───────────── data ───────────── */
const channels = ref([]);     // 슬라이더에 표시될 채널
const bp = { 640: { slidesPerView: 2 }, 1024: { slidesPerView: 3 } };

/* ───────────── methods ───────────── */
async function fetchChannels() {
  try {
    const { data } = await axios.get(`/api/channels/${props.selectedTab}`, {
      params: { page: 0, size: 5 },
    });
    channels.value = data;
  } catch (err) {
    console.error('ChannelSlider fetch error:', err);
  }
}

/* ───────────── lifecycle & watch ───────────── */
onMounted(fetchChannels);
watch(() => props.selectedTab, fetchChannels);   // 탭 바뀔 때마다 다시 로드
</script>