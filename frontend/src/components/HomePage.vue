<template>
  <main class="container mx-auto px-4 sm:px-6 lg:px-12 space-y-6 lg:space-y-10 py-5 lg:py-8">
    <LiveStatus />

    <ChannelSlider @loaded-video-ids="sliderVideoIds = $event" />

    <CustomLinksSlider />

    <ClickAnalyticsCard />

    <TabNavigation :selectedTab="selectedTab" @update:tab="selectedTab=$event" />

    <ChannelList
      :selectedTab="selectedTab"
      :excludedVideoIds="selectedTab === 'clip' ? sliderVideoIds : []"
      :key="selectedTab"
    />
  </main>
</template>

<script setup>
import { ref }            from 'vue'
import TabNavigation      from './TabNavigation.vue'
import LiveStatus         from './LiveStatus.vue'
import ChannelSlider      from './ChannelSlider.vue'
import CustomLinksSlider  from './CustomLinksSlider.vue'
import ClickAnalyticsCard from './ClickAnalyticsCard.vue'
import ChannelList        from './ChannelList.vue'

const selectedTab = ref('clip')
const sliderVideoIds = ref([])
</script>
