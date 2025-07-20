<template>
  <Swiper
    :modules="[Navigation, Pagination, A11y]"
    slides-per-view="auto"
    centeredSlides
    grabCursor
    navigation
    :pagination="{ clickable: true }"
    class="h-72 py-4"
  >
    <SwiperSlide
      v-for="c in channels"
      :key="c.channelId"
      class="relative rounded-xl overflow-hidden mx-2"
    >
      <a :href="c.channelLink" target="_blank" class="block w-full h-full">
        <img
          :src="getHighRes(c.thumbnailUrl)"
          class="w-full h-full object-cover"
          alt="채널 썸네일"
        />
        <div class="absolute inset-0 bg-black/40 flex items-center justify-center">
          <h3 class="text-white text-lg font-semibold">{{ c.channelName }}</h3>
        </div>
      </a>
    </SwiperSlide>
  </Swiper>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation, Pagination, A11y } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/css/pagination'

const props    = defineProps({ selectedTab: String })
const channels = ref([])

async function fetchChannels() {
  const url = `http://localhost:8080/api/channels/${props.selectedTab}?page=0&size=7`
  channels.value = (await axios.get(url)).data
}
function getHighRes(url) {
  return url.replace(/default\.jpg$/, 'maxresdefault.jpg')
}

onMounted(fetchChannels)
watch(() => props.selectedTab, fetchChannels)
</script>