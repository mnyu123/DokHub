<template>
  <div>
    <!-- 슬라이더 상단 제목 -->
    <h2 class="text-4xl font-bold text-white mb-6">이번주 최신순 클립</h2>

    <Swiper
      :modules="[Navigation, Pagination, A11y]"
      slides-per-view="auto"
      centeredSlides
      grabCursor
      navigation
      :pagination="{ clickable: true }"
      :initial-slide="3"
      class="h-80 py-6"
    >
      <SwiperSlide
        v-for="clip in sliderClips"
        :key="clip.videoId"
        class="mx-3 flex-shrink-0 overflow-hidden rounded-lg shadow-md"
        style="width: 300px;"
      >
        <a
          :href="`https://youtu.be/${clip.videoId}`"
          target="_blank"
          class="block w-full h-full"
        >
          <img
            :src="getHighRes(clip.thumbnailUrl)"
            @error="$event.target.src = defaultImg"
            class="w-full h-48 object-cover rounded-t-md"
            :alt="clip.videoTitle"
          />
          <div class="p-3 bg-base-100">
            <p class="font-semibold truncate">{{ clip.videoTitle }}</p>
            <p class="text-sm text-gray-500 truncate">{{ clip.channelName }}</p>
          </div>
        </a>
      </SwiperSlide>
    </Swiper>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation, Pagination, A11y } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/css/pagination'
import defaultImg from '@/assets/doksame3.gif'

const props = defineProps({ selectedTab: String })
const sliderClips = ref([])

async function fetchSliderClips() {
  /*
  // 일주일 새 올라온 카테고리=clip 채널 7개 호출
  const { data } = await axios.get(
    `http://localhost:8080/api/channels/clip`,
    { params: { page: 0, size: 7 } }
  )  */
    // 일주일 새 올라온 카테고리=clip 채널 7개 호출
    // API 호출 (prod/dev 양쪽 모두 주석 형태로 유지)
    const url = `/api/channels/clip`  // production
    // const url = `http://localhost:8080/api/channels/clip`  // dev
    const { data } = await axios.get(url, { params: { page: 0, size: 7 } })
    
  // 각 채널의 첫번째 recentVideos만 뽑기
  sliderClips.value = data
    .map(c => {
      const v = c.recentVideos?.[0]
      return v
        ? {
            videoId: v.videoId,
            thumbnailUrl: v.thumbnailUrl,
            channelName: c.channelName,
            videoTitle: v.videoTitle
          }
        : null
    })
    .filter(Boolean)
}

function getHighRes(url) {
  return url.replace(/default\.jpg$/, 'maxresdefault.jpg')
}

onMounted(fetchSliderClips)
watch(() => props.selectedTab, fetchSliderClips)
</script>