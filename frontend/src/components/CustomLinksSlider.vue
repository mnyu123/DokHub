<template>
  <Swiper
    :modules="[Pagination]"
    slides-per-view="5"
    space-between="48"
    :pagination="{ clickable: true }"
    class="h-48 py-8 mb-8"
  >
    <SwiperSlide
      v-for="(link, idx) in fixedLinks"
      :key="idx"
      class="rounded-lg overflow-visible shadow tooltip tooltip-bottom"
      :data-tip="link.alt"
    >
      <a :href="link.href" target="_blank" class="block w-full h-40">
        <img
          :src="link.imgSrc"
          class="w-full h-full object-cover"
          :alt="link.alt"
        />
      </a>
    </SwiperSlide>
  </Swiper>
</template>

<script setup>
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Pagination }            from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/pagination'

const fixedLinks = [
  {
    href: 'https://chzzk.naver.com/b68af124ae2f1743a1dcbf5e2ab41e0b',
    imgSrc: require('@/assets/cafe4.jpg'),
    alt:    '독케익 치지직 방송'
  },
  {
    href: 'https://cafe.naver.com/poisoncake',
    imgSrc: require('@/assets/cafe5.jpg'),
    alt:    '개떡이 수용소'
  },
  {
    href: 'https://sharp-scarer-41d.notion.site/1b8d01855ab181989094c50446305706?v=1b8d01855ab180eeb7e5000cac6e922d',
    imgSrc: require('@/assets/dokart.png'),
    alt:    '독케익 예술의 전당'
  },
  {
    href: 'https://thunder-plant-4a0.notion.site/d7cdf50c1aeb4385a4945057caddaf8b',
    imgSrc: require('@/assets/pngwing.com.png'),
    alt:    '독케익 종합게임 라이브러리'
  },
  {
    href: 'https://www.notion.so/1e9111b4b3f780e18878e3b0750b6c6f?v=1e9111b4b3f7803abc5e000cf6fb18ca',
    imgSrc: require('@/assets/pngwing.com.png'),
    alt:    '독케익 방송일정'
  }
]
</script>