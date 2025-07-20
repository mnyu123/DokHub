<template>
  <Swiper
    :modules="[Pagination]"
    slides-per-view="5"
    space-between="32"
    :pagination="{ clickable: true }"
    initialSlide="2"
    :allow-touch-move="false"
    grabCursor
    centeredSlides
    class="py-4 pb-16 mb-8 overflow-visible"
  >
    <SwiperSlide
      v-for="(link, idx) in fixedLinks"
      :key="idx"
      class="flex justify-center pb-8 overflow-visible"
    >
      <a
        :href="link.href"
        target="_blank"
        class="tooltip tooltip-bottom"
        :data-tip="link.alt"
      >
        <img
          :src="link.imgSrc"
          :alt="link.alt"
          :class="[
            'object-contain transition-transform',
            // 기본 사이즈
            'w-16 h-32',
            // 개떡이 수용소만 살짝 크게
            link.alt === '개떡이 수용소(팬 카페)' ? 'w-48 h-32' : ''
          ]"
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
    imgSrc: require('@/assets/chzzk2.png'),
    alt:    '독케익 치지직 방송'
  },
  {
    href: 'https://cafe.naver.com/poisoncake',
    imgSrc: require('@/assets/cafe5.jpg'),
    alt:    '개떡이 수용소(팬 카페)'
  },
  {
    href: 'https://sharp-scarer-41d.notion.site/1b8d01855ab181989094c50446305706?v=1b8d01855ab180eeb7e5000cac6e922d',
    imgSrc: require('@/assets/doksd.png'),
    alt:    '독케익 예술의 전당'
  },
  {
    href: 'https://thunder-plant-4a0.notion.site/d7cdf50c1aeb4385a4945057caddaf8b',
    imgSrc: require('@/assets/doksd2.png'),
    alt:    '독케익 종합게임 라이브러리'
  },
  {
    href: 'https://www.youtube.com/@%EB%8F%85%EC%BC%80%EC%9D%B5%EC%9C%A0%ED%8A%9C%EB%B8%8C',
    imgSrc: require('@/assets/youtube_logo.png'),
    alt:    '독케익 유튜브'
  }
]
</script>