<template>
  <div>
    <!-- 슬라이더 상단 제목 -->
    <h2 class="text-4xl font-bold text-white mb-6">신규 클립</h2>

    <!-- 내비 버튼 가시성 강화를 위한 래퍼 -->
    <div class="clip-swiper group h-80 py-6">
      <Swiper
        :modules="[Navigation, Pagination, A11y]"
        slides-per-view="auto"
        centeredSlides
        grabCursor
        navigation
        :pagination="{ clickable: true }"
        :initial-slide="3"
        class="h-full"
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
          rel="noopener noreferrer"
          class="block w-full h-full"
          @click="trackSliderClick(clip)"
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation, Pagination, A11y } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/css/pagination'
import defaultImg from '@/assets/default_thumbnail.svg'

const emit = defineEmits(['loaded-video-ids'])
const sliderClips = ref([])

async function fetchSliderClips() {
  const { data } = await api.get('/api/channels/clip', { params: { page: 0, size: 7 } })

  // 각 채널의 첫번째 recentVideos만 뽑기
  sliderClips.value = data
    .map(c => {
      const v = c.recentVideos?.[0]
      return v
        ? {
            videoId: v.videoId,
      thumbnailUrl: v.thumbnailUrl,
      channelName: c.channelName,
      channelId: c.channelId,
      videoTitle: v.videoTitle
          }
        : null
    })
    .filter(Boolean)
  emit('loaded-video-ids', sliderClips.value.map(clip => clip.videoId))
}

async function trackSliderClick(clip) {
  try {
    await api.post('/api/metrics/video-click', {
      videoId: clip.videoId,
      videoTitle: clip.videoTitle,
      category: 'clip',
      channelName: clip.channelName || '',
      channelId: clip.channelId || ''
    })
  } catch (e) {
    console.error('trackSliderClick error:', e)
  }
}

function getHighRes(url) {
  return url ? url.replace(/default\.jpg$/, 'maxresdefault.jpg') : defaultImg
}

onMounted(fetchSliderClips)
</script>
<style scoped>
/* 래퍼에서 오버레이/버튼 스타일 제어 */
.clip-swiper {
  position: relative;
}
/* 좌/우 명암 오버레이(hover 시 강조) */
.clip-swiper::before,
.clip-swiper::after {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  width: 72px;
  pointer-events: none;
  z-index: 5;              /* 슬라이드 위, 버튼 아래 */
  opacity: 0;
  transition: opacity .25s ease;
}
.clip-swiper::before {
  left: 0;
  background: linear-gradient(90deg, rgba(0,0,0,.35), transparent);
}
.clip-swiper::after {
  right: 0;
  background: linear-gradient(270deg, rgba(0,0,0,.35), transparent);
}
.clip-swiper:hover::before,
.clip-swiper:hover::after {
  opacity: 1;
}

/* Swiper 기본 내비 버튼 커스텀(:deep으로 내부 클래스 타겟) */
.clip-swiper :deep(.swiper-button-prev),
.clip-swiper :deep(.swiper-button-next) {
  z-index: 10;
  width: 44px;
  height: 44px;
  border-radius: 9999px;
  border: 2px solid rgba(255,255,255,0.95);       /* 흰색 테두리 강조 */
  background: rgba(0,0,0,0.28);                   /* 살짝 어두운 배경 */
  backdrop-filter: blur(2px);
  box-shadow: 0 4px 10px rgba(0,0,0,0.35);
  opacity: .9;
  transition: opacity .2s ease, transform .1s ease;
}
.clip-swiper :deep(.swiper-button-prev:hover),
.clip-swiper :deep(.swiper-button-next:hover) {
  opacity: 1;
  transform: scale(1.03);
}
.clip-swiper :deep(.swiper-button-prev::after),
.clip-swiper :deep(.swiper-button-next::after) {
  font-size: 18px;                                   /* 화살표 아이콘 크기 */
  -webkit-text-stroke: 2px rgba(255,255,255,0.9);    /* 화살표 외곽선(화이트) */
  text-shadow: 0 0 6px rgba(0,0,0,0.6);              /* 가독성 향상 */
  color: white;
}
</style>
