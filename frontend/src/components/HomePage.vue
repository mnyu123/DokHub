<template>
  <div class="container mx-auto px-4 lg:px-8 space-y-8">
    <TabNavigation 
      :selectedTab="selectedTab" 
      @update:tab="selectedTab = $event" />

    <LiveStatus class="my-4" />

    <!-- 1) 채널 슬라이더 -->
    <ChannelSlider 
      :selectedTab="selectedTab" 
      class="mb-8" />

    <!-- 2) 고정 링크 슬라이더 -->
    <CustomLinksSlider />

    <!-- 3) 채널/클립 리스트 -->
    <ChannelList 
      :selectedTab="selectedTab" 
      :key="selectedTab" />
  </div>
</template>

<script setup>
import { ref }            from 'vue'
import TabNavigation      from './TabNavigation.vue'
import LiveStatus         from './LiveStatus.vue'
import ChannelSlider      from './ChannelSlider.vue'
import CustomLinksSlider  from './CustomLinksSlider.vue'
import ChannelList        from './ChannelList.vue'

const selectedTab = ref('clip')
</script>