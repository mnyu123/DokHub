<template>
  <Swiper
    :modules="[Pagination]"
    slides-per-view="5"
    spaceBetween="10"
    :pagination="{ clickable: true }"
    class="h-64 mb-8"
  >
    <SwiperSlide v-for="(link, idx) in fixedLinks" :key="idx">
      <a :href="link.href" target="_blank">
        <img :src="link.imgSrc" :alt="link.alt" class="w-full h-full object-cover rounded-lg" />
      </a>
    </SwiperSlide>
  </Swiper>
</template>

<script setup>
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Pagination } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/pagination'

const fixedLinks = [
  {
    href: 'https://chzzk.naver.com/b68af124ae2f1743a1dcbf5e2ab41e0b',
    imgSrc: require('@/assets/dokchzzk.png'),      // Vue CLI에서 assets 폴더 이미지 임포트 :contentReference[oaicite:6]{index=6}
    alt:    '독케익 치지직 방송'
  },
  {
    href: 'https://cafe.naver.com/poisoncake',
    imgSrc: require('@/assets/cafe1.png'), // 나중에 허락받으면 팬아트로 교체 대상 : https://cafe.naver.com/poisoncake?iframe_url_utf8=%2FArticleRead.nhn%253Fclubid%3D30861172%2526articleid%3D35878%2526commentFocus%3Dtrue
    alt:    '개떡이 수용소'
  },
  {
    href: 'https://sharp-scarer-41d.notion.site/1b8d01855ab181989094c50446305706?v=1b8d01855ab180eeb7e5000cac6e922d',
    imgSrc: require('@/assets/pngwing.com.png'),
    alt:    '독케익 팬아트 검색 사이트'
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
