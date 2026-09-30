<template>
  <nav class="grid grid-cols-5 gap-2 md:hidden" aria-label="바로가기">
    <a
      v-for="(link, idx) in fixedLinks"
      :key="idx"
      :href="link.href"
      target="_blank"
      rel="noopener noreferrer"
      class="flex min-w-0 flex-col items-center justify-center gap-1.5 rounded-xl bg-base-100 px-1 py-2 text-center"
      :aria-label="link.alt"
    >
      <img :src="link.imgSrc" :alt="link.alt" class="h-10 w-10 object-contain" loading="lazy" />
      <span class="text-[11px] leading-tight whitespace-nowrap">{{ link.label }}</span>
    </a>
  </nav>

  <Swiper
    :modules="[Pagination]"
    :breakpoints="{
      0:   { slidesPerView: 4.2, spaceBetween: 12, allowTouchMove: true  },
      360: { slidesPerView: 4.8, spaceBetween: 14, allowTouchMove: true  },
      480: { slidesPerView: 5.2, spaceBetween: 16, allowTouchMove: true  },
      768: { slidesPerView: 5,   spaceBetween: 32, allowTouchMove: false }
    }"
    slides-per-view="5"
    space-between="32"
    :pagination="{ clickable: true }"
    initialSlide="2"
    :allow-touch-move="false"
    grabCursor
    class="hidden md:block py-4 pb-16 mb-8 overflow-visible relative z-10"
    style="overflow: visible;"
  >
    <SwiperSlide
      v-for="(link, idx) in fixedLinks"
      :key="idx"
      class="flex justify-center pb-8 overflow-visible"
      style="overflow: visible;"
    >
      <a
        :href="link.href"
        target="_blank"
        rel="noopener noreferrer"
        class="tooltip tooltip-bottom"
        :data-tip="link.alt"
      >
        <img
          :src="link.imgSrc"
          :alt="link.alt"
          class="object-contain transition-transform w-16 h-16 sm:w-20 sm:h-20 md:w-28 md:h-28"
          loading="lazy"
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
    imgSrc: require('@/assets/chzzk_ico.png'),
    alt:    '독케익 치지직 방송',
    label:  '치지직'
  },
  {
    href: 'https://cafe.naver.com/poisoncake',
    imgSrc: require('@/assets/cafe512.png'),
    alt:    '개떡이 수용소(팬 카페)',
    label:  '팬 카페'
  },
  {
    href: 'https://sharp-scarer-41d.notion.site/1b8d01855ab181989094c50446305706?v=1b8d01855ab180eeb7e5000cac6e922d',
    imgSrc: require('@/assets/dok_fanArt_ico.png'),
    alt:    '독케익 예술의 전당',
    label:  '팬아트'
  },
  {
    href: 'https://thunder-plant-4a0.notion.site/d7cdf50c1aeb4385a4945057caddaf8b',
    imgSrc: require('@/assets/dok_game_lib_ico2.png'),
    alt:    '독케익 종합게임 라이브러리',
    label:  '게임'
  },
  {
    href: 'https://www.youtube.com/@%EB%8F%85%EC%BC%80%EC%9D%B5%EC%9C%A0%ED%8A%9C%EB%B8%8C',
    imgSrc: require('@/assets/yt1.png'),
    alt:    '독케익 유튜브',
    label:  '유튜브'
  }
]
</script>
