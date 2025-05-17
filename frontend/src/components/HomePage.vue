<template>
  <div class="container mx-auto px-4 lg:px-8">
    <TabNavigation :selectedTab="selectedTab" @update:tab="selectedTab=$event"/>
    <LiveStatus      class="my-4"/>
    <!-- Swiper 슬라이더로 채널 프리뷰 -->
    <ChannelSlider   :selectedTab="selectedTab" class="mb-8"/>
    <ChannelList :selectedTab="selectedTab" :key="selectedTab"/>
  </div>
</template>
<script setup>
import { ref }      from 'vue';
import TabNavigation from './TabNavigation.vue';
import LiveStatus    from './LiveStatus.vue';
import ChannelList   from './ChannelList.vue';
import ChannelSlider from './ChannelSlider.vue';  // 새로 추가
const selectedTab = ref('clip');
</script>