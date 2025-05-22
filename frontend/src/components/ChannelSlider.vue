<template>
  <div class="space-y-6">
    <!-- 추천 채널 슬라이더 -->
    <Swiper
      :slides-per-view="auto"
      centeredSlides
      grabCursor
      class="h-48"
    >
      <SwiperSlide
        v-for="c in channels"
        :key="c.channelId"
        class="relative rounded-xl overflow-hidden"
      >
        <img
          :src="getHighRes(c.thumbnailUrl)"
          @error="$event.target.src = c.thumbnailUrl"
          class="w-full h-full object-cover"
          alt="채널 썸네일"
        />
        <div class="absolute inset-0 bg-black/30 flex items-center justify-center">
          <h3 class="text-white text-lg font-semibold">{{ c.channelName }}</h3>
        </div>
      </SwiperSlide>
    </Swiper>

    <!-- 최근 업로드 슬라이더 -->
    <Swiper :slides-per-view="3" spaceBetween="10" class="h-32">
      <SwiperSlide
        v-for="c in channels"
        :key="c.channelId"
      >
        <img
          :src="getHighRes(c.recentVideos[0]?.thumbnailUrl)"
          @error="$event.target.src = c.recentVideos[0]?.thumbnailUrl"
          class="w-full h-full object-cover rounded-lg"
          alt="최근 영상 썸네일"
        />
      </SwiperSlide>
    </Swiper>
  </div>
</template>

<script setup>
import { Swiper, SwiperSlide } from 'swiper/vue';
import 'swiper/css';
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';

const props = defineProps({ selectedTab: String });
const channels = ref([]);

async function fetchChannels() {
  try {
    const { data } = await axios.get(
      `/api/channels/${props.selectedTab}`,
      { params: { page: 0, size: 5 } }
    );
    channels.value = data;
  } catch (e) {
    console.error('fetchChannels slider error:', e);
  }
}

function getHighRes(url) {
  return url.replace(/default\.jpg$/, 'maxresdefault.jpg');
}

onMounted(fetchChannels);
watch(() => props.selectedTab, fetchChannels);
</script>